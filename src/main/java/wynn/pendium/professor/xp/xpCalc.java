package wynn.pendium.professor.xp;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import wynn.pendium.Ref;
import wynn.pendium.WebManager;
import wynn.pendium.professor.NodeType;
import wynn.pendium.professor.node.*;
import wynn.pendium.professor.professor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wynn.pendium.professor.xp.xpHud.updateXPBar;

public class xpCalc {

    private static Pattern scanner = Pattern.compile("^(?:x(?<Multiplier>\\d+(?:\\.\\d)?) )?\\[\\+(?<Exp>\\d+) . (?<Profession>Farming|Woodcutting|Mining|Fishing) XP\\] \\[(?<Percent>\\d+)%\\]$");
    private static Pattern levelup = Pattern.compile(" +You are now level (?<Level>\\d+) in . (?<Profession>Farming|Woodcutting|Mining|Fishing)");

    private static int farmLvl = 0;
    private static int woodLvl = 0;
    private static int mineLvl = 0;
    private static int fishLvl = 0;

    private static StringBuilder uploadQue = new StringBuilder();
    private static List<String> prepQue = new ArrayList<>();
    private static long nextUpload = Long.MAX_VALUE;

    public static void reset() {
        farmLvl = 0;
        woodLvl = 0;
        mineLvl = 0;
        fishLvl = 0;
        prepQue.clear();
    }

    public static void processXP(String name, BlockPos pos) {
        Matcher scan;

        if (!(scan = scanner.matcher(Ref.noColour(name))).matches()) return;

        professor.updateReplenTime(Ref.getStatAmount("Gather Speed", "%"));

        int percent = Integer.parseInt(scan.group("Percent"));
        int exp = Integer.parseInt(scan.group("Exp"));
        float multiplier = (scan.group("Multiplier") == null ? 1.0f : Float.parseFloat(scan.group("Multiplier")));

        switch (scan.group("Profession")) {
            case "Farming":
                if (farmLvl > 0) uploadGain("FARM", pos, farmLvl, multiplier, exp);
                else prepUploadGain("FARM", pos, multiplier, exp);
                updateXPBar(NodeType.FARM, farmLvl, percent);
                break;
            case "Woodcutting":
                if (woodLvl > 0) uploadGain("WOOD", pos, woodLvl, multiplier, exp);
                else prepUploadGain("WOOD", pos, multiplier, exp);
                updateXPBar(NodeType.WOOD, woodLvl, percent);
                break;
            case "Mining":
                if (mineLvl > 0) uploadGain("MINE", pos, mineLvl, multiplier, exp);
                else prepUploadGain("MINE", pos, multiplier, exp);
                updateXPBar(NodeType.MINE, mineLvl, percent);
                break;
            case "Fishing":
                if (fishLvl > 0) uploadGain("FISH", pos, fishLvl, multiplier, exp);
                else prepUploadGain("FISH", pos, multiplier, exp);
                updateXPBar(NodeType.FISH, fishLvl, percent);
                break;
        }
    }


    private static void uploadGain(String Prof, BlockPos origin, int Level, float multiplier, int Exp) {
        String gain = gainData(Prof, origin, multiplier, Exp);
        if (gain == null) return;

        uploadQue.append(gain.replace("%", String.valueOf(Level)));

        if (nextUpload == Long.MAX_VALUE)
            nextUpload = System.currentTimeMillis() + 300000;
    }

    private static String gainData(String Prof, BlockPos origin, float multiplier, int Exp) {
        Node node = null;
        switch (Prof) {
            case "FARM": node = farming.getNode(origin); break;
            case "WOOD": node = woodcutting.getNode(origin); break;
            case "MINE": node = mining.getNode(origin); break;
            case "FISH": node = fishing.getNode(origin); break;
        }
        if (node == null) return null;
        node.setHarvestable(false);

        StringBuffer Lore = Ref.getLore(Ref.mc.player.inventory.getStackInSlot(8));
        boolean hunted = Pattern.compile("\\+50% Mob XP\\n\\+50% Gathering XP").matcher(Lore).find();

        int Bonus = Ref.getStatAmount("Gathering XP Bonus", "%");

        return Prof + ":" + node.getMaterial() + ":" + node.getLevel() + ":%:" + new DecimalFormat("#0.0").format(multiplier) + ":" + hunted + ":" + Exp + ":" + Bonus + ",";
    }

