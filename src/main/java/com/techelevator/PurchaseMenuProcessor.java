package com.techelevator;

import com.techelevator.items.Item;
import com.techelevator.util.ActionLogger;
import com.techelevator.view.Menu;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PurchaseMenuProcessor extends MenuProcessor {

    private static final String SELECT_ITEM = "Select item: ";
    private static final String ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION = "\"%s\" is not valid selection.";
    private static final String ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY = "Not enough money.";
    private static final String ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION = "Item Sold out! Make a new selection";
    private static final String ACTION_LOG_DISPENSE_ITEM_FORMAT = "%s %s";
    private static final String CURRENT_MONEY_PROVIDED_FORMAT = System.lineSeparator() + "Current Money Provided: $%.2f" + System.lineSeparator();
    private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
    private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
    private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
    private static final Object[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION };
    private static final String ENTER_AMOUNT_IN_WHOLE_DOLLARS = "Enter amount in whole dollars: ";
    private static final String ACTION_LOG_ACTION_FEED_MONEY = "FEED MONEY";
    private static final String ACTION_LOG_ACTION_GIVE_CHANGE = "GIVE CHANGE";
    private static final String ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT = "\"%s\" is not a valid whole dollar amount.";

    private double currentMoney = 0;

    public PurchaseMenuProcessor(VendingMachine WelcomeToTheMachine, Menu menu, PrintWriter console, Scanner userInput) {
        super(WelcomeToTheMachine, menu, console, userInput);
    }

    private void purchaseMenuProductSelectionOptions() {
        displayItems();

        console.print(SELECT_ITEM);
        console.flush();
        String input = userInput.nextLine();

        if (!WelcomeToTheMachine.getInventory().containsKey(input)) {
            console.printf(ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION + System.lineSeparator(), input);
        } else {
            Item item = WelcomeToTheMachine.getInventory().get(input);
            if (currentMoney < item.getPrice()) {
                console.println(ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY);
            } else if (item.getQuantityInStock() == 0) {
                console.println(ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION);
            } else {
                currentMoney -= item.getPrice();
                item.purchase();
                console.println("Item name: " + item.getProductName() + " | " + "Item Price: " + item.getPrice() + " | " + "Money remaining: " + currentMoney);
                console.println(item.getDispenseMessage());
                ActionLogger.log(String.format(ACTION_LOG_DISPENSE_ITEM_FORMAT, item.getProductName(), item.getSlotLocation()),
                        item.getPrice(), currentMoney);
            }
        }
    }

    private void moneyFeeder() {
        while (true) {
            console.print(ENTER_AMOUNT_IN_WHOLE_DOLLARS);
            console.flush();
            String input = userInput.nextLine();
            try {
                double result = Double.parseDouble(input);
                if (result < 1 || result % 1 != 0) {
                    throw new NumberFormatException();
                }
                currentMoney += result;
                ActionLogger.log(ACTION_LOG_ACTION_FEED_MONEY, result, currentMoney);
                break;
            } catch (NumberFormatException e) {
                console.printf(ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT + System.lineSeparator(), input);
            }
        }
    }
    public Map<Integer, Integer> computeChange() {
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

    private void finishTransaction() {
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

    public void run() {
        while (true) {
            String additionalMenuText = String.format(CURRENT_MONEY_PROVIDED_FORMAT, currentMoney);
            String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS, additionalMenuText);

            if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
                moneyFeeder();
            } else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
                purchaseMenuProductSelectionOptions();
            } else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
                // finish transaction
                finishTransaction();
                break;
            }
        }
    }

}
