package com.techelevator;

import com.techelevator.items.Item;
import com.techelevator.view.Menu;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public abstract class MenuProcessor {
    protected final Menu menu;
    protected final PrintWriter console;
    protected final Scanner userInput;
    private static final String ITEM_LIST_ENTRY_FORMAT = "%s: %s %s $%.2f (%s Available)";
    private static final String ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT = "SOLD OUT";
    protected final VendingMachine WelcomeToTheMachine;


    protected MenuProcessor(VendingMachine WelcomeToTheMachine, Menu menu, PrintWriter console, Scanner userInput) {
        this.WelcomeToTheMachine = WelcomeToTheMachine;
        this.menu = menu;
        this.console = console;
        this.userInput = userInput;
    }

    protected void displayItems() {
        String[] sortedSlotLocations = WelcomeToTheMachine.getInventory().keySet().toArray(new String[] {});
        Arrays.sort(sortedSlotLocations);
        for (String slotLocation : sortedSlotLocations) {
            Item item = WelcomeToTheMachine.getInventory().get(slotLocation);
            menu.getOut().printf(ITEM_LIST_ENTRY_FORMAT,
                    item.getSlotLocation(),
                    item.getProductName(),
                    item.getProductTypeName(),
                    item.getPrice(),
                    item.getQuantityInStock() > 0
                            ? Integer.toString(item.getQuantityInStock())
                            : ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT
            );
            menu.getOut().println();
        }
    }

    public abstract void run();
}
