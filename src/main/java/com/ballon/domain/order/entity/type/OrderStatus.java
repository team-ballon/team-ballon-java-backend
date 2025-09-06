package com.ballon.domain.order.entity.type;

public enum OrderStatus {
    READY,          // 결제 준비 (Toss Payments: READY)
    IN_PROGRESS,    // 결제 진행 중 (Toss Payments: IN_PROGRESS)
    WAITING_FOR_DEPOSIT, // 가상계좌 입금 대기 (Toss Payments: WAITING_FOR_DEPOSIT)
    DONE,           // 결제 완료 (Toss Payments: DONE)
    CANCELED,       // 결제 취소 (Toss Payments: CANCELED)
    PARTIAL_CANCELED, // 부분 취소 (Toss Payments: PARTIAL_CANCELED)
    ABORTED,        // 결제 승인 실패 또는 중단 (Toss Payments: ABORTED)
    EXPIRED         // 유효 시간 만료 (Toss Payments: EXPIRED)
}
