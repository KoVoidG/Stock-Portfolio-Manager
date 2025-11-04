package org.global.academy;

import java.util.Map;
import java.util.Random;

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
            int randomInt = rand.nextInt(10) + 1; // 1 to 10
            return gson.toJson(Map.of("number", randomInt));
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
}
