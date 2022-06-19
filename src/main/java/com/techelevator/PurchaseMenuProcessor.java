package com.techelevator;

import com.techelevator.view.Menu;

public class PurchaseMenuProcessor extends MenuProcessor {

    private static final String SELECT_ITEM = "Select item >>> ";
    private static final String ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION = "\"%s\" is not valid selection.";
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

        vendingMachine.getConsole().println();
        vendingMachine.getConsole().print(SELECT_ITEM);
        vendingMachine.getConsole().flush();
        String slotLocation = menu.getIn().nextLine();

        if (!vendingMachine.getInventory().containsKey(slotLocation)) {
            vendingMachine.getConsole().printf(ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION + System.lineSeparator(), slotLocation);
        } else {
            vendingMachine.getConsole().println();
            vendingMachine.purchaseItem(slotLocation);
            vendingMachine.getConsole().println();
        }
    }

    private void onFeedMoneySelected() {
        while (true) {
            vendingMachine.getConsole().print(ENTER_AMOUNT_IN_WHOLE_DOLLARS);
            vendingMachine.getConsole().flush();
            String input = menu.getIn().nextLine();
            try {
                double amount = Double.parseDouble(input);
                if (amount < 1 || amount % 1 != 0) {
                    throw new NumberFormatException();
                }
                vendingMachine.feedMoney(amount);
                break;
            } catch (NumberFormatException e) {
                vendingMachine.getConsole().printf(ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT + System.lineSeparator(), input);
            }
        }
    }

    private void OnFinishTransactionSelected() {
        vendingMachine.dispenseChange();
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