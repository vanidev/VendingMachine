package com.techelevator.view;

import com.techelevator.util.ItemSoldOutVendingMachineException;
import com.techelevator.util.NotEnoughMoneyVendingMachineException;
import com.techelevator.VendingMachine;
import com.techelevator.items.Item;
import java.math.BigDecimal;
import java.util.Map;

// Class contains: Use of BigDecimal for arbitrary precision

public class PurchaseMenuProcessor extends MenuProcessor {
    private static final String SELECT_ITEM = "Select item >>> ";
    private static final String ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION = "\"%s\" is not valid selection.";
    private static final String ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY = "Not enough money.";
    private static final String ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION = "Item Sold out! Make a new selection";
    private static final String ITEM_DISPENSE_MESSAGE_FORMAT = "Dispensing item %s" + System.lineSeparator() + "Money remaining: $%.2f";
    private static final String CURRENT_MONEY_PROVIDED_FORMAT = System.lineSeparator() + "Current Money Provided: $%.2f" + System.lineSeparator();
    private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
    private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
    private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
    private static final Object[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION };
    private static final String ENTER_AMOUNT_IN_WHOLE_DOLLARS = "Enter amount in whole dollars >>> ";
    private static final String ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT = "\"%s\" is not a valid whole dollar amount.";

    public PurchaseMenuProcessor(VendingMachine WelcomeToTheMachine, Menu menu) {
        super(WelcomeToTheMachine, menu);
    }

    private void onProductSelected() {
        displayItems();

        console.println();
        console.print(SELECT_ITEM);
        console.flush();
        String slotLocation = menu.getIn().nextLine();

        if (!vendingMachine.getInventory().containsKey(slotLocation)) {
            console.printf(ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION + System.lineSeparator(), slotLocation);
        } else {
            console.println();
            try {
                Item item = vendingMachine.purchaseItem(slotLocation);
                console.printf(ITEM_DISPENSE_MESSAGE_FORMAT, getItemDescription(item), vendingMachine.getCurrentMoney());
                console.println();
                console.println(item.getDispenseMessage());
            }
            catch (NotEnoughMoneyVendingMachineException e) {
                console.println(ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY);
            }
            catch (ItemSoldOutVendingMachineException e) {
                console.println(ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION);
            }
            console.println();
        }
    }

    private void onFeedMoneySelected() {
        while (true) {
            console.print(ENTER_AMOUNT_IN_WHOLE_DOLLARS);
            console.flush();
            String input = menu.getIn().nextLine();
            try {
                BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(input));
                if (amount.compareTo(BigDecimal.valueOf(1)) < 0
                        || amount.remainder(BigDecimal.valueOf(1)).equals(BigDecimal.valueOf(0))) {
                    throw new NumberFormatException();
                }
                vendingMachine.feedMoney(amount);
                break;
            } catch (NumberFormatException e) {
                console.printf(ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT + System.lineSeparator(), input);
            }
        }
    }

    private void OnFinishTransactionSelected() {
        Map<Integer, Integer> coinCounts = vendingMachine.dispenseChange();
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
            String additionalMenuText = String.format(CURRENT_MONEY_PROVIDED_FORMAT, vendingMachine.getCurrentMoney());
            String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS, additionalMenuText);

            if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
                onFeedMoneySelected();
            } else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
                onProductSelected();
            } else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
                // finish transaction
                OnFinishTransactionSelected();
                break;
            }
        }
    }
}