    public static void checkUpload() {
        if (nextUpload > System.currentTimeMillis()) return;

        WebManager.sendRequest(new String[][] {{"action","save_nodeexp"}, {"node_exp", uploadQue.toString()}});
        uploadQue = new StringBuilder();
        nextUpload = Long.MAX_VALUE;
    }

    private static void prepUploadGain(String Prof, BlockPos origin, float multiplier, int Exp) {
        String gain = gainData(Prof, origin, multiplier, Exp);
        if (gain == null) return;

        prepQue.add(gain);
    }

    private static void processPrepQue(String Prof, int Level) {
        for (int i = 0; i < prepQue.size(); i++)
            if (prepQue.get(i).startsWith(Prof)) {
                uploadQue.append(prepQue.get(i).replace("%", String.valueOf(Level)));
                prepQue.remove(i);
                i--;
            }
    }


    public static void levelUpDetect(String msg) {
        Matcher scan;

        if (!(scan = levelup.matcher(msg)).matches())
            return;

        switch (scan.group("Profession")) {
            case "Farming":
                farmLvl = Integer.parseInt(scan.group("Level"));
                processPrepQue("FARM", farmLvl-1);
                break;
            case "Woodcutting":
                woodLvl = Integer.parseInt(scan.group("Level"));
                processPrepQue("WOOD", woodLvl-1);
                break;
            case "Mining":
                mineLvl = Integer.parseInt(scan.group("Level"));
                processPrepQue("MINE", mineLvl-1);
                break;
            case "Fishing":
                fishLvl = Integer.parseInt(scan.group("Level"));
                processPrepQue("FISH", fishLvl-1);
                break;
        }
    }


    private static Pattern COMPASS = Pattern.compile("\\d+ skill points? remaining");
    private static Pattern PROFESSION_NAME = Pattern.compile("^. (?<Profession>Farming|Woodcutting|Mining|Fishing) Profession \\[Gathering]$");
    private static Pattern PROFESSION_STATS = Pattern.compile("\\nLevel: (?<Level>\\d+)\\nXP: (?<EXP>\\d+)%$");
    private static int tries = 0;
    public static void primeScrapeCompass() {
        GuiContainer gui = (GuiContainer) Ref.mc.currentScreen;

        String name = gui.inventorySlots.getSlot(0).inventory.getName().replaceAll("\\u00A7[a-z0-9]", "");

        if (COMPASS.matcher(name).matches())
            tries = 5;
    }
    public static void scrapeCompass() {
        if (tries == 0) return;
        tries--;

        if (!(Ref.mc.currentScreen instanceof GuiContainer)) return;

        GuiContainer gui = (GuiContainer) Ref.mc.currentScreen;

        for (int i = 0; i < 45; i++) {
            if (!gui.inventorySlots.getSlot(i).getHasStack()) continue;

            Matcher name;
            ItemStack stack = gui.inventorySlots.getSlot(i).getStack();
            if (!(name = PROFESSION_NAME.matcher(Ref.noColour(stack.getDisplayName()))).matches()) continue;

            Matcher lore;
            StringBuffer Lore = Ref.getLore(stack);
            if (!(lore = PROFESSION_STATS.matcher(Lore)).find()) continue;

            switch (name.group("Profession")) {
                case "Farming":
                    farmLvl = Integer.parseInt(lore.group("Level"));
                    processPrepQue("FARM", farmLvl);
                    farming.updateLevel(farmLvl, true);
                    tries = 0;
                    break;
                case "Woodcutting":
                    woodLvl = Integer.parseInt(lore.group("Level"));
                    processPrepQue("WOOD", woodLvl);
                    woodcutting.updateLevel(woodLvl, true);
                    tries = 0;
                    break;
                case "Mining":
                    mineLvl = Integer.parseInt(lore.group("Level"));
                    processPrepQue("MINE", mineLvl);
                    mining.updateLevel(mineLvl, true);
                    tries = 0;
                    break;
                case "Fishing":
                    fishLvl = Integer.parseInt(lore.group("Level"));
                    processPrepQue("FISH", fishLvl);
                    fishing.updateLevel(fishLvl, true);
                    tries = 0;
                    break;
            }
        }
    }
}