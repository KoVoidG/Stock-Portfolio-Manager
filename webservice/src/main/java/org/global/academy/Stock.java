package org.global.academy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Stock {

    private String company_name; //member
    private String symbol;
    private String stock_exchange;
    private double current_price;

    public Stock(String name, String short_symbol, String stock, double price) {
        company_name = name;
        symbol = short_symbol;
        stock_exchange = stock;
        current_price = price;
    }

    public String getcompany_Name() {
        return company_name;
    }

    public String getsymbol() {
        return symbol;
    }

    public String getStock() {
        return stock_exchange;
    }

    public double getPrice() {
        return current_price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s @ $%.2f", company_name, symbol, stock_exchange, current_price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stock stock = (Stock) o;
        return symbol != null && symbol.equals(stock.symbol);
    }

    @Override
    public int hashCode() {
        return symbol == null ? 0 : symbol.hashCode();
    }

    // setters in case server wants to update details
    public void setPrice(double price) {
        this.current_price = price;
    }

    public void setCompanyName(String name) {
        this.company_name = name;
    }

    public void setStockExchange(String stock) {
        this.stock_exchange = stock;
    }

    /**
     * Fetches today's stock price from Yahoo Finance API for the given symbol.
     *
     * @param symbol the stock symbol (e.g., "AAPL", "GOOGL")
     * @return the current regular market price, or -1.0 if fetch fails
     */
    public static double fetchTodaysPrice(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return -1.0;
        }
        String urlString = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol.toLowerCase();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            conn.disconnect();

            // Parse JSON
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject meta = json.getAsJsonObject("chart").getAsJsonArray("result")
                    .get(0).getAsJsonObject().getAsJsonObject("meta");
            double price = meta.get("regularMarketPrice").getAsDouble();
            return price;
        } catch (Exception e) {
            System.err.println("Failed to fetch price for " + symbol + ": " + e.getMessage());
            return -1.0;
        }
    }

}
