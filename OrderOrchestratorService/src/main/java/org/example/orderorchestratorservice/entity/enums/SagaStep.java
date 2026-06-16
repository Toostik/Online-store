package org.example.orderorchestratorservice.entity.enums;

public enum SagaStep {

    WAITING_RESERVE,

    WAITING_PAYMENT,

    WAITING_COMMIT,

    WAITING_RELEASE,

    COMPLETED,

    FAILED

}