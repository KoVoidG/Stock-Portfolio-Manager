package org.global.academy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PortfolioPersistence {

    // Save data in src/main/resources/data/ to persist across rebuilds
    private static final String DATA_FILE = "src/main/resources/data/portfolio_data.json";
    private static final Gson gson = new Gson();

    // Save portfolio to file
    public static void savePortfolio(Portfolio portfolio) {
        try {
            // Create directory if it doesn't exist
            File dataFile = new File(DATA_FILE);
            File parentDir = dataFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("holdings", portfolio.getHoldings());
            data.put("costBasis", portfolio.getAllCostBasis());

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

            // Save purchase history
            List<Map<String, Object>> historyData = new ArrayList<>();
            for (Portfolio.Purchase purchase : portfolio.getAllPurchaseHistory()) {
                Map<String, Object> purchaseMap = new HashMap<>();
                purchaseMap.put("symbol", purchase.getSymbol());
                purchaseMap.put("quantity", purchase.getQuantity());
                purchaseMap.put("price", purchase.getPrice());
                purchaseMap.put("timestamp", purchase.getTimestamp());
                historyData.add(purchaseMap);
            }
            data.put("purchaseHistory", historyData);

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

            // Load holdings (without creating new purchase history)
            JsonObject holdings = data.getAsJsonObject("holdings");
            if (holdings != null) {
                for (String symbol : holdings.keySet()) {
                    int quantity = holdings.get(symbol).getAsInt();
                    if (quantity > 0) {
                        Stock stock = portfolio.getStock(symbol);
                        if (stock != null) {
                            // Use setter method to properly restore holdings
                            portfolio.setHolding(symbol, quantity);
                        }
                    }
                }
            }

            // Load cost basis
            JsonObject costBasisData = data.getAsJsonObject("costBasis");
            if (costBasisData != null) {
                for (String symbol : costBasisData.keySet()) {
                    double costBasis = costBasisData.get(symbol).getAsDouble();
                    // Use setter method to properly restore cost basis
                    portfolio.setCostBasis(symbol, costBasis);
                }
            }

            // Load purchase history with original timestamps
            JsonArray purchaseHistoryData = data.getAsJsonArray("purchaseHistory");
            if (purchaseHistoryData != null) {
                for (int i = 0; i < purchaseHistoryData.size(); i++) {
                    JsonObject purchaseObj = purchaseHistoryData.get(i).getAsJsonObject();
                    String symbol = purchaseObj.get("symbol").getAsString();
                    int quantity = purchaseObj.get("quantity").getAsInt();
                    double price = purchaseObj.get("price").getAsDouble();
                    String timestamp = purchaseObj.get("timestamp").getAsString();
                    
                    // Use method to properly add to purchase history
                    portfolio.addPurchaseToHistory(
                        new Portfolio.Purchase(symbol, quantity, price, timestamp)
                    );
                }
            }

            System.out.println("Portfolio loaded from " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Failed to load portfolio: " + e.getMessage());
        }
    }
}
