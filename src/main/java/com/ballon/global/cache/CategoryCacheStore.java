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

    // ===== 내부 노드 정의 =====
    public static final class Node {
        public final Long id;
        public String name;
        public int depth;
        public Long parentId;             // null = root
        public final List<Long> children; // 자식 ID 목록

        Node(Long id, String name, int depth, Long parentId) {
            this.id = id;
            this.name = name;
            this.depth = depth;
            this.parentId = parentId;
            this.children = new ArrayList<>();
        }
    }

    private final Map<Long, Node> byId = new HashMap<>();
    private final Map<Long, List<Long>> childrenIndex = new HashMap<>(); // parentId → childIds(정렬)
    private final List<Long> rootIds = new ArrayList<>();
    private final Map<String, Long> byName = new HashMap<>(); // name 전역 유니크 가정

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // ===== 초기 적재 =====
    public void loadAll(List<Category> all) {
        lock.writeLock().lock();
        try {
            byId.clear(); childrenIndex.clear(); rootIds.clear(); byName.clear();

            // 1) 노드 생성
            for (Category c : all) {
                Long id = c.getCategoryId();
                Long pid = (c.getParent() == null ? null : c.getParent().getCategoryId());
                Node n = new Node(id, c.getName(), c.getDepth(), pid);
                byId.put(id, n);
                // name 전역 유니크 정책
                byName.put(c.getName(), id);
            }

            // 2) children 인덱스/루트 계산
            for (Node n : byId.values()) {
                if (n.parentId == null) {
                    rootIds.add(n.id);
                } else {
                    childrenIndex.computeIfAbsent(n.parentId, k -> new ArrayList<>()).add(n.id);
                }
            }

            // 3) 정렬(이름 기준) – 필요 시 다른 기준으로 변경 가능
            Comparator<Long> byNameCmp = Comparator.comparing(id -> byId.get(id).name, String.CASE_INSENSITIVE_ORDER);
            rootIds.sort(byNameCmp);
            for (List<Long> list : childrenIndex.values()) list.sort(byNameCmp);
        } finally {
            lock.writeLock().unlock();
        }
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
            List<Long> ids = childrenIndex.getOrDefault(parentId, List.of());
            return ids.stream().map(byId::get).toList();
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

    public Node getByName(String name) {
        lock.readLock().lock();
        try {
            Long id = byName.get(name);
            return id == null ? null : byId.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    // ===== 갱신 API (DB 커밋 후 호출) =====
    public void onCreated(Long id, String name, Long parentId) {
        lock.writeLock().lock();
        try {
            // parent depth 계산
            int depth = (parentId == null) ? 0 : (byId.get(parentId).depth + 1);
            Node n = new Node(id, name, depth, parentId);
            byId.put(id, n);
            byName.put(name, id);

            if (parentId == null) {
                rootIds.add(id);
                rootIds.sort(Comparator.comparing(i -> byId.get(i).name, String.CASE_INSENSITIVE_ORDER));
            } else {
                childrenIndex.computeIfAbsent(parentId, k -> new ArrayList<>()).add(id);
                childrenIndex.get(parentId)
                        .sort(Comparator.comparing(i -> byId.get(i).name, String.CASE_INSENSITIVE_ORDER));
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
            // name 전역 유니크 정책에 맞게 인덱스 갱신
            byName.remove(n.name);
            n.name = newName;
            byName.put(newName, id);

            // 정렬 영향
            if (n.parentId == null) {
                rootIds.sort(Comparator.comparing(i -> byId.get(i).name, String.CASE_INSENSITIVE_ORDER));
            } else {
                List<Long> siblings = childrenIndex.getOrDefault(n.parentId, List.of());
                if (!siblings.isEmpty()) {
                    siblings.sort(Comparator.comparing(i -> byId.get(i).name, String.CASE_INSENSITIVE_ORDER));
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

            // 하위 전체 삭제(정책: cascade delete)
            List<Long> toDelete = new ArrayList<>();
            collectDescendants(id, toDelete);
            toDelete.add(id);

            // 인덱스 정리
            if (n.parentId == null) {
                rootIds.remove(id);
            } else {
                List<Long> siblings = childrenIndex.get(n.parentId);
                if (siblings != null) siblings.remove(id);
            }

            for (Long delId : toDelete) {
                Node dn = byId.remove(delId);
                if (dn != null) {
                    byName.remove(dn.name);
                    List<Long> ch = childrenIndex.remove(delId);
                    if (ch != null) ch.clear();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ===== 내부 유틸 =====
    private void collectDescendants(Long id, List<Long> acc) {
        List<Long> ch = childrenIndex.getOrDefault(id, List.of());
        for (Long cid : ch) {
            acc.add(cid);
            collectDescendants(cid, acc);
        }
    }

    private void propagateDepth(Long id, int delta) {
        Node n = byId.get(id);
        if (n == null || delta == 0) return;
        n.depth += delta;
        for (Long cid : childrenIndex.getOrDefault(id, List.of())) {
            propagateDepth(cid, delta);
        }
    }
}
