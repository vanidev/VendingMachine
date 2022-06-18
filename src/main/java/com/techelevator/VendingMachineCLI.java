package com.techelevator;

import com.techelevator.items.*;
import com.techelevator.util.ActionLogger;
import com.techelevator.view.Menu;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class VendingMachineCLI {

	private static final String SELECT_ITEM = "Select item: ";
	private static final int MAX_ITEMS_PER_SLOT = 5;
	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };
	private static final String ITEM_LIST_ENTRY_FORMAT = "%s: %s %s $%.2f (%s Available)";
	private static final String ITEM_LIST_ENTRY_SOLD_OUT_QUANTITY_TEXT = "SOLD OUT";
	private static final String VENDING_MACHINE_INVENTORY_FILE_PATH = "vendingmachine.csv";
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final Object[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION };
	private static final String CURRENT_MONEY_PROVIDED_FORMAT = System.lineSeparator() + "Current Money Provided: $%.2f" + System.lineSeparator();
	private static final String ENTER_AMOUNT_IN_WHOLE_DOLLARS = "Enter amount in whole dollars: ";
	private static final String ITEM_SOLD_OUT_MAKE_A_NEW_SELECTION = "Item Sold out! Make a new selection";
	private static final String ERROR_MESSAGE_FORMAT_NOT_ENOUGH_MONEY = "Not enough money.";
	private static final String ACTION_LOG_ACTION_FEED_MONEY = "FEED MONEY";
	private static final String ACTION_LOG_ACTION_GIVE_CHANGE = "GIVE CHANGE";
	private static final String ACTION_LOG_DISPENSE_ITEM_FORMAT = "%s %s";
	private static final String ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT = "\"%s\" is not a valid whole dollar amount.";
	private static final String ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION = "\"%s\" is not valid selection.";
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
								ActionLogger.log(ACTION_LOG_ACTION_FEED_MONEY, result, currentMoney);
								break;
							} catch (NumberFormatException e) {
								console.printf(ERROR_MESSAGE_FORMAT_INVALID_WHOLE_DOLLAR_AMOUNT + System.lineSeparator(), input);
							}
						}
					} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						displayItems();

						console.print(SELECT_ITEM);
						console.flush();
						String input = userInput.nextLine();

						if (!inventory.containsKey(input)) {
							console.printf(ERROR_MESSAGE_FORMAT_INVALID_PRODUCT_SELECTION + System.lineSeparator(), input);
						} else {
							Item item = inventory.get(input);
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
					} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						// finish transaction
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
		String[] sortedSlotLocations = inventory.keySet().toArray(new String[] {});
		Arrays.sort(sortedSlotLocations);
		for (String slotLocation : sortedSlotLocations) {
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
			menu.getOut().println();
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