package com.techelevator;

import com.techelevator.view.MainMenuProcessor;
import com.techelevator.view.Menu;
import java.io.PrintWriter;

public class VendingMachineCLI {
	private final Menu menu;


	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() throws Exception {
		VendingMachine vending = new VendingMachine(); // "Welcome to the Machine!"
		MainMenuProcessor mainMenuProcessor = new MainMenuProcessor(vending, menu);
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