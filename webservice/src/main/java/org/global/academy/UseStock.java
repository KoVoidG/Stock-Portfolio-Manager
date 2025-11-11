package org.global.academy;

public class UseStock {

    public static void main(String[] args) {
        Portfolio p = new Portfolio();
        Stock s1 = new Stock("Acme Corporation", "ACME", "NYSE", 123.45);
        Stock s2 = new Stock("Globex", "GLBX", "NASDAQ", 98.7);
        p.addStock(s1, 10);
        p.addStock(s2, 5);
        System.out.println("Initial portfolio:");
        p.showPortfolio();

        p.removeStock("ACME", 5);
        System.out.println("After selling 5 ACME:");
        p.showPortfolio();

        System.out.printf("Total value: %.2f\n", p.getValue());
    }
}
