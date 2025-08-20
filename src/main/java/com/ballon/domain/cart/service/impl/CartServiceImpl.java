package com.ballon.domain.cart.service.impl;

import com.ballon.domain.cart.dto.CartItemRequest;
import com.ballon.domain.cart.dto.CartResponse;
import com.ballon.domain.cart.entity.Cart;
import com.ballon.domain.cart.entity.CartItem;
import com.ballon.domain.cart.repository.CartItemRepository;
import com.ballon.domain.cart.repository.CartRepository;
import com.ballon.domain.cart.service.CartService;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    // securtiy 에서 userID 뽑지 않고 파라미터로 받는 방식 security 에서 뽑는거면 수정해야함.

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder()
                                .user(User.builder().userId(userId).build())
                        .build()));
        return toDto(cart);
    }

    @Override
    public CartResponse addItem(Long userId, CartItemRequest req) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder()
                    .user(User.builder().userId(userId).build())
                .build()));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        CartItem item = cartItemRepository.findByCartIdAndUserId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem created = CartItem.builder()
                            .product(product)
                            .quantity(0)
                            .build();
                    cart.addItem(created);
                    return created;
                });
        item.increase(req.getQuantity());
        cartItemRepository.save(item);
        return toDto(cart);
    }

    @Override
    public CartResponse changeQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.)");
        }
        Cart cart =cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목이 없습니다."));
        if (quantity == 0) {
            cart.removeItem(item);
        } else {
            item.changeQuantity(quantity);
        }
        item.changeQuantity(quantity);
        return toDto(cart);
    }

    @Override
    public CartResponse removeItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목이 없습니다."));
        cart.removeItem(item);
        return toDto(cart);
    }

    private CartResponse toDto(Cart cart) {
        var items = cart.getItems().stream().map(ci -> CartResponse.Item.builder()
                .cartItemId(ci.getId())
                .productId(ci.getProduct().getId())
                .name(ci.getProduct().getName())
                .price(ci.getProduct().getPrice())
                .quantity(ci.getQuantity())
                .lineAmount(ci.getProduct().getPrice() * ci.getQuantity())
                .build()).collect(Collectors.toList());

        int totalQty = items.stream().mapToInt(CartResponse.Item::getQuantity).sum();
        int totalAmt = items.stream().mapToInt(CartResponse.Item::getLineAmount).sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getUserId())
                .items(items)
                .totalQuantity(totalQty)
                .totalPrice(totalAmt)
                .build();

    }
}
