package com.techelevator;

import com.techelevator.items.*;
import com.techelevator.util.ActionLogger;
import com.techelevator.util.ItemSoldOutVendingMachineException;
import com.techelevator.util.NotEnoughMoneyVendingMachineException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Class contains: Use of BigDecimal for arbitrary precision

public class VendingMachine {
    //reused pattern from Menu class. No need to reinvent the wheel.
    public static final int MAX_ITEMS_PER_SLOT = 5;
    private static final String ACTION_LOG_ACTION_FEED_MONEY = "FEED MONEY";
    private static final String ACTION_LOG_DISPENSE_ITEM_FORMAT = "%s %s";
    private static final String ACTION_LOG_ACTION_GIVE_CHANGE = "GIVE CHANGE";
    private static final String VENDING_MACHINE_INVENTORY_FILE_PATH = "vendingmachine.csv";
    private static final String ERROR_MESSAGE_BAD_INVENTORY_FILE_FORMAT = "Bad inventory file format.";
    private final Map<String, Item> inventory = new HashMap<>();
    private BigDecimal currentMoney = BigDecimal.valueOf(0);

    public BigDecimal getCurrentMoney() {
        return currentMoney;
    }

    public VendingMachine() throws Exception {
        loadItems();
    }

    public Map<String, Item> getInventory() {
        return inventory;
    }

    //initialize inventory
    private void loadItems() throws IOException {
        try(Scanner scanner = new Scanner(new File(VENDING_MACHINE_INVENTORY_FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split("\\|");
                String slotLocation = fields[0];
                String productName = fields[1];
                BigDecimal price = BigDecimal.valueOf(Double.parseDouble(fields[2]));
                String itemType = fields[3];
                Item item;

                //item format setup
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
                    throw new IOException(ERROR_MESSAGE_BAD_INVENTORY_FILE_FORMAT);
                }
                inventory.put(item.getSlotLocation(), item);
            }
        }
        catch (IOException e) {
            throw e;
        }
    }

    public void feedMoney(BigDecimal amount) {
        currentMoney = currentMoney.add(amount);
        ActionLogger.log(ACTION_LOG_ACTION_FEED_MONEY, amount, currentMoney);
    }

    public Item purchaseItem(String slotLocation)
            throws NotEnoughMoneyVendingMachineException, ItemSoldOutVendingMachineException {

        Item item = inventory.get(slotLocation);
        if (currentMoney.compareTo(item.getPrice()) < 0) {
            throw new NotEnoughMoneyVendingMachineException();
        } else if (item.getQuantityInStock() == 0) {
            throw new ItemSoldOutVendingMachineException();
        } else {
            currentMoney = currentMoney.subtract(item.getPrice());
            item.purchase();
            ActionLogger.log(String.format(ACTION_LOG_DISPENSE_ITEM_FORMAT, item.getProductName(), item.getSlotLocation()),
                    item.getPrice(), currentMoney);
            return item;
        }
    }

    public Map<Integer, Integer> dispenseChange() {
        Map<Integer, Integer> coinCounts = computeChange();
        ActionLogger.log(ACTION_LOG_ACTION_GIVE_CHANGE, currentMoney, BigDecimal.valueOf(0));
        currentMoney = BigDecimal.valueOf(0);
        return coinCounts;
    }

    // Implementation 'Algorithm' makes use of the least amount of coins.
    private Map<Integer, Integer> computeChange() {
        Map<Integer, Integer> coinCounts = new HashMap<>();

        //quarters
        BigDecimal remainingMoney = new BigDecimal(currentMoney.toString());
        int coinCount = remainingMoney.divide(new BigDecimal("0.25"), RoundingMode.UNNECESSARY).intValue();
        remainingMoney = remainingMoney.subtract(BigDecimal.valueOf(coinCount).multiply(new BigDecimal( "0.25")));

        if (coinCount > 0) {
            coinCounts.put(25, coinCount);
        }

        //dimes
        coinCount = remainingMoney.divide(new BigDecimal("0.10"), RoundingMode.UNNECESSARY).intValue();
        remainingMoney = remainingMoney.subtract(BigDecimal.valueOf(coinCount).multiply(new BigDecimal( "0.10")));

        if (coinCount > 0) {
            coinCounts.put(10, coinCount);
        }

        //nickles
        coinCount = remainingMoney.divide( new BigDecimal("0.05"), RoundingMode.UNNECESSARY).intValue();
        if (coinCount > 0) {
            coinCounts.put(5, coinCount);
        }

        return coinCounts;
    }
}