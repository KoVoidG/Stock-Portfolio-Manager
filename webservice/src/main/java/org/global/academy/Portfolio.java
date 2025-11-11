package org.global.academy;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    // Use a Map to store stocks with their quantities
    private Map<Stock, Integer> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    // Add stock purchase
    public void addStock(Stock stock, int quantity) {
        holdings.put(stock, holdings.getOrDefault(stock, 0) + quantity);
    }

    // Remove (sell) stock
    public void removeStock(Stock stock, int quantity) {
        if (holdings.containsKey(stock)) {
            int currentQty = holdings.get(stock);
            if (quantity >= currentQty) {
                holdings.remove(stock); // remove completely
            } else {
                holdings.put(stock, currentQty - quantity);
            }
        }
    }

    // Get total portfolio value
    public double getValue() {
        double total = 0.0;
        for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
            Stock stock = entry.getKey();
            int quantity = entry.getValue();
            total += stock.getPrice() * quantity;
        }
        return total;
    }

    // Display holdings
    public void showPortfolio() {
        for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
            System.out.println(entry.getKey() + " | Quantity: " + entry.getValue());
        }
    }
}

