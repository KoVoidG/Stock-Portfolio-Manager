package org.global.academy;

public class Stock {
    private String company_name; //member
    private String symbol;
    private String stock_exchange;
    private double current_price;

    public Stock(String name,String short_symbol,String stock, double price){
        company_name = name;
        symbol = short_symbol;
        stock_exchange = stock;
        current_price = price;
    }
    public String getcompany_Name()
    {
        return company_name;
    }
    public String getsymbol()
    {
        return symbol;
    }
    public String getStock ()
    {
        return stock_exchange;
    }
    public double getPrice()
    {
        return current_price;
    }


    
}

