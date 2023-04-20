package com.example;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;

public class Main {
    public static void main(String[] args) {
        SpotClient spotClient = new SpotClientImpl();
        TradingStrategy smaStrategy = new SMAStrategy(spotClient, "BTCUSDT", "1h", 14);
        TradingBot tradingBot = new TradingBot(spotClient, smaStrategy);
        tradingBot.startTrading();
    }
}



