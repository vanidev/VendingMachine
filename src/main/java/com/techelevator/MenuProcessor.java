package com.techelevator;

import com.techelevator.items.Item;
import com.techelevator.view.Menu;

import java.util.Arrays;

public abstract class MenuProcessor {
    protected final Menu menu;
    private static final String ITEM_LIST_ENTRY_FORMAT = "%s (%s Available)";
    private static final String ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT = "SOLD OUT";
    protected final VendingMachine vendingMachine;


    protected MenuProcessor(VendingMachine vendingMachine, Menu menu) {
        this.vendingMachine = vendingMachine;
        this.menu = menu;
    }

    protected void displayItems() {
        String[] sortedSlotLocations = vendingMachine.getInventory().keySet().toArray(new String[] {});
        Arrays.sort(sortedSlotLocations);
        for (String slotLocation : sortedSlotLocations) {
            Item item = vendingMachine.getInventory().get(slotLocation);
            menu.getOut().printf(ITEM_LIST_ENTRY_FORMAT,
                    vendingMachine.getItemDescription(item),
                    item.getQuantityInStock() > 0
                            ? Integer.toString(item.getQuantityInStock())
                            : ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT
            );
            menu.getOut().println();
        }
    }

    public abstract void run();
}
