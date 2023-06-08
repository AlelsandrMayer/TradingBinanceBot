package com.example;

import com.binance.connector.client.WebsocketStreamClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;

public class SMAStrategy implements TradingStrategy {
    private final String API_KEY;
    private final String SECRET_KEY;
    private final String symbol;
    private final String interval;
    private final int smaPeriod;
    private final int rsiPeriod;
    private final int emaPeriod;
    private final LinkedList<Double> closingPrices;
    private final WebsocketStreamClient websocketStreamClient;
    private Portfolio portfolio;

    public SMAStrategy(WebsocketStreamClient websocketStreamClient, String API_KEY, String SECRET_KEY, String symbol, String interval, int smaPeriod, int rsiPeriod, int emaPeriod) {
        this.websocketStreamClient = websocketStreamClient;
        this.API_KEY = API_KEY;
        this.SECRET_KEY = SECRET_KEY;
        this.symbol = symbol;
        this.interval = interval;
        this.smaPeriod = smaPeriod;
        this.rsiPeriod = rsiPeriod;
        this.emaPeriod = emaPeriod;
        this.closingPrices = new LinkedList<>();
        subscribeKlines();
        this.portfolio = new Portfolio(1000000);
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
            System.out.println("Recommended action: " + action + "\nТекущие открытые сделки/всего открыто сделок ----: "
                    + portfolio.getSize() + "/" + portfolio.getTradeCountAll() + "\nБаланс ----: " + portfolio.getBalance() +
                    "\nПрофит ----: " + portfolio.getProfit() + "\nПоследняя цена: ---- " + closingPrices.getLast());

            processTrades(action, closingPrices.getLast());
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public OrderActionType analyzeMarketData() {
        if (closingPrices.size() < smaPeriod) {
            return OrderActionType.HOLD;
        }

        double sma = calculateSMA();
        double rsi = calculateRSI();
        double ema = calculateEMA();

        double lastPrice = closingPrices.getLast();

//        System.out.println("SMA: " + sma);
//        System.out.println("RSI: " + rsi);
//        System.out.println("EMA: " + ema);


        if (lastPrice > sma && rsi > 70 && lastPrice > ema) {
            return OrderActionType.SELL;
        } else if (lastPrice < sma && rsi < 30 && lastPrice < ema) {
            return OrderActionType.BUY;
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

    private double calculateRSI() {
        if (closingPrices.size() < rsiPeriod) {
            return 0;
        }

        double sumGain = 0;
        double sumLoss = 0;

        for (int i = 1; i < rsiPeriod; i++) {
            double priceDiff = closingPrices.get(i) - closingPrices.get(i - 1);
            if (priceDiff > 0) {
                sumGain += priceDiff;
            } else if (priceDiff < 0) {
                sumLoss += Math.abs(priceDiff);
            }
        }

        for (int i = rsiPeriod; i < closingPrices.size(); i++) {
            double priceDiff = closingPrices.get(i) - closingPrices.get(i - 1);
            if (priceDiff > 0) {
                sumGain += priceDiff;
            } else if (priceDiff < 0) {
                sumLoss += Math.abs(priceDiff);
            }
        }

        double avgGain = sumGain / rsiPeriod;
        double avgLoss = sumLoss / rsiPeriod;

        double relativeStrength = avgGain / avgLoss;

        return 100 - (100 / (1 + relativeStrength));
    }

    private double calculateEMA() {
        if (closingPrices.size() < emaPeriod) {
            return 0;
        }

        double smoothingFactor = 2.0 / (emaPeriod + 1);
        double ema = closingPrices.get(0);

        for (int i = 1; i < closingPrices.size(); i++) {
            ema = (closingPrices.get(i) - ema) * smoothingFactor + ema;
        }

        return ema;
    }

    private void processTrades(OrderActionType action, double price) {
        if (action == OrderActionType.BUY) {
            portfolio.openTrade(price);
        } else if (action == OrderActionType.SELL) {
            portfolio.closeTrade(price);
        }
    }
}


