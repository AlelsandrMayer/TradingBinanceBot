package com.example;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;

public class Main {
    public static void main(String[] args) {
        SpotClient spotClient = new SpotClientImpl();
        TradingBot tradingBot = new TradingBot(spotClient, new SMAStrategy());
        tradingBot.startTrading();
    }
}


