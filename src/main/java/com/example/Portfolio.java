package com.example;


import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private int tradeCountAll;
    private double balance;
    private double profit;
    private List<Double> trades;

    public Portfolio(double initialBalance) {
        this.balance = initialBalance;
        this.profit = 0.0;
        this.trades = new ArrayList<>();
        this.tradeCountAll = 0;
    }

    public void openTrade(double price) {
        if (balance > 20000) {
            this.trades.add(price);
            this.tradeCountAll++;
            this.balance -= price;
        }
    }

    public void closeTrade(double price) {
        if (!this.trades.isEmpty()) {
            double entryPrice = trades.remove(trades.size() - 1);
            this.profit += (price - entryPrice);
            this.balance += price;
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public double getProfit() {
        return this.profit;
    }

    public int getSize() {
        return this.trades.size();
    }
    public int getTradeCountAll() {
        return tradeCountAll;
    }
}


