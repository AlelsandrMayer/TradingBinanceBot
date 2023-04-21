package com.example;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;

public class Main {
    public static void main(String[] args) {
        String apiKey =

        SpotClient spotClient = new SpotClientImpl(apiKey, secretKey);
        TradingStrategy smaStrategy = new SMAStrategy(spotClient, "BTCUSDT", "1h", 14);
        TradingBot tradingBot = new TradingBot(apiKey, secretKey, smaStrategy);
        tradingBot.startTrading();
    }
}

