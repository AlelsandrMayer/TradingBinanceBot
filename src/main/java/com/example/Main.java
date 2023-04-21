package com.example;
import com.binance.connector.client.WebsocketStreamClient;
import com.binance.connector.client.impl.WebsocketStreamClientImpl;

public class Main {

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String SECRET_KEY = "YOUR_SECRET_KEY";

    public static void main(String[] args) {
        // Создание экземпляра WebsocketStreamClient без использования API ключей
        WebsocketStreamClient websocketStreamClient = new WebsocketStreamClientImpl();

        // Создание экземпляра SMAStrategy с использованием WebsocketStreamClient и передачей API ключей
        TradingStrategy smaStrategy = new SMAStrategy(websocketStreamClient, API_KEY, SECRET_KEY, "BTCUSDT", "1h", 14);

        // Ваш торговый бот теперь работает с веб-сокетами и автоматически анализирует данные с заданным интервалом
    }
}



