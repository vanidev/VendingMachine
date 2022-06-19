package com.techelevator;

import com.techelevator.items.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {

    private Map<String, Item> inventory = new HashMap<>();
    private double currentMoney = 0;
    private static final int MAX_ITEMS_PER_SLOT = 5;
    private static final String VENDING_MACHINE_INVENTORY_FILE_PATH = "vendingmachine.csv";

    public VendingMachine() throws Exception {
        loadItems();
    }

    public Map<String, Item> getInventory() {
        return inventory;
    }

    private void loadItems() throws Exception {
        try(Scanner scanner = new Scanner(new File(VENDING_MACHINE_INVENTORY_FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split("\\|");
                String slotLocation = fields[0];
                String productName = fields[1];
                double price = Double.parseDouble(fields[2]);
                String itemType = fields[3];
                Item item;
                if (itemType.equalsIgnoreCase(CandyItem.PRODUCT_TYPE_NAME)) {
                    item = new CandyItem(slotLocation, productName, price, MAX_ITEMS_PER_SLOT);
                }
                else if (itemType.equalsIgnoreCase(ChipsItem.PRODUCT_TYPE_NAME)) {
                    item = new ChipsItem(slotLocation, productName, price, MAX_ITEMS_PER_SLOT);
                }
                else if (itemType.equalsIgnoreCase(DrinkItem.PRODUCT_TYPE_NAME)) {
                    item = new DrinkItem(slotLocation, productName, price, MAX_ITEMS_PER_SLOT);
                }
                else if (itemType.equalsIgnoreCase(GumItem.PRODUCT_TYPE_NAME)) {
                    item = new GumItem(slotLocation, productName, price, MAX_ITEMS_PER_SLOT);
                }
                else {
                    throw new Exception("Bad inventory file format.");
                }
                inventory.put(item.getSlotLocation(), item);
            }
        }
        catch (Exception e) {
            throw e;
        }
    }
}
