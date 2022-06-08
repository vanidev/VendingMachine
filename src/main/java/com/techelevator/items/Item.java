package com.techelevator.items;

public abstract class Item {
    private String slotLocation;
    private String productName;
    private double price;
    private int quantityInStock;

    public String getSlotLocation() {
        return slotLocation;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getProductTypeName();

    public abstract String getDispenseMessage();

    protected Item(String slotLocation, String productName, double price, int quantityInStock) {
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