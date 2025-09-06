package com.ballon.domain.order.service.impl;

import com.ballon.domain.address.repository.AddressRepository;
import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.coupon.entity.type.Type;
import com.ballon.domain.order.dto.*;
import com.ballon.domain.order.entity.Order;
import com.ballon.domain.order.entity.OrderProduct;
import com.ballon.domain.order.entity.type.OrderStatus;
import com.ballon.domain.order.repository.OrderProductRepository;
import com.ballon.domain.order.repository.OrderRepository;
import com.ballon.domain.order.service.OrderService;
import com.ballon.domain.product.entity.CouponProduct;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.CouponProductRepository;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.user.entity.UserCoupon;
import com.ballon.domain.user.entity.id.UserCouponId;
import com.ballon.domain.user.repository.UserCouponRepository;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.BadRequestException;
import com.ballon.global.common.exception.ConflictException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderProductRepository orderProductRepository;
    private final CouponProductRepository couponProductRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        int totalAmount = 0;
        String firstProductName = null;

        List<OrderProduct> orderProducts = new ArrayList<>();

        for (OrderProductRequest productRequest : orderRequest.getProducts()) {
            // 상품 조회
            Product product = productRepository.findById(productRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

            if (Objects.isNull(firstProductName)) {
                firstProductName = product.getName();
            }

            int unitPrice = product.getPrice();

            // 쿠폰 조회 및 검증
            Coupon coupon = null;
            int discountAmount = 0;
            if (productRequest.getCouponId() != null) {
                UserCoupon userCoupon = userCouponRepository.findById(
                                new UserCouponId(UserUtil.getUserId(), productRequest.getCouponId()))
                        .orElseThrow(() -> new NotFoundException("회원이 가지고 있지 않은 쿠폰입니다."));

                validateUserCoupon(userCoupon);

                CouponProduct couponProduct = couponProductRepository
                        .findByProduct_IdAndCoupon_CouponId(productRequest.getProductId(), productRequest.getCouponId())
                        .orElseThrow(() -> new NotFoundException("해당 상품에 적용할 수 없는 쿠폰입니다."));

                coupon = couponProduct.getCoupon();
                discountAmount = coupon.getType().equals(Type.PERCENT)
                        ? unitPrice * coupon.getDiscount() / 100
                        : coupon.getDiscount();
            }

            // 최종 결제금액
            int paidAmount = (unitPrice - discountAmount) * productRequest.getQuantity();

            // OrderProduct 생성
            OrderProduct orderProduct = OrderProduct.createOrderProduct(
                    product,
                    coupon, // 쿠폰 저장 (없으면 null)
                    productRequest.getQuantity(),
                    unitPrice,
                    discountAmount,
                    paidAmount
            );

            orderProducts.add(orderProduct);
            totalAmount += paidAmount;
        }

        // 주문 생성 및 저장
        Order order = createAndSaveOrder(orderRequest, totalAmount);

        // 주문-상품 연결
        linkOrderProducts(orderProducts, order);

        // 응답 반환
        return buildOrderResponse(orderRequest, order, firstProductName);
    }

    @Override
    public OrderResponse completeOrder(PaymentConfirmRequest paymentConfirmRequest) {
        Order order = orderRepository.findById(paymentConfirmRequest.getOrderId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주문입니다."));

        List<OrderProduct> orderProducts = orderProductRepository.findByOrder_OrderId(order.getOrderId());
        int totalAmount = orderProducts.stream()
                .mapToInt(OrderProduct::getPaidAmount)
                .sum();

        if(totalAmount != paymentConfirmRequest.getAmount()) {
            throw new BadRequestException("결제 금액 불일치.");
        }

        order.updateOrderStatus(OrderStatus.DONE);

        List<Long> couponIds = orderProductRepository.findCouponIdsByOrderId(order.getOrderId());
        if (!couponIds.isEmpty()) {
            int usedCouponQuantity = userCouponRepository.markCouponsAsUsed(UserUtil.getUserId(), couponIds);

            log.info("사용자 {}의 쿠폰 사용 처리 성공 (총 {}건) - 적용 쿠폰 ID: {}",
                    UserUtil.getUserId(), usedCouponQuantity, couponIds);
        }

        String firstProductName = orderProducts.getFirst().getProduct().getName();
        String orderTitle = (orderProducts.size() > 1)
                ? firstProductName + " 외 " + (orderProducts.size() - 1) + "건"
                : firstProductName;

        return new OrderResponse(
                order.getOrderId(),
                order.getAmount(),
                orderTitle,
                userRepository.getUserNameByUserId(UserUtil.getUserId())
        );
    }

    @Override
    public void failOrder(PaymentFailRequest paymentFailRequest) {
        Order order = orderRepository.findById(paymentFailRequest.getOrderId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주문입니다."));

        order.updateOrderStatus(OrderStatus.CANCELED);

        log.warn("주문 {} 결제 실패 처리 - 코드: {}, 사유: {}",
                paymentFailRequest.getOrderId(),
                paymentFailRequest.getCode(),
                paymentFailRequest.getMessage());
    }

    public void getUserOrders(Long userId) {

    }

    // 쿠폰 검증
    private void validateUserCoupon(UserCoupon userCoupon) {
        if (userCoupon.getIsUsed()) {
            throw new ConflictException("이미 사용된 쿠폰입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (userCoupon.getCoupon().getEvent().getEndDate().isBefore(now)) {
            throw new ConflictException("만료된 쿠폰입니다.");
        }
    }

    // 주문 생성 및 저장
    private Order createAndSaveOrder(OrderRequest orderRequest, int totalAmount) {
        Order order = Order.createOrder(
                totalAmount,
                OrderStatus.READY,
                userRepository.getReferenceById(UserUtil.getUserId()),
                addressRepository.getReferenceById(orderRequest.getAddressId())
        );

        return orderRepository.save(order);
    }

    // 주문과 상품 연결
    private void linkOrderProducts(List<OrderProduct> orderProducts, Order order) {
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrder(order);
        }
        orderProductRepository.saveAll(orderProducts);
    }

    // 응답 DTO 생성
    private OrderResponse buildOrderResponse(OrderRequest orderRequest, Order order, String firstProductName) {
        String orderTitle = (orderRequest.getProducts().size() > 1)
                ? firstProductName + " 외 " + (orderRequest.getProducts().size() - 1) + "건"
                : firstProductName;

        return new OrderResponse(
                order.getOrderId(),
                order.getAmount(),
                orderTitle,
                userRepository.getUserNameByUserId(UserUtil.getUserId())
        );
    }

}
