package org.global.academy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Portfolio {

    // Use symbol-keyed maps to avoid needing Stock equality and to simplify lookups
    private Map<String, Integer> holdings; // symbol -> quantity
    private Map<String, Stock> stockInfo;   // symbol -> Stock details
    private Map<String, Double> costBasis;  // symbol -> total cost basis (total amount invested)
    private List<Purchase> purchaseHistory; // list of all purchases

    public Portfolio() {
        holdings = new HashMap<>();
        stockInfo = new HashMap<>();
        costBasis = new HashMap<>();
        purchaseHistory = new ArrayList<>();
    }

    // Inner class to represent a purchase transaction
    public static class Purchase {
        private String symbol;
        private int quantity;
        private double price;
        private String timestamp;

        public Purchase(String symbol, int quantity, double price, String timestamp) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.timestamp = timestamp;
        }

        public String getSymbol() { return symbol; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getTimestamp() { return timestamp; }
        public double getTotalCost() { return quantity * price; }
    }

    // Add stock purchase
    public void addStock(Stock stock, int quantity) {
        if (stock == null || stock.getsymbol() == null) {
            return;
        }
        String symbol = stock.getsymbol();
        stockInfo.put(symbol, stock);
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + Math.max(0, quantity));
        
        // Update cost basis: add (quantity * price) to total investment
        double purchaseCost = Math.max(0, quantity) * stock.getPrice();
        costBasis.put(symbol, costBasis.getOrDefault(symbol, 0.0) + purchaseCost);
        
        // Record purchase in history
        if (quantity > 0) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            purchaseHistory.add(new Purchase(symbol, quantity, stock.getPrice(), timestamp));
        }
    }

    // Remove (sell) stock
    public void removeStock(String symbol, int quantity) {
        if (symbol == null) {
            return;
        }
        if (holdings.containsKey(symbol)) {
            int currentQty = holdings.get(symbol);
            if (quantity >= currentQty) {
                // Remove completely
                holdings.remove(symbol);
                stockInfo.remove(symbol);
                costBasis.remove(symbol);
            } else {
                // Partial sale: reduce cost basis proportionally
                double currentCostBasis = costBasis.getOrDefault(symbol, 0.0);
                double avgCost = currentQty > 0 ? currentCostBasis / currentQty : 0.0;
                double reducedCost = quantity * avgCost;
                
                holdings.put(symbol, currentQty - quantity);
                costBasis.put(symbol, currentCostBasis - reducedCost);
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

    public double getCostBasis(String symbol) {
        return costBasis.getOrDefault(symbol, 0.0);
    }

    public Map<String, Double> getAllCostBasis() {
        return new HashMap<>(costBasis);
    }

    public List<Purchase> getPurchaseHistory(String symbol) {
        List<Purchase> history = new ArrayList<>();
        for (Purchase p : purchaseHistory) {
            if (p.getSymbol().equals(symbol)) {
                history.add(p);
            }
        }
        return history;
    }

    public List<Purchase> getAllPurchaseHistory() {
        return new ArrayList<>(purchaseHistory);
    }

    public void removeStock(Stock stock, int quantity) {
        if (stock != null) {
            removeStock(stock.getsymbol(), quantity);
        }
    }
}
