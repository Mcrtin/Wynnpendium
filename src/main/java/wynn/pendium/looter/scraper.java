package wynn.pendium.looter;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import wynn.pendium.Ref;
import wynn.pendium.WebManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class scraper {

    private static int tries = 0;
    private static BlockPos chest;
    private static byte Tier;
    private static int lootBonus;
    private static int lootQuality;

    private static final Pattern CHEST_NAME = Pattern.compile("Loot Chest (?<Tier>[IVX]+)");
    private static final Pattern Material = Pattern.compile("\\u00A77(?<Name>[a-zA-Z ]+)\\u00A7[a-z0-9] \\[\\u00A7(?<Tier>[8edb])\\u272B.*");
    private static final Pattern Powder = Pattern.compile("\\u00A7[0-9a-f]. (?<Type>[A-Za-z]+) Powder (?<Tier>[IVX]+)");
    private static final Pattern Potion = Pattern.compile("^Potion of (?<Type>.+?)(?: \\[\\d/\\d])?$");
    private static final Pattern LevelGrabber = Pattern.compile("Combat Lv\\. Min: (?<Level>\\d+)");
    private static final Pattern NormalItemType = Pattern.compile("(?<Type>[a-zA-Z]+)$");
    private static final Pattern Item = Pattern.compile("(?:.|\\n)*Info:\\n" +
            "- Lv\\. Range: (?<LevelLow>\\d+)-(?<LevelUp>\\d+)\\n" +
            "- Tier: (?<Quality>[a-zA-Z]+)\\n" +
            "- Type: (?<Type>[a-zA-Z]+)(?:.|\\n)*|" +
            "(?:.|\\n)*Combat Lv\\. Min: (?<LevelNorm>\\d+)\\n" +
            "\\n" +
            "(?:\\[0/\\d] Powder Slots\\n)?" +
            "Normal Item(?:.|\\n)*");


    static void prime(BlockPos location, int bonus, int quality) {
        GuiContainer gui = (GuiContainer) Ref.mc.currentScreen;

        String name = gui.inventorySlots.getSlot(0).inventory.getName().replaceAll("\\u00A7[a-z0-9]", "");
        Matcher nameCheck;

        if (!(nameCheck = CHEST_NAME.matcher(name)).matches()) return;
        tries = 5;
        chest = location;
        switch (nameCheck.group("Tier")) {
            case "I": Tier = 1; break;
            case "II": Tier = 2; break;
            case "III": Tier = 3; break;
            case "IV": Tier = 4; break;
            case "V": Tier = 5; break;
        }
        lootBonus = bonus;
        lootQuality = quality;
    }

    static void scrape() {
        if (tries == 0) return;
        tries--;

        if (!(Ref.mc.currentScreen instanceof GuiContainer)) return;

        GuiContainer gui = (GuiContainer) Ref.mc.currentScreen;

        String data = "";
        int emeralds = 0;

        for (int i = 0; i < 27; i++) {
            if (!gui.inventorySlots.getSlot(i).getHasStack()) continue;

            ItemStack stack = gui.inventorySlots.getSlot(i).getStack();
            String name = stack.getDisplayName();

            if (name.equals("\u00A7aEmerald")) {
                emeralds += stack.getCount();
                continue;
            }

            Matcher reg;

            if ((reg = Material.matcher(name)).matches()) {
                switch (reg.group("Tier")) {
                    case "8": data += "MT:" + reg.group("Name") + ":0,"; continue;
                    case "e": data += "MT:" + reg.group("Name") + ":1,"; continue;
                    case "d": data += "MT:" + reg.group("Name") + ":2,"; continue;
                    case "b": data += "MT:" + reg.group("Name") + ":3,"; continue;
                    default: continue;
                }
            }

            if ((reg = Powder.matcher(name)).matches()) {
                byte tier = 0;
                switch (reg.group("Tier")) {
                    case "I": tier = 1; break;
                    case "II": tier = 2; break;
                    case "III": tier = 3; break;
                    case "IV": tier = 4; break;
                    case "V": tier = 5; break;
                    case "VI": tier = 6; break;
                    default: continue;
                }
                data += "PR:" + reg.group("Type") + ":" + tier + ",";
                continue;
            }

            StringBuffer Lore = Ref.getLore(stack);

            name = name.replaceAll("\\u00A7[a-z0-9]", "");
            if ((reg = Potion.matcher(name)).matches()) {
                Matcher reg2;
                if (!(reg2 = LevelGrabber.matcher(Lore)).find()) continue;

                String type = reg.group("Type");
                if (type.charAt(1) == ' ') type = type.substring(2);

                data += "PN:" + type + ":" + reg2.group("Level") + ",";
                continue;
            }

            if ((reg = Item.matcher(Lore)).matches()) {
                if (reg.group("LevelNorm") == null) {
                    data += "IM:" + reg.group("Quality") + ":" + reg.group("Type") + ":" + reg.group("LevelLow") + ",";
                    continue;
                }

                Matcher reg2 = NormalItemType.matcher(name);
                reg2.matches();
                if (!reg2.find()) {
                    System.out.println("Crash imminent, Type Match not found: " + name + " [" + name.toCharArray()[name.toCharArray().length - 1] + "]");//continue;
                    for (int j = 0; j < 27; j++) {
                        Slot debug = gui.inventorySlots.getSlot(j);
                        if (debug.getHasStack()) System.out.println("[" + j + "] " + debug.getStack().getDisplayName() + " - " + debug.getStack().serializeNBT().toString());
                    }
                }
                data += "IM:Normal:" + reg2.group("Type") + ":" + reg.group("LevelNorm") + ",";
                continue;
            }
        }

        if (emeralds > 0 || !data.equals("") || tries == 0) {
            if (data.equals("")) data = "N";
            WebManager.sendRequest(new String[][] { {"action", "save_chest"},
                    {"pos_x", String.valueOf(chest.getX())},
                    {"pos_y", String.valueOf(chest.getY())},
                    {"pos_z", String.valueOf(chest.getZ())},
                    {"tier", String.valueOf(Tier)},
                    {"loot_bonus", String.valueOf(lootBonus)},
                    {"loot_quality", String.valueOf(lootQuality)},
                    {"emeralds", String.valueOf(emeralds)},
                    {"data", data}
            });
            tries = 0;
        }
    }
}
