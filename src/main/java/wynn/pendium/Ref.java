package wynn.pendium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import wynn.pendium.gluttony.gluttony;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ref {

    private static final Pattern ColourRegex = Pattern.compile("\\u00A7[a-z0-9]");

    public static final Minecraft mc = Minecraft.getMinecraft();


    static boolean isDev = false;
    public static boolean Dev(){
        return isDev;
    }

    public static boolean inServer() {
        if (mc.getCurrentServerData() == null) return false;
        return mc.getCurrentServerData().serverIP.toLowerCase().contains("wynncraft");
    }

    private static boolean isInLobby = false;
    public static boolean inLobby() { return isInLobby; }
    static boolean setLobby() {
        isInLobby = false;
        if (!inServer())
            return false;

        try {
            if (mc.player.posX > 0 && mc.player.posZ > 0 && mc.player.posX < 512 && mc.player.posZ < 512)
                isInLobby = true;
        } catch (Exception ignored) {}
        return isInLobby;
    }

    private static boolean isInGame = false;
    public static boolean inGame() {return isInGame;}
    static boolean setInGame() {
        isInGame = false;
        if (!inServer())
            return false;

        try {
            ItemStack book = mc.player.inventory.getStackInSlot(7);
            if (book.getDisplayName().contains("Quest Book"))
                isInGame = true;
        } catch (Exception ignored) {}
        return isInGame;
    }

    public static String noColour(String text) {
        return ColourRegex.matcher(text).replaceAll("");
    }

    public static StringBuffer getLore(ItemStack item) {
        if (item == null || item.isEmpty()) return new StringBuffer();

        if (!item.getTagCompound().hasKey("display") || !item.getSubCompound("display").hasKey("Lore"))
            return new StringBuffer();

        NBTTagList rawLore = item.getSubCompound("display").getTagList("Lore", 8);
        StringBuffer Lore = new StringBuffer();
        for (NBTBase rawLine : rawLore) {
            String line = rawLine.toString().replaceAll("\"", "");
            if (line.startsWith("\u00A78"))
                break;
            Lore.append(line.replaceAll("\\u00A7[a-z0-9]|\"", "") + "\n");
        }
        //rawLore.forEach(line -> Lore.append(line.toString().replaceAll("\\u00A7[a-z0-9]|\"", "") + "\n"));
        return Lore;
    }

    public static StringBuffer getLoreRaw(ItemStack item) {
        if (item == null || item.isEmpty()) return new StringBuffer();

        if (!item.getTagCompound().hasKey("display") || !item.getSubCompound("display").hasKey("Lore"))
            return new StringBuffer();

        NBTTagList rawLore = item.getSubCompound("display").getTagList("Lore", 8);
        StringBuffer Lore = new StringBuffer();
        for (NBTBase rawLine : rawLore) {
            String line = rawLine.toString().replaceAll("\"", "");
            Lore.append(line.replaceAll("\"", "") + "\n");
        }
        //rawLore.forEach(line -> Lore.append(line.toString().replaceAll("\\u00A7[a-z0-9]|\"", "") + "\n"));
        return Lore;
    }

    public static int getStatAmount(String statName, String suffix) {
        int Bonus = gluttony.getStat(statName, suffix); // Get amount from active potions

        Pattern stat = Pattern.compile("\\n\\+?(?<Value>-?\\d+)" + suffix + "\\*{0,3}(?:/-?\\d+" + suffix + ")? " + statName);
        Matcher scan;
        if ((scan = stat.matcher(Ref.getLore(Ref.mc.player.getHeldItemMainhand()))).find()) // Get amount from hand
            Bonus += Integer.parseInt(scan.group("Value"));
        for (int i=0; i < Ref.mc.player.inventory.armorInventory.size(); i++)   // Get amount from armour
            if ((scan = stat.matcher(Ref.getLore(Ref.mc.player.inventory.armorInventory.get(i)))).find())
                Bonus += Integer.parseInt(scan.group("Value"));
        for (int i=9; i < 13; i++)
            if ((scan = stat.matcher(Ref.getLore(Ref.mc.player.inventory.mainInventory.get(i)))).find())
                Bonus += Integer.parseInt(scan.group("Value"));
        return Bonus;
    }

    public static void drawLine(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

        bufferbuilder.pos(minX, minY, minZ).color(0,0,0, 0.0F).endVertex();
        bufferbuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        tessellator.draw();
    }


    public static int getStatHierarchy(String stat, String suffix) {
        switch(stat) {
            default: return 1000;

            case "Heal": return -10;

            case "Strength": return -5;
            case "Dexterity": return -4;
            case "Intelligence": return -3;
            case "Defence": return -2;
            case "Agility": return -1;

            case "Attack Speed": return 0;

            case "Main Attack Damage": return 1;
            case "Main Attack Neutral Damage": return 2;
            case "Spell Damage": return 3;
            case "Neutral Spell Damage": return 4;

            case "Health": return 5;
            case "Health Regen": return (suffix.equals("") ? 6 : 7);
            case "Life Steal": return 8;
            case "Mana Regen": return 9;
            case "Mana Steal": return 10;

            case "Earth Damage": return 11;
            case "Thunder Damage": return 12;
            case "Water Damage": return 13;
            case "Fire Damage": return 14;
            case "Air Damage": return 15;

            case "Earth Defence": return 12;
            case "Thunder Defence": return 13;
            case "Water Defence": return 14;
            case "Fire Defence": return 15;
            case "Air Defence": return 16;


            case "Exploding": return 17;
            case "Poison": return 18;
            case "Thorns": return 19;
            case "Reflection": return 20;

            case "Walk Speed": return 21;
            case "Sprint": return 22;
            case "Sprint Regen": return 23;
            case "Jump Height": return 24;
            case "Soul Point Regen": return 25;

            case "XP Bonus": return 26;
            case "Crafting XP Bonus": return 27;
            case "Gather XP Bonus": return 28;
            case "Gather Speed": return 29;

            case "Loot Bonus": return 30;
            case "Loot Quality": return 31;
            case "Stealing": return 32;

        }
    }
}
