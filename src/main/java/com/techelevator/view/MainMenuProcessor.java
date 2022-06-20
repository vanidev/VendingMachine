package com.techelevator.view;

import com.techelevator.VendingMachine;

public class MainMenuProcessor extends MenuProcessor{
    private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
    private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
    private static final String MAIN_MENU_OPTION_EXIT = "Exit";
    private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };


    public MainMenuProcessor(VendingMachine WelcomeToTheMachine, Menu menu) {
        super(WelcomeToTheMachine, menu);
    }

    @Override
    public void run() {
        PurchaseMenuProcessor purchaseMenuProcessor = new PurchaseMenuProcessor(vendingMachine, menu);

        while (true) {
            String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
                console.println();
                displayItems();
            } else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
                purchaseMenuProcessor.run();
            } else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
                break;
            }
        }
    }
}
