package com.pastryapi.exception;

import java.util.List;

public class InsufficientStockException extends RuntimeException {
    private final List<StockShortage> shortages;

    public InsufficientStockException(String message, List<StockShortage> shortages) {
        super(message);
        this.shortages = shortages;
    }

    public List<StockShortage> getShortages() {
        return shortages;
    }
}
