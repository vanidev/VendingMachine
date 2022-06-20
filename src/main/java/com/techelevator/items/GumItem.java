package com.techelevator.items;

import java.math.BigDecimal;

public class GumItem extends Item {

    public static final String PRODUCT_TYPE_NAME = "Gum";
    public static final String DISPENSE_MESSAGE = "Chew Chew, Yum!";

    public GumItem(String slotLocation, String productName, BigDecimal price, int quantityInStock) {
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