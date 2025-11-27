package org.global.academy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PortfolioPersistence {

    private static final String DATA_FILE = "portfolio_data.json";
    private static final Gson gson = new Gson();

    // Save portfolio to file
    public static void savePortfolio(Portfolio portfolio) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("holdings", portfolio.getHoldings());

            // Save stock details
            Map<String, Map<String, Object>> stocksData = new HashMap<>();
            for (String symbol : portfolio.getHoldings().keySet()) {
                Stock stock = portfolio.getStock(symbol);
                if (stock != null) {
                    Map<String, Object> stockInfo = new HashMap<>();
                    stockInfo.put("company_name", stock.getcompany_Name());
                    stockInfo.put("symbol", stock.getsymbol());
                    stockInfo.put("stock_exchange", stock.getStock());
                    stockInfo.put("current_price", stock.getPrice());
                    stocksData.put(symbol, stockInfo);
                }
            }
            data.put("stocks", stocksData);

            String json = gson.toJson(data);
            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                writer.write(json);
            }
            System.out.println("Portfolio saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save portfolio: " + e.getMessage());
        }
    }

    // Load portfolio from file
    public static void loadPortfolio(Portfolio portfolio) {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                System.out.println("No saved portfolio found.");
                return;
            }

            String json;
            try (FileReader reader = new FileReader(file)) {
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    sb.append((char) c);
                }
                json = sb.toString();
            }

            JsonObject data = gson.fromJson(json, JsonObject.class);

            // Load stocks
            JsonObject stocksData = data.getAsJsonObject("stocks");
            if (stocksData != null) {
                for (String symbol : stocksData.keySet()) {
                    JsonObject stockInfo = stocksData.getAsJsonObject(symbol);
                    String companyName = stockInfo.has("company_name") ? stockInfo.get("company_name").getAsString() : symbol;
                    String exchange = stockInfo.has("stock_exchange") ? stockInfo.get("stock_exchange").getAsString() : "";
                    double price = stockInfo.has("current_price") ? stockInfo.get("current_price").getAsDouble() : 0.0;

                    Stock stock = new Stock(companyName, symbol, exchange, price);
                    portfolio.addStock(stock, 0); // Add with 0 quantity first
                }
            }

            // Load holdings
            JsonObject holdings = data.getAsJsonObject("holdings");
            if (holdings != null) {
                for (String symbol : holdings.keySet()) {
                    int quantity = holdings.get(symbol).getAsInt();
                    if (quantity > 0) {
                        Stock stock = portfolio.getStock(symbol);
                        if (stock != null) {
                            portfolio.removeStock(symbol, Integer.MAX_VALUE); // Remove the 0 quantity one
                            portfolio.addStock(stock, quantity); // Add with correct quantity
                        }
                    }
                }
            }

            System.out.println("Portfolio loaded from " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Failed to load portfolio: " + e.getMessage());
        }
    }
}
