package com.techelevator;

import com.techelevator.items.Item;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class VendingMachineTest {

    private ByteArrayOutputStream output;
    private PrintWriter console;
    VendingMachine vendingMachine;

    @Before
    public void setup() throws Exception {
        output = new ByteArrayOutputStream();
        console = new PrintWriter(output);
        vendingMachine = new VendingMachine(console);
    }


    @Test
    public void current_money_is_zero_at_initialization() {
            Assert.assertEquals("Current money is not initialized.",vendingMachine.getCurrentMoney(),0, 0);
    }

    @Test
    public void all_slots_are_full_at_initialization() {

        boolean allSlotsFull = true;

        if(vendingMachine.getInventory().isEmpty()){

            allSlotsFull = false;

        } else {
            for( String slotLocation: vendingMachine.getInventory().keySet()) {

                Item item = vendingMachine.getInventory().get(slotLocation);

                if(item.getQuantityInStock() < VendingMachine.MAX_ITEMS_PER_SLOT) {
                    allSlotsFull = false;
                    break;
                }
            }
        }
        Assert.assertTrue("All slots are not full at initialization.",allSlotsFull);
    }

    @Test
    public void is_money_fed () {
        double startingMoney = 5.0;

        vendingMachine.feedMoney(startingMoney);

        Assert.assertEquals("Not the correct amount.", 5, vendingMachine.getCurrentMoney() , 0);

    }

    @Test
    public void is_sold_out () {



    }

}
