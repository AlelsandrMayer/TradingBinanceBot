package com.example;

public class OrderAction {
    private final OrderActionType actionType;
    private final double amount;

    public OrderAction(OrderActionType actionType, double amount) {
        this.actionType = actionType;
        this.amount = amount;
    }

    public OrderActionType getActionType() {
        return actionType;
    }

    public double getAmount() {
        return amount;
    }
}
