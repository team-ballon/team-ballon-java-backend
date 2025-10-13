package com.ballon.global.cache;

import com.ballon.domain.category.entity.Category;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 애플리케이션 기동 시 전체 카테고리를 1회 적재하고,
 * 이후 모든 조회/갱신을 메모리에서 수행한다.
 */
@Component
public class CategoryCacheStore {

    public static final class Node {
        public final Long id;
        public String name;
        public int depth;
        public Long parentId;
        public final NavigableSet<Long> children; // 자동 정렬 TreeSet

        Node(Long id, String name, int depth, Long parentId, Comparator<Long> comparator) {
            this.id = id;
            this.name = name;
            this.depth = depth;
            this.parentId = parentId;
            this.children = new TreeSet<>(comparator);
        }
    }

    private Map<Long, Node> byId = new HashMap<>();
    private Map<Long, NavigableSet<Long>> childrenIndex = new HashMap<>();
        private NavigableSet<Long> rootIds; // TreeSet으로 자동 정렬
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Comparator<Long> nameComparator = Comparator.comparing(id -> byId.get(id).name, String.CASE_INSENSITIVE_ORDER);

    // ===== double-buffered loadAll =====
    public void loadAll(List<Category> all) {
        Map<Long, Node> newById = new HashMap<>();
        Map<Long, NavigableSet<Long>> newChildrenIndex = new HashMap<>();
        NavigableSet<Long> newRootIds = new TreeSet<>(Comparator.comparing(id -> {
            Category c = all.stream().filter(cat -> cat.getCategoryId().equals(id)).findFirst().orElse(null);
            return (c == null) ? "" : c.getName();
        }, String.CASE_INSENSITIVE_ORDER));

        // 1) Node 생성
        for (Category c : all) {
            Long pid = (c.getParent() == null ? null : c.getParent().getCategoryId());
            Node n = new Node(c.getCategoryId(), c.getName(), c.getDepth(), pid, nameComparator);
            newById.put(c.getCategoryId(), n);
        }

        // 2) children / root 구성
        for (Node n : newById.values()) {
            if (n.parentId == null) {
                newRootIds.add(n.id);
            } else {
                newChildrenIndex.computeIfAbsent(n.parentId, k -> new TreeSet<>(nameComparator)).add(n.id);
                Node parent = newById.get(n.parentId);
                if (parent != null) parent.children.add(n.id);
            }
        }

        // 3) swap (writeLock 최소화)
        lock.writeLock().lock();
        try {
            byId = newById;
            childrenIndex = newChildrenIndex;
            rootIds = newRootIds;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Node> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(byId.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public record CategoryTree(Long id, String name, int depth, List<CategoryTree> children) {
        @Override
        public String toString() {
            return "CategoryTree{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", depth=" + depth +
                    ", children size=" + (children == null ? 0 : children.size()) +
                    '}';
        }
    }

    public List<CategoryTree> getCategoryTree() {
        lock.readLock().lock();
        try {
            return rootIds.stream()
                    .map(this::toTree)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    private CategoryTree toTree(Long id) {
        Node n = byId.get(id);
        List<CategoryTree> children = n.children.stream()
                .map(this::toTree)
                .toList();
        return new CategoryTree(n.id, n.name, n.depth, children);
    }

    // ===== 조회 API (전부 메모리) =====
    public List<Node> getRoots() {
        lock.readLock().lock();
        try {
            return rootIds.stream().map(byId::get).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Node> getChildrenOf(Long parentId) {
        lock.readLock().lock();
        try {
            Node parent = byId.get(parentId);
            if (parent == null) return List.of();
            return parent.children.stream().map(byId::get).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Node getById(Long id) {
        lock.readLock().lock();
        try {
            return byId.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    // 동일한 이름을 가진 노드가 여러 개 있을 수 있음
    public List<Node> getByName(String name) {
        lock.readLock().lock();
        try {
            return byId.values().stream()
                    .filter(n -> n.name.equalsIgnoreCase(name))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    // ===== 갱신 API (DB 커밋 후 호출) =====
    public void onCreated(Long id, String name, Long parentId) {
        lock.writeLock().lock();
        try {
            int depth = (parentId == null) ? 0 : (byId.get(parentId).depth + 1);
            Node n = new Node(id, name, depth, parentId, nameComparator);
            byId.put(id, n);

            if (parentId == null) {
                rootIds.add(id); // TreeSet이라 자동 정렬
            } else {
                childrenIndex.computeIfAbsent(parentId, k -> new TreeSet<>(nameComparator)).add(id);
                Node parent = byId.get(parentId);
                if (parent != null) parent.children.add(id); // TreeSet 자동 정렬
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void onRenamed(Long id, String newName) {
        lock.writeLock().lock();
        try {
            Node n = byId.get(id);
            if (n == null) return;
            n.name = newName;

            if (n.parentId == null) {
                rootIds.remove(id);
                rootIds.add(id); // TreeSet 재정렬
            } else {
                NavigableSet<Long> siblings = childrenIndex.get(n.parentId);
                if (siblings != null) {
                    siblings.remove(id);
                    siblings.add(id);
                }
                Node parent = byId.get(n.parentId);
                if (parent != null) {
                    parent.children.remove(id);
                    parent.children.add(id);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void onDeleted(Long id) {
        lock.writeLock().lock();
        try {
            Node n = byId.get(id);
            if (n == null) return;

            // 삭제할 노드 + 모든 자손 수집
            Set<Long> toDelete = new HashSet<>();
            collectDescendants(id, toDelete);
            toDelete.add(id);

            // 부모 참조에서 제거
            if (n.parentId == null) {
                rootIds.remove(id);
            } else {
                NavigableSet<Long> siblings = childrenIndex.get(n.parentId);
                if (siblings != null) siblings.remove(id);

                Node parent = byId.get(n.parentId);
                if (parent != null) parent.children.remove(id);
            }

            // 자손 포함 전체 삭제
            for (Long delId : toDelete) {
                Node delNode = byId.remove(delId);
                if (delNode != null) {
                    NavigableSet<Long> ch = childrenIndex.remove(delId);
                    if (ch != null) ch.clear();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // TreeSet 기반 자손 수집
    private void collectDescendants(Long id, Set<Long> acc) {
        NavigableSet<Long> ch = childrenIndex.getOrDefault(id, new TreeSet<>());
        for (Long cid : ch) {
            acc.add(cid);
            collectDescendants(cid, acc);
        }
    }

}
