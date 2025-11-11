package org.global.academy;

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

}
