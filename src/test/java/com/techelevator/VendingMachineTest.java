package com.techelevator;

import com.techelevator.items.Item;
import com.techelevator.util.ItemSoldOutVendingMachineException;
import com.techelevator.util.NotEnoughMoneyVendingMachineException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class VendingMachineTest {
    VendingMachine vendingMachine;
    BigDecimal startingMoney = BigDecimal.valueOf(5);

    @Before
    public void setup() throws Exception {
        vendingMachine = new VendingMachine();
    }


    @Test
    public void current_money_is_zero_at_initialization() {
            assertEquals("Current money is not initialized.",  vendingMachine.getCurrentMoney(),
                    BigDecimal.valueOf(0));
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
        assertTrue("All slots are not full at initialization.",allSlotsFull);
    }

    @Test
    public void is_money_fed() {

        vendingMachine.feedMoney(startingMoney);

        assertEquals("Not the correct amount.", BigDecimal.valueOf(5), vendingMachine.getCurrentMoney());

    }

    @Test
    public void becomes_sold_out_after_max_items_per_slot_purchased() {
        String slotLocation = "A1";
        BigDecimal startingMoney = vendingMachine.getInventory().get(slotLocation).getPrice().multiply(
                BigDecimal.valueOf(VendingMachine.MAX_ITEMS_PER_SLOT));
        vendingMachine.feedMoney(startingMoney);
        int i = 0;
        try {
            for (i = 1; i < VendingMachine.MAX_ITEMS_PER_SLOT + 1; i++) {
                vendingMachine.purchaseItem(slotLocation);
            }
        }
        catch (ItemSoldOutVendingMachineException e) {
            assertEquals("Sold out after wrong number of purchases.", i, VendingMachine.MAX_ITEMS_PER_SLOT);
        }
        catch (NotEnoughMoneyVendingMachineException e) {
            fail("Should of not ran out of money.");
        }
    }

    @Test
    public void change_is_dispense_correctly() {
        vendingMachine.feedMoney(startingMoney);


        assertEquals("doesn't display correct change after user finishes", 2, vendingMachine.dispenseChange());
    }

    @Test
    public void current_money_is_back_to_zero_after_program_terminates() {

    }

    @Test
    public void display_dispense_message_correctly() {

    }

    @Test
    public void check_if_item_type_is_correct_per_item() {

    }

}