package org.example.trade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSeries {
    private final String symbol;
    private final List<StockBar> bars;
    private final LocalDate startDate;
    private final LocalDate endDate;
    public TimeSeries(String symbol, List<StockBar> bars){
        this.symbol = symbol;
        this.bars = new ArrayList<>(bars);

        this.startDate = bars.isEmpty() ? null : bars.get(0).getDate();
        this.endDate = bars.isEmpty() ? null : bars.get(bars.size() - 1).getDate();
    }
    public String getSymbol(){
        return symbol;
    }
    public List<StockBar> getBars(){
        return bars;
    }
    public LocalDate getStartDate(){
        return startDate;
    }
    public LocalDate getEndDate(){
        return endDate;
    }
    public int getBarCount(){
        return bars.size();
    }
}
