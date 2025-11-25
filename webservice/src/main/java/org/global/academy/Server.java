package org.global.academy;

import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Server {

    public static void main(String[] args) {
        port(8080);
        Random rand = new Random();

        // Serve static files from src/main/resources/public
        staticFiles.location("/public");

        // Simple CORS setup
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });

        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        Gson gson = new Gson();

        // Default route: redirect root to index.html
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // Random number
        get("/random", (req, res) -> {
            res.type("application/json");
            int randomInt = rand.nextInt(13); // 0 to 12
            return gson.toJson(Map.of("number", randomInt));
        });

        // Get Apple stock with today's price
        get("/getapple", (req, res) -> {
            res.type("application/json");
            double applePrice = Stock.fetchTodaysPrice("AAPL");
            if (applePrice < 0) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Failed to fetch Apple stock price"));
            }
            Stock appleStock = new Stock("Apple Inc.", "AAPL", "NASDAQ", applePrice);
            return gson.toJson(Map.of(
                    "symbol", appleStock.getsymbol(),
                    "company_name", appleStock.getcompany_Name(),
                    "stock_exchange", appleStock.getStock(),
                    "current_price", appleStock.getPrice()));
        });

        // API endpoint for login
        post("/login", (req, res) -> {
            System.out.println("Received /login request with body: " + req.body());
            LoginRequest lr = gson.fromJson(req.body(), LoginRequest.class);

            if ("alice".equals(lr.username) && "secret".equals(lr.password)) {
                res.type("application/json");
                return gson.toJson(new LoginResponse("a-fake-token", lr.username));
            } else {
                res.status(401);
                res.type("application/json");
                return gson.toJson(new ErrorResponse("Invalid credentials"));
            }
        });

        // Portfolio instance to manage holdings
        Portfolio portfolio = new Portfolio();

        // Pre-populate a few well-known stocks. Quantities start at 0.
        List<Stock> known = new ArrayList<>();
        known.add(new Stock("Apple Inc.", "AAPL", "NASDAQ", 0.0));
        known.add(new Stock("Alphabet Inc.", "GOOGL", "NASDAQ", 0.0));
        known.add(new Stock("Microsoft Corporation", "MSFT", "NASDAQ", 0.0));
        known.add(new Stock("Amazon.com, Inc.", "AMZN", "NASDAQ", 0.0));
        known.add(new Stock("Tesla, Inc.", "TSLA", "NASDAQ", 0.0));

        // Try fetching today's price for each known stock and add to portfolio
        for (Stock s : known) {
            double price = Stock.fetchTodaysPrice(s.getsymbol());
            if (price < 0) {
                // fallback: small random price so UI can still show something
                price = 50 + rand.nextDouble() * 150; // $50 - $200
            }
            s.setPrice(price);
            portfolio.addStock(s, 0);
        }

        // Endpoint to fetch a single stock's current price and info
        get("/stock/:symbol", (req, res) -> {
            res.type("application/json");
            String symbol = req.params("symbol");
            if (symbol == null || symbol.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Missing symbol"));
            }
            String up = symbol.toUpperCase();
            // Lookup known details from portfolio if available
            Stock existing = portfolio.getStock(up);
            String company = existing == null ? up : existing.getcompany_Name();
            String exchange = existing == null ? "" : existing.getStock();

            double price = Stock.fetchTodaysPrice(up);
            if (price < 0) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Failed to fetch price for " + up));
            }
            Stock returnStock = new Stock(company, up, exchange, price);
            return gson.toJson(Map.of(
                    "symbol", returnStock.getsymbol(),
                    "company_name", returnStock.getcompany_Name(),
                    "stock_exchange", returnStock.getStock(),
                    "current_price", returnStock.getPrice()));
        });

        // List known stocks (pre-populated) with latest prices
        get("/stocks/known", (req, res) -> {
            res.type("application/json");
            List<Map<String, Object>> out = new ArrayList<>();
            for (String sym : portfolio.getHoldings().keySet()) {
                Stock s = portfolio.getStock(sym);
                double price = Stock.fetchTodaysPrice(sym);
                if (price < 0) {
                    price = s == null ? 0.0 : s.getPrice();
                }
                out.add(Map.of(
                        "symbol", sym,
                        "company_name", s == null ? null : s.getcompany_Name(),
                        "stock_exchange", s == null ? null : s.getStock(),
                        "current_price", price));
            }
            return gson.toJson(out);
        });

        // --- CRUD for stocks ---
        // List all holdings
        get("/api/stocks", (req, res) -> {
            res.type("application/json");
            Map<String, Integer> holdings = portfolio.getHoldings();
            // Create a simple response array
            return gson.toJson(holdings.entrySet().stream().map(e -> {
                String sym = e.getKey();
                Stock s = portfolio.getStock(sym);
                return Map.of(
                        "symbol", sym,
                        "company_name", s == null ? null : s.getcompany_Name(),
                        "stock_exchange", s == null ? null : s.getStock(),
                        "current_price", s == null ? null : s.getPrice(),
                        "quantity", e.getValue());
            }).toList());
        });

        // Add a stock purchase (create or increase quantity)
        post("/api/stocks", (req, res) -> {
            res.type("application/json");
            NewStockRequest nsr = gson.fromJson(req.body(), NewStockRequest.class);
            if (nsr == null || nsr.symbol == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Invalid stock data"));
            }
            Stock s = new Stock(nsr.company_name, nsr.symbol, nsr.stock_exchange, nsr.current_price);
            portfolio.addStock(s, nsr.quantity);
            res.status(201);
            return gson.toJson(Map.of("status", "ok"));
        });

        // Update stock details or quantity
        post("/api/stocks/:symbol", (req, res) -> {
            res.type("application/json");
            String symbol = req.params("symbol");
            UpdateStockRequest usr = gson.fromJson(req.body(), UpdateStockRequest.class);
            if (symbol == null || usr == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Invalid request"));
            }
            Stock existing = portfolio.getStock(symbol);
            if (existing == null) {
                // create new if details provided
                Stock s = new Stock(usr.company_name == null ? symbol : usr.company_name, symbol,
                        usr.stock_exchange == null ? "" : usr.stock_exchange,
                        usr.current_price == null ? 0.0 : usr.current_price);
                portfolio.addStock(s, usr.quantity == null ? 0 : usr.quantity);
            } else {
                if (usr.company_name != null) {
                    existing.setCompanyName(usr.company_name);
                }
                if (usr.stock_exchange != null) {
                    existing.setStockExchange(usr.stock_exchange);
                }
                if (usr.current_price != null) {
                    existing.setPrice(usr.current_price);
                }
                if (usr.quantity != null) {
                    // set quantity to provided value
                    portfolio.removeStock(symbol, Integer.MAX_VALUE); // remove completely
                    portfolio.addStock(existing, usr.quantity);
                }
            }
            return gson.toJson(Map.of("status", "ok"));
        });

        // Delete a stock entirely
        post("/api/stocks/:symbol/delete", (req, res) -> {
            res.type("application/json");
            String symbol = req.params("symbol");
            if (symbol == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Invalid symbol"));
            }
            portfolio.removeStock(symbol, Integer.MAX_VALUE);
            return gson.toJson(Map.of("status", "deleted"));
        });
    }

    static class LoginRequest {

        String username;
        String password;
    }

    static class LoginResponse {

        String token;
        String username;

        LoginResponse(String t, String u) {
            token = t;
            username = u;
        }
    }

    static class ErrorResponse {

        String error;

        ErrorResponse(String e) {
            error = e;
        }
    }

    static class NewStockRequest {

        String company_name;
        String symbol;
        String stock_exchange;
        double current_price;
        int quantity;
    }

    static class UpdateStockRequest {

        String company_name;
        String stock_exchange;
        Double current_price;
        Integer quantity;
    }
}
