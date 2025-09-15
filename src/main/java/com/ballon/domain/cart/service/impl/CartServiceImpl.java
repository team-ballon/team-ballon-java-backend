package com.ballon.domain.cart.service.impl;

import com.ballon.domain.cart.dto.CartProductRequest;
import com.ballon.domain.cart.dto.CartResponse;
import com.ballon.domain.cart.entity.Cart;
import com.ballon.domain.cart.entity.CartProduct;
import com.ballon.domain.cart.repository.CartProductRepository;
import com.ballon.domain.cart.repository.CartRepository;
import com.ballon.domain.cart.service.CartService;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.user.entity.User;
import com.ballon.global.common.exception.NotFoundException;
import com.ballon.global.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponse getMyCart(Long userId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(User.builder().userId(userId).build())));

        log.debug("사용자 {} 장바구니 조회 완료 - 상품 {}건", userId, cart.getProducts().size());
        return toDto(cart);
    }

    @Override
    public CartResponse addProduct(Long userId, CartProductRequest req) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(User.builder().userId(userId).build())));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new NotFoundException("상품이 존재하지 않습니다."));

        CartProduct line = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartProduct created = CartProduct.create(product, 0);
                    cart.addProduct(created);

                    return created;
                });

        line.increase(req.getQuantity());
        cartProductRepository.save(line);
        log.info("사용자 {} 장바구니에 상품 {} 수량 {}개 추가 완료", userId, product.getId(), req.getQuantity());

        return toDto(cart);
    }

    @Override
    public CartResponse changeQuantity(Long userId, Long cartProductId, Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new ValidationException("수량은 1 이상이어야 합니다.");
        }

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("장바구니가 없습니다."));

        CartProduct line = cart.getProducts().stream()
                .filter(cp -> cp.getId().equals(cartProductId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("장바구니 상품이 없습니다."));

        line.changeQuantity(quantity);
        log.info("사용자 {} 장바구니 상품 {} 수량을 {}로 변경 완료", userId, cartProductId, quantity);

        return toDto(cart);
    }

    @Override
    public CartResponse removeProduct(Long userId, Long cartProductId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new NotFoundException("장바구니가 없습니다."));

        CartProduct line = cart.getProducts().stream()
                .filter(cp -> cp.getId().equals(cartProductId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("장바구니 상품이 없습니다."));

        cart.removeProduct(line);
        log.info("사용자 {} 장바구니에서 상품 {} 제거 완료", userId, cartProductId);

        return toDto(cart);
    }

    private CartResponse toDto(Cart cart) {
        var products = cart.getProducts().stream()
                .map(cp -> CartResponse.Product.builder()
                        .cartProductId(cp.getId())
                        .productImageUrl(cp.getProduct().getProductUrl())
                        .productId(cp.getProduct().getId())
                        .name(cp.getProduct().getName())
                        .price(cp.getProduct().getPrice())
                        .quantity(cp.getQuantity())
                        .lineAmount(cp.getProduct().getPrice() * cp.getQuantity())
                        .build())
                .collect(Collectors.toList());

        int totalQty = products.stream().mapToInt(CartResponse.Product::getQuantity).sum();
        int totalAmt = products.stream().mapToInt(CartResponse.Product::getLineAmount).sum();

        log.debug("장바구니 DTO 변환 완료 - 상품 {}건, 총 수량 {}, 총 금액 {}",
                products.size(), totalQty, totalAmt);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getUserId())
                .products(products)
                .totalQuantity(totalQty)
                .totalPrice(totalAmt)
                .build();
    }
}
