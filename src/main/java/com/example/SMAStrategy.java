package com.example;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.spot.Market;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SMAStrategy implements TradingStrategy {
    private final SpotClient spotClient;
    private final String tradingPair;
    private final String interval;
    private final int smaPeriod;
    private final ObjectMapper objectMapper;

    public SMAStrategy(SpotClient spotClient, String tradingPair, String interval, int smaPeriod) {
        this.spotClient = spotClient;
        this.tradingPair = tradingPair;
        this.interval = interval;
        this.smaPeriod = smaPeriod;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public OrderActionType analyzeMarketData() {
        Market market = spotClient.createMarket();
        List<Double> closingPrices = getHistoricalClosingPrices(market);
        double sma = calculateSMA(closingPrices);
        double currentPrice = closingPrices.get(closingPrices.size() - 1);

        if (currentPrice > sma) {
            return OrderActionType.BUY;
        } else if (currentPrice < sma) {
            return OrderActionType.SELL;
        } else {
            return OrderActionType.HOLD;
        }
    }




    private List<Double> getHistoricalClosingPrices(Market market) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", tradingPair);
        parameters.put("interval", interval);
        parameters.put("limit", smaPeriod);

        String klinesJson = market.klines(parameters);
        List<List<Object>> klines = parseKlinesJson(klinesJson);
        List<Double> closingPrices = klines.stream()
                .map(kline -> Double.parseDouble(kline.get(4).toString())) // Index 4 is the closing price
                .collect(Collectors.toList());

        return closingPrices;
    }

    private List<List<Object>> parseKlinesJson(String json) {
        try {
            CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, List.class);
            return objectMapper.readValue(json, collectionType);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing klines JSON", e);
        }
    }

    private double calculateSMA(List<Double> prices) {
        double sum = prices.stream().mapToDouble(Double::doubleValue).sum();
        return sum / prices.size();
    }
}

