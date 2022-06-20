package com.techelevator;

import com.techelevator.items.*;
import com.techelevator.util.ActionLogger;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {
    private static final String ACTION_LOG_ACTION_FEED_MONEY = "FEED MONEY";
    private static final String ACTION_LOG_DISPENSE_ITEM_FORMAT = "%s %s";
    private static final String ACTION_LOG_ACTION_GIVE_CHANGE = "GIVE CHANGE";
    private static final String ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY = "Not enough money.";
    private static final String ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION = "Item Sold out! Make a new selection";
    private static final String ITEM_DESCRIPTION_FORMAT = "%s: %s %s $%.2f";
    private static final String ITEM_DISPENSE_MESSAGE_FORMAT = "Dispensing item %s" + System.lineSeparator() + "Money remaining: $%.2f";
    public static final int MAX_ITEMS_PER_SLOT = 5;
    private static final String VENDING_MACHINE_INVENTORY_FILE_PATH = "vendingmachine.csv";

    private final Map<String, Item> inventory = new HashMap<>();
    private double currentMoney = 0;
    private final PrintWriter console;

    public PrintWriter getConsole() {
        return console;
    }

    public double getCurrentMoney() {
        return currentMoney;
    }

    public VendingMachine(PrintWriter console) throws Exception {
        this.console = console;
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

    public void feedMoney(double amount) {
        currentMoney += amount;
        ActionLogger.log(ACTION_LOG_ACTION_FEED_MONEY, amount, currentMoney);
    }

    public void purchaseItem(String slotLocation) {
        Item item = inventory.get(slotLocation);
        if (currentMoney < item.getPrice()) {
            console.println(ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY);
        } else if (item.getQuantityInStock() == 0) {
            console.println(ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION);
        } else {
            currentMoney -= item.getPrice();
            item.purchase();
            console.printf(ITEM_DISPENSE_MESSAGE_FORMAT, getItemDescription(item), currentMoney);
            console.println();
            console.println(item.getDispenseMessage());
            ActionLogger.log(String.format(ACTION_LOG_DISPENSE_ITEM_FORMAT, item.getProductName(), item.getSlotLocation()),
                    item.getPrice(), currentMoney);
        }
    }

    public void dispenseChange() {
        Map<Integer, Integer> coinCounts = computeChange();
        ActionLogger.log(ACTION_LOG_ACTION_GIVE_CHANGE, currentMoney, 0.0);
        currentMoney = 0;
        if(!coinCounts.isEmpty()) {
            String coinText = "";
            if(coinCounts.containsKey(25)) {
                int coinCount = coinCounts.get(25);
                coinText += coinCount + (coinCount == 1 ? " quarter" : " quarters");
            }
            if(coinCounts.containsKey(10)) {
                int coinCount = coinCounts.get(10);
                if(!coinText.isEmpty()) {
                    coinText += ", ";
                }
                coinText += coinCount + (coinCount == 1 ? " dime" : " dimes");
            }
            if(coinCounts.containsKey(5)) {
                int coinCount = coinCounts.get(5);
                if(!coinText.isEmpty()) {
                    coinText += ", ";
                }
                coinText += coinCount + (coinCount == 1 ? " nickle" : " nickles");
            }
            console.println();
            console.print("Dispensing change: " + coinText);
            console.println();
        }
    }

    private Map<Integer, Integer> computeChange() {
        Map<Integer, Integer> coinCounts = new HashMap<>();

        //quarters
        double remainingMoney = currentMoney;
        int coinCount = (int)(remainingMoney / .25);
        remainingMoney -= coinCount * .25;

        if (coinCount > 0) {
            coinCounts.put(25, coinCount);
        }

        //dimes
        coinCount = (int)(remainingMoney / .10);
        remainingMoney -= coinCount * .10;
        if (coinCount > 0) {
            coinCounts.put(10, coinCount);
        }

        //nickles
        coinCount = (int)(remainingMoney / .05);
        if (coinCount > 0) {
            coinCounts.put(5, coinCount);
        }

        return coinCounts;
    }

    public String getItemDescription(Item item) {
        return String.format(ITEM_DESCRIPTION_FORMAT,
                item.getSlotLocation(),
                item.getProductName(),
                item.getProductTypeName(),
                item.getPrice()
        );
    }
}
