package wynn.pendium.professor.craftsman;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import wynn.pendium.Ref;

import java.util.List;

public class scraper {
    // Scribing Jeweling Alchemism Cooking Weaponsmithing Tailoring Woodworking Armouring

    private static boolean[] outputSlots = {false, false, false, false, false, false, false, false, false, false, false, false};

    static void resetOutputMem() {
        for (boolean b : outputSlots)
            b = false;
    }

    static void releaseOutputMem() {
        if (!(Ref.mc.currentScreen instanceof GuiContainer)) return;
        GuiContainer gui = (GuiContainer) Ref.mc.currentScreen;

        for (int i=5; i < 27; i++) {
            if (i % 9 == 0)
                i += 5;

            if (!outputSlots[i-(5*((i/9)+1))])
                continue;

            ItemStack item = gui.inventorySlots.getSlot(i).getStack();
            if (item.isEmpty() || item.getItem() == Items.AIR || item.getDisplayName().contains("Crafted Item Slot"))
                outputSlots[i-(5*((i/9)+1))] = false;
        }
    }

    static void scrapeCraft(List<ItemStack> items) {
        ItemStack Mat1 = (items.get(0).getDisplayName().contains("Ingredient Slot") ? null : items.get(0));
        ItemStack Mat2 = (items.get(9).getDisplayName().contains("Ingredient Slot") ? null : items.get(9));

        if (Mat1 == null || Mat2 == null) return;

        ItemStack Ingr1 = (items.get(2).getDisplayName().contains("Ingredient Slot") ? null : items.get(2));
        ItemStack Ingr2 = (items.get(3).getDisplayName().contains("Ingredient Slot") ? null : items.get(3));
        ItemStack Ingr3 = (items.get(11).getDisplayName().contains("Ingredient Slot") ? null : items.get(11));
        ItemStack Ingr4 = (items.get(12).getDisplayName().contains("Ingredient Slot") ? null : items.get(12));
        ItemStack Ingr5 = (items.get(20).getDisplayName().contains("Ingredient Slot") ? null : items.get(20));
        ItemStack Ingr6 = (items.get(21).getDisplayName().contains("Ingredient Slot") ? null : items.get(21));

        ItemStack Output = null;

        for (int i=5; i < 27; i++) {
            if (i % 9 == 0)
                i += 5;

            if (outputSlots[i-(5*((i/9)+1))])
                continue;

            Output = items.get(i);
            if (Output.isEmpty() || Output.getItem() == Items.AIR || Output.getDisplayName().contains("Crafted Item Slot"))
                continue;

            outputSlots[i-(5*((i/9)+1))] = true;
            break;
        }

        if (Output == null) return;

        // DO SCRAPING ANALYSIS HERE
    }
}
