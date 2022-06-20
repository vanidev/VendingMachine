package com.techelevator;

import com.techelevator.items.Item;
import com.techelevator.util.ItemSoldOutVendingMachineException;
import com.techelevator.util.NotEnoughMoneyVendingMachineException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

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
        String slotLocation = "A1";
        BigDecimal startingMoney = vendingMachine.getInventory().get(slotLocation).getPrice()
                .add(new BigDecimal("0.25").multiply(BigDecimal.valueOf(3)))
                .add(new BigDecimal("0.10").multiply(BigDecimal.valueOf(1)))
                .add(new BigDecimal("0.05").multiply(BigDecimal.valueOf(1)));
        vendingMachine.feedMoney(startingMoney);
        try {
            vendingMachine.purchaseItem(slotLocation);
        }
        catch (ItemSoldOutVendingMachineException e) {
            assertEquals("Should not be sold out", VendingMachine.MAX_ITEMS_PER_SLOT, 0);
        }
        catch (NotEnoughMoneyVendingMachineException e) {
            fail("Should of not ran out of money.");
        }
        Map<Integer, Integer> coinCounts = vendingMachine.dispenseChange();
        String expectedCoinCounts = "";
        if(!coinCounts.isEmpty()) {
            if(coinCounts.containsKey(25)) {
                expectedCoinCounts += String.format("25=%d",  coinCounts.get(25));
            }

            if(coinCounts.containsKey(10)) {
                if(!expectedCoinCounts.isEmpty()) {
                    expectedCoinCounts += ",";
                }
                expectedCoinCounts += String.format("10=%d",  coinCounts.get(10));
            }
            if(coinCounts.containsKey(5)) {
                if(!expectedCoinCounts.isEmpty()) {
                    expectedCoinCounts += ",";
                }
                expectedCoinCounts += String.format("5=%d",  coinCounts.get(5));
            }
        }
        expectedCoinCounts = "{" + expectedCoinCounts + "}";
        assertEquals("doesn't display correct change after user finishes", "{25=3,10=1,5=1}", expectedCoinCounts );
    }

    @Test
    public void current_money_is_back_to_zero_after_program_terminates()   {
        String slotLocation = "A1";
        vendingMachine.feedMoney(startingMoney);

        try {
            vendingMachine.purchaseItem(slotLocation);
            vendingMachine.dispenseChange();
        }
        catch (ItemSoldOutVendingMachineException e) {
            assertEquals("Should not be sold out", VendingMachine.MAX_ITEMS_PER_SLOT, 0);
        }
        catch (NotEnoughMoneyVendingMachineException e) {
            fail("Should of not ran out of money.");
        }

        assertEquals("Current money not reset!",  BigDecimal.valueOf(0), vendingMachine.getCurrentMoney());
    }

    @Test
    public void current_money_reset_after_multiple_purchases_when_dispense_change() {
        vendingMachine.feedMoney(startingMoney.multiply(BigDecimal.valueOf(5)));

        try {
            vendingMachine.purchaseItem("A1");
            vendingMachine.purchaseItem("B3");
            vendingMachine.purchaseItem("C1");
            vendingMachine.purchaseItem("C2");
            vendingMachine.purchaseItem("C3");

            vendingMachine.dispenseChange();
        }
        catch (ItemSoldOutVendingMachineException e) {
            assertEquals("Should not be sold out", VendingMachine.MAX_ITEMS_PER_SLOT - 5, 0);
        }
        catch (NotEnoughMoneyVendingMachineException e) {
            fail("Should of not ran out of money.");
        }

        assertEquals("Current money not reset!",  BigDecimal.valueOf(0), vendingMachine.getCurrentMoney());
    }
}