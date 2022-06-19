package com.techelevator;

import com.techelevator.view.Menu;

import java.io.PrintWriter;
import java.util.*;

public class VendingMachineCLI {
	private Menu menu;
	private final PrintWriter console;
	private final Scanner userInput;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.console = menu.getOut();
		this.userInput = menu.getIn();
	}

	public void run() throws Exception {
		VendingMachine vending = new VendingMachine();
		MainMenuProcessor mainMenuProcessor = new MainMenuProcessor(vending, menu, console, userInput);
		mainMenuProcessor.run();
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