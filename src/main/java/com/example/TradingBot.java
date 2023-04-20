package com.example;

import com.binance.connector.client.SpotClient;

public class TradingBot {
    private final SpotClient spotClient;
    private final TradingStrategy strategy;

    public TradingBot(SpotClient spotClient, TradingStrategy strategy) {
        this.spotClient = spotClient;
        this.strategy = strategy;
    }

    public void startTrading() {
        // Implement trading logic here
    }
}
