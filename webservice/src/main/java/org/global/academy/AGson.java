package org.global.academy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class AGson {
    public static void main(String[] args) {
        String urlString = "https://query1.finance.yahoo.com/v8/finance/chart/aapl";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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
            System.out.println("AAPL Current Price: $" + price);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

