package com.techelevator.items;

import java.math.BigDecimal;

// Class contains: Use of BigDecimal for arbitrary precision

public abstract class Item {
    private String slotLocation;
    private String productName;
    private BigDecimal price;
    private int quantityInStock;

    public String getSlotLocation() {
        return slotLocation;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public abstract String getProductTypeName();

    public abstract String getDispenseMessage();

    protected Item(String slotLocation, String productName, BigDecimal price, int quantityInStock) {
        this.slotLocation = slotLocation;
        this.productName = productName;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void purchase() {
        if (quantityInStock > 0) {
            quantityInStock--;
        }
    }
}