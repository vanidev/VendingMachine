package com.techelevator.items;

public class ChipsItem extends Item {

    public static final String PRODUCT_TYPE_NAME = "Chip";
    public static final String DISPENSE_MESSAGE = "Crunch Crunch, Yum!";

    public ChipsItem(String slotLocation, String productName, double price, int quantityInStock) {
        super(slotLocation, productName, price, quantityInStock);
    }

    @Override
    public String getProductTypeName() {
        return PRODUCT_TYPE_NAME;
    }

    @Override
    public String getDispenseMessage() {
        return DISPENSE_MESSAGE;
    }
}