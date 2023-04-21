package com.example;

import com.binance.connector.client.WebsocketStreamClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;

public class SMAStrategy implements TradingStrategy {
    private  final String API_KEY;
    private  final String SECRET_KEY;
    private final String symbol;
    private final String interval;
    private final int smaPeriod;
    private final LinkedList<Double> closingPrices;
    private final WebsocketStreamClient websocketStreamClient;

    public SMAStrategy(WebsocketStreamClient websocketStreamClient,String API_KEY, String SECRET_KEY, String symbol, String interval, int smaPeriod) {
        this.websocketStreamClient = websocketStreamClient;
        this.API_KEY = API_KEY;
        this.SECRET_KEY = SECRET_KEY;
        this.symbol = symbol;
        this.interval = interval;
        this.smaPeriod = smaPeriod;
        this.closingPrices = new LinkedList<>();
        subscribeKlines();
    }

    private void subscribeKlines() {
        websocketStreamClient.klineStream(symbol, interval, this::handleKlineUpdate);
    }

    private void handleKlineUpdate(String klineUpdate) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(klineUpdate);
            double closePrice = rootNode.get("k").get("c").asDouble();
            updateClosingPrices(closePrice);
            OrderActionType action = analyzeMarketData();
            System.out.println("Recommended action: " + action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderActionType analyzeMarketData() {
        if (closingPrices.size() < smaPeriod) {
            return OrderActionType.HOLD;
        }

        double sma = calculateSMA();
        double lastPrice = closingPrices.getLast();

        if (lastPrice > sma) {
            return OrderActionType.BUY;
        } else if (lastPrice < sma) {
            return OrderActionType.SELL;
        } else {
            return OrderActionType.HOLD;
        }
    }

    private void updateClosingPrices(double closingPrice) {
        closingPrices.addLast(closingPrice);
        if (closingPrices.size() > smaPeriod) {
            closingPrices.removeFirst();
        }
    }

    private double calculateSMA() {
        double sum = 0;
        for (double closingPrice : closingPrices) {
            sum += closingPrice;
        }
        return sum / closingPrices.size();
    }
}

