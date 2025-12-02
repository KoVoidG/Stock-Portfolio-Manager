package org.global.academy;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    // Use symbol-keyed maps to avoid needing Stock equality and to simplify lookups
    private Map<String, Integer> holdings; // symbol -> quantity
    private Map<String, Stock> stockInfo;   // symbol -> Stock details

    public Portfolio() {
        holdings = new HashMap<>();
        stockInfo = new HashMap<>();
    }

    // Add stock purchase
    public void addStock(Stock stock, int quantity) {
        if (stock == null || stock.getsymbol() == null) {
            return;
        }
        String symbol = stock.getsymbol();
        stockInfo.put(symbol, stock);
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + Math.max(0, quantity));
    }

    // Remove (sell) stock
    public void removeStock(String symbol, int quantity) {
        if (symbol == null) {
            return;
        }
        if (holdings.containsKey(symbol)) {
            int currentQty = holdings.get(symbol);
            if (quantity >= currentQty) {
                holdings.remove(symbol); // remove completely
                stockInfo.remove(symbol);
            } else {
                holdings.put(symbol, currentQty - quantity);
            }
        }
    }

    // Update stock details (price/name/exchange)
    public void updateStockDetails(String symbol, Stock updated) {
        if (symbol == null || updated == null) {
            return;
        }
        Stock existing = stockInfo.get(symbol);
        if (existing != null) {
            existing.setCompanyName(updated.getcompany_Name());
            existing.setPrice(updated.getPrice());
            existing.setStockExchange(updated.getStock());
        } else {
            // if not exists, add it with zero quantity
            stockInfo.put(symbol, updated);
            holdings.putIfAbsent(symbol, 0);
        }
    }

    // Get total portfolio value
    public double getValue() {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = stockInfo.get(entry.getKey());
            if (stock != null) {
                total += stock.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    // Display holdings
    public void showPortfolio() {
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock s = stockInfo.get(entry.getKey());
            System.out.println((s == null ? entry.getKey() : s.toString()) + " | Quantity: " + entry.getValue());
        }
    }

    // Accessors used by the server
    public Map<String, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }

    public Stock getStock(String symbol) {
        return stockInfo.get(symbol);
    }

    public void removeStock(Stock stock, int quantity) {
        if (stock != null) {
            removeStock(stock.getsymbol(), quantity);
        }
    }
}
