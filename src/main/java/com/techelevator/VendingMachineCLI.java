package com.techelevator;

import com.techelevator.items.*;
import com.techelevator.view.Menu;

import java.util.HashMap;
import java.util.Map;

public class VendingMachineCLI {

	private static final int MAX_ITEMS_PER_SLOT = 5;
	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };
	private static final String ITEM_LIST_ENTRY_FORMAT = "%s|%s|%s|%.2f|%s\n";
	private static final String ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT = "SOLD OUT";

	private Menu menu;
	private Map<String, Item> inventory = new HashMap<>();

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() {
		loadItems();

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				menu.getOut().println();
				displayItems();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				break;
			}
		}
	}

	private void loadItems() {
		inventory.put("A1", new ChipsItem("A1", "Potato Crisps", 3.05, MAX_ITEMS_PER_SLOT));
		inventory.put("B1", new CandyItem("B1", "Moonpie", 1.80, 3));
		inventory.put("B2", new CandyItem("B2", "Cowtales", 1.50, 0));
		inventory.put("C1", new DrinkItem("C1", "Cola", 1.25, 4));
	}

	private void displayItems() {
		for (String slotLocation : inventory.keySet()) {
			Item item = inventory.get(slotLocation);
			menu.getOut().printf(ITEM_LIST_ENTRY_FORMAT,
					item.getSlotLocation(),
					item.getProductName(),
					item.getProductTypeName(),
					item.getPrice(),
					item.getQuantityInStock() > 0
							? Integer.toString(item.getQuantityInStock())
							: ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT
			);
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}