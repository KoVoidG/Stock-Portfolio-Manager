package org.global.academy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    private static final String JWT_SECRET = "your-secret-key-change-this-in-production";
    private static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours
    private static final Map<String, String> validTokens = new ConcurrentHashMap<>(); // token -> username

    public static void main(String[] args) {
        try {
            mainImpl(args);
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Server crashed!");
            System.err.println("Exception: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void mainImpl(String[] args) {
        port(8080);
        Random rand = new Random();

        // DO NOT serve static files publicly - we control access through routes
        // staticFiles.location("/public");
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

        // Default route: serve index.html
        get("/", (req, res) -> {
            try {
                res.type("text/html");
                return readFileContent("public/index.html");
            } catch (Exception e) {
                res.status(500);
                return "Error loading page: " + e.getMessage();
            }
        });

        // Public pages (no auth required)
        get("/index.html", (req, res) -> {
            try {
                res.type("text/html");
                return readFileContent("public/index.html");
            } catch (Exception e) {
                res.status(500);
                return "Error loading page: " + e.getMessage();
            }
        });

        get("/login.html", (req, res) -> {
            try {
                res.type("text/html");
                return readFileContent("public/login.html");
            } catch (Exception e) {
                res.status(500);
                return "Error loading page: " + e.getMessage();
            }
        });

        // Serve static assets (images, CSS, etc.) - no auth required
        get("/logo.jpg", (req, res) -> {
            try {
                res.type("image/jpeg");
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                java.io.InputStream input = classLoader.getResourceAsStream("public/logo.jpg");
                if (input == null) {
                    res.status(404);
                    return "Logo not found";
                }
                byte[] imageBytes = input.readAllBytes();
                res.raw().getOutputStream().write(imageBytes);
                res.raw().getOutputStream().flush();
                return "";
            } catch (Exception e) {
                res.status(500);
                return "Error loading logo: " + e.getMessage();
            }
        });

        // Serve GIF files - no auth required
        get("/cat-pixel-art.gif", (req, res) -> {
            try {
                res.type("image/gif");
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                java.io.InputStream input = classLoader.getResourceAsStream("public/cat-pixel-art.gif");
                if (input == null) {
                    res.status(404);
                    return "GIF not found";
                }
                byte[] gifBytes = input.readAllBytes();
                res.raw().getOutputStream().write(gifBytes);
                res.raw().getOutputStream().flush();
                return "";
            } catch (Exception e) {
                res.status(500);
                return "Error loading GIF: " + e.getMessage();
            }
        });

        // Protected pages - server-side auth check required
        get("/welcome.html", (req, res) -> {
            String token = req.queryParams("token");
            String username = req.queryParams("username");
            if (token == null || token.isEmpty() || username == null || username.isEmpty()
                    || !validTokens.containsKey(token) || !validTokens.get(token).equals(username)) {
                res.redirect("/login.html");
                return null;
            }
            try {
                res.type("text/html");
                return readFileContent("public/welcome.html");
            } catch (Exception e) {
                res.status(500);
                return "Error loading page: " + e.getMessage();
            }
        });

        get("/portfolio.html", (req, res) -> {
            String token = req.queryParams("token");
            String username = req.queryParams("username");
            if (token == null || token.isEmpty() || username == null || username.isEmpty()
                    || !validTokens.containsKey(token) || !validTokens.get(token).equals(username)) {
                res.redirect("/login.html");
                return null;
            }
            try {
                res.type("text/html");
                return readFileContent("public/portfolio.html");
            } catch (Exception e) {
                res.status(500);
                return "Error loading page: " + e.getMessage();
            }
        });

        get("/stock_app.html", (req, res) -> {
            String token = req.queryParams("token");
            String username = req.queryParams("username");
            if (token == null || token.isEmpty() || username == null || username.isEmpty()
                    || !validTokens.containsKey(token) || !validTokens.get(token).equals(username)) {
                res.redirect("/login.html");
                return null;
            }
            try {
                res.type("text/html");
                return readFileContent("public/stock_app.html");
            } catch (Exception e) {
                res.status(500);
                return "Error loading page: " + e.getMessage();
            }
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
                String token = UUID.randomUUID().toString();
                validTokens.put(token, lr.username); // Store the valid token
                System.out.println("Login successful for user: " + lr.username + ", token: " + token);
                return gson.toJson(new LoginResponse(token, lr.username));
            } else {
                res.status(401);
                res.type("application/json");
                return gson.toJson(new ErrorResponse("Invalid credentials"));
            }
        });

        // API endpoint to check if user is authenticated
        get("/api/auth/check", (req, res) -> {
            res.type("application/json");
            String token = req.headers("Authorization");
            String username = req.headers("X-Username");

            if (token != null && !token.isEmpty() && validTokens.containsKey(token) && username != null
                    && !username.isEmpty() && validTokens.get(token).equals(username)) {
                return gson.toJson(Map.of("authenticated", true, "username", username));
            } else {
                res.status(401);
                return gson.toJson(Map.of("authenticated", false));
            }
        });

        // Portfolio instance to manage holdings
        Portfolio portfolio = new Portfolio();

        // Load persisted portfolio data
        PortfolioPersistence.loadPortfolio(portfolio);

        // Pre-populate a few well-known stocks. Quantities start at 0.
        List<Stock> known = new ArrayList<>();
        known.add(new Stock("Apple Inc.", "AAPL", "NASDAQ", 0.0));
        known.add(new Stock("Alphabet Inc.", "GOOGL", "NASDAQ", 0.0));
        known.add(new Stock("Microsoft Corporation", "MSFT", "NASDAQ", 0.0));
        known.add(new Stock("Amazon.com, Inc.", "AMZN", "NASDAQ", 0.0));
        known.add(new Stock("Tesla, Inc.", "TSLA", "NASDAQ", 0.0));
        known.add(new Stock("Meta Platforms, Inc.", "META", "NASDAQ", 0.0));
        known.add(new Stock("Netflix, Inc.", "NFLX", "NASDAQ", 0.0));
        known.add(new Stock("NVIDIA Corporation", "NVDA", "NASDAQ", 0.0));

        // Try fetching today's price for each known stock and add to portfolio if not
        // already there
        try {
            for (Stock s : known) {
                if (portfolio.getStock(s.getsymbol()) == null) {
                    // Not yet in portfolio, add it
                    try {
                        double price = Stock.fetchTodaysPrice(s.getsymbol());
                        if (price < 0) {
                            // fallback: small random price so UI can still show something
                            price = 50 + rand.nextDouble() * 150; // $50 - $200
                        }
                        s.setPrice(price);
                        portfolio.addStock(s, 0);
                    } catch (Exception e) {
                        System.err.println("Failed to fetch price for " + s.getsymbol() + ": " + e.getMessage());
                        // Use fallback price
                        s.setPrice(50 + rand.nextDouble() * 150);
                        portfolio.addStock(s, 0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during stock initialization: " + e.getMessage());
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

            // Get quantity and bought price if owned
            int quantity = 0;
            double boughtPrice = 0.0;
            if (existing != null && portfolio.getHoldings().containsKey(up)) {
                quantity = portfolio.getHoldings().get(up);
                boughtPrice = existing.getPrice(); // In this simple model, stored price is bought price
            }

            double price = Stock.fetchTodaysPrice(up);
            if (price < 0) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Failed to fetch price for " + up));
            }

            // We return a map to include extra fields not in Stock class
            return gson.toJson(Map.of(
                    "symbol", up,
                    "company_name", company,
                    "stock_exchange", exchange,
                    "current_price", price,
                    "bought_price", boughtPrice,
                    "quantity", quantity));
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
                // Fetch current price dynamically
                double currentPrice = Stock.fetchTodaysPrice(sym);
                if (currentPrice < 0) {
                    currentPrice = s == null ? 0.0 : s.getPrice();
                }
                // Get bought price from stock object
                double boughtPrice = s == null ? 0.0 : s.getPrice();
                return Map.of(
                        "symbol", sym,
                        "company_name", s == null ? null : s.getcompany_Name(),
                        "stock_exchange", s == null ? null : s.getStock(),
                        "bought_price", boughtPrice,
                        "current_price", currentPrice,
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
            PortfolioPersistence.savePortfolio(portfolio);
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
            PortfolioPersistence.savePortfolio(portfolio);
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
            PortfolioPersistence.savePortfolio(portfolio);
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

    private static String readFileContent(String resourcePath) throws Exception {
        // Read from classpath resources
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        java.io.InputStream input = classLoader.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }
        try (input) {
            return new String(input.readAllBytes());
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
