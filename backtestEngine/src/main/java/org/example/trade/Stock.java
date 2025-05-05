package org.example.trade;

public class Stock {
    private double price;
    private int volume;

    public int getVolume(org.example.trade.Stock stock) {
        return stock.volume;
    }

    public int getVolume() {
        return this.volume;
    }
}