package com.techelevator;

import com.techelevator.items.*;
import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VendingMachineCLI {

	public static final String SELECT_ITEM = "Select item: ";
	private static final int MAX_ITEMS_PER_SLOT = 5;
	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };
	private static final String ITEM_LIST_ENTRY_FORMAT = "%s|%s|%s|%.2f|%s\n";
	private static final String ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT = "SOLD OUT";
	private static final String VENDING_MACHINE_INVENTORY_FILE_PATH = "vendingmachine.csv";
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final Object[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION };
	private static final String CURRENT_MONEY_PROVIDED_FORMAT = "\nCurrent Money Provided: $%.2f \n";
	public static final String ENTER_AMOUNT_IN_WHOLE_DOLLARS = "Enter amount in whole dollars: ";
	public static final String ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION = "Item Sold out! Make a new selection";
	public static final String NOT_ENOUGH_MONEY = " not enough money";
	private double currentMoney = 0;

	private Menu menu;
	private Map<String, Item> inventory = new HashMap<>();

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() throws Exception {
		Scanner userInput = menu.getIn();
		PrintWriter console = menu.getOut();
		loadItems();

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				console.println();
				displayItems();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				while (true) {
					String additionalMenuText = String.format(CURRENT_MONEY_PROVIDED_FORMAT, currentMoney);
					choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS, additionalMenuText);

					if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
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
								break;
							} catch (NumberFormatException e) {
								console.println(input + " is not a valid whole dollar amount.");
							}
						}
					} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						displayItems();

						console.print(SELECT_ITEM);
						console.flush();
						String input = userInput.nextLine();

						if (!inventory.containsKey(input)) {
							console.println(input + " is not valid selection");
						} else {
							Item item = inventory.get(input);
							if (currentMoney < item.getPrice()) {
								console.println(NOT_ENOUGH_MONEY);
							} else if (item.getQuantityInStock() == 0) {
								console.println(ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION);
							} else {
								currentMoney -= item.getPrice();
								item.purchase();
								console.println("Item name: " + item.getProductName() + " | " + "Item Price: " + item.getPrice() + " | " + "Money remaining: " + currentMoney);
								console.println(item.getDispenseMessage());
							}
						}
					} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						// finish transaction
						break;
					}
				}
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				break;
			}
		}
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
		try {
			Menu menu = new Menu(System.in, System.out);
			VendingMachineCLI cli = new VendingMachineCLI(menu);
			cli.run();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}