package com.techelevator.view;

import com.techelevator.VendingMachine;
import com.techelevator.items.Item;
import com.techelevator.view.Menu;

import java.io.PrintWriter;
import java.util.Arrays;

public abstract class MenuProcessor {
    protected final Menu menu;
    protected final PrintWriter console;
    protected final VendingMachine vendingMachine;
    private static final String ITEM_LIST_ENTRY_FORMAT = "%s (%s Available)";
    private static final String ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT = "SOLD OUT";
    private static final String ITEM_DESCRIPTION_FORMAT = "%s: %s %s $%.2f";

    protected MenuProcessor(VendingMachine vendingMachine, Menu menu) {
        this.vendingMachine = vendingMachine;
        this.menu = menu;
        this.console = menu.getOut();
    }

    public String getItemDescription(Item item) {
        return String.format(ITEM_DESCRIPTION_FORMAT,
                item.getSlotLocation(),
                item.getProductName(),
                item.getProductTypeName(),
                item.getPrice()
        );
    }

    protected void displayItems() {
        String[] sortedSlotLocations = vendingMachine.getInventory().keySet().toArray(new String[] {});
        Arrays.sort(sortedSlotLocations);
        for (String slotLocation : sortedSlotLocations) {
            Item item = vendingMachine.getInventory().get(slotLocation);
            menu.getOut().printf(ITEM_LIST_ENTRY_FORMAT,
                    getItemDescription(item),
                    item.getQuantityInStock() > 0
                            ? Integer.toString(item.getQuantityInStock())
                            : ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT
            );
            menu.getOut().println();
        }
    }

    public abstract void run();
}