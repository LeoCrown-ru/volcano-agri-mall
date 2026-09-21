package com.cloud.oms.enums;

public enum OrderStatus {
    UNPAYMENT(0L, "待支付"),
    PAID(1L, "已支付"),
    CANCEL(2L, "已取消"),
    COMPLETE(3L, "已完成"),
    BACK(4L, "已退款");

    private final Long value;
    private final String label;

    OrderStatus(Long value, String label) {
        this.value = value;
        this.label = label;
    }

    public Long getValue() { return value; }
    public String getLabel() { return label; }
}
