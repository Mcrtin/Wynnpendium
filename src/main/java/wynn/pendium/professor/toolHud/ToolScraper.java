package wynn.pendium.professor.toolHud;

import net.minecraft.item.ItemStack;
import wynn.pendium.Ref;
import wynn.pendium.hud.components.features.ComponentToolDurability;
import wynn.pendium.hud.components.features.DisplayStack;
import wynn.pendium.professor.NodeType;
import wynn.pendium.professor.node.NodeFarming;
import wynn.pendium.professor.node.NodeFishing;
import wynn.pendium.professor.node.NodeMining;
import wynn.pendium.professor.node.NodeWoodcutting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wynn.pendium.hud.components.features.ComponentToolDurability.activeTool;
import static wynn.pendium.hud.components.features.ComponentToolDurability.hudList;

public class ToolScraper {

    private static final Pattern ItemMatcher = Pattern.compile("^\\u00A7f(?<Name>.+)\\u00A76 \\[\\u00A7e(?<Tier>\\u272B+)(?:\\u00A78)?\\u272B*\\u00A76\\]$");
    private static final Pattern ItemScanner = Pattern.compile("^Crafting Material\\n\\n(?<CanUse>.) . (?<Profession>Farming|Woodcutting|Mining|Fishing) Lv\\. Min: (?<Level>\\d+)");
    private static final Pattern ToolMatcher = Pattern.compile("^\\u00A7[0-9a-z]. Gathering (?<Tool>Scythe|Axe|Rod|Pickaxe) T\\d+$");
    private static final Pattern ToolScanner = Pattern.compile("(?<CanUse>.) . (?<Profession>Farming|Woodcutting|Mining|Fishing) Lv\\. Min: (?<Level>\\d+)\\n(?:.|\\n)*?\\nGathering Tool \\[(?<CurDur>\\d+)/(?<MaxDur>\\d+) Durability\\]");

    private static final List<DisplayStack> MattList = new ArrayList<>();

    public static boolean[] Tools = {false, false, false, false};

    public static void scrapeToolsAndMatts() {
        MattList.clear();
        Tools[0] = false; Tools[1] = false; Tools[2] = false; Tools[3] = false;
        activeTool = NodeType.NONE;

        Matcher match;
        for (ItemStack item : Ref.mc.player.inventory.mainInventory) {
            if ((match = ToolMatcher.matcher(item.getDisplayName())).matches())
                processTool(item, match);
            else if ((match = ItemMatcher.matcher(item.getDisplayName())).matches())
                processMaterial(item, match);
        }

        hudList.clear();
        for (DisplayStack matt : MattList)
            if (matt.getType().equals(activeTool))
                hudList.add(matt);
    }

    private static void processTool(ItemStack tool, Matcher match) {
        NodeType toolType = NodeType.NONE;
        switch (match.group("Tool")) {
            case "Scythe": toolType = NodeType.FARM; Tools[0] = true; break;
            case "Axe": toolType = NodeType.WOOD; Tools[1] = true; break;
            case "Pickaxe": toolType = NodeType.MINE; Tools[2] = true; break;
            case "Rod": toolType = NodeType.FISH; Tools[3] = true; break;
            default: break;
        }

        StringBuffer Lore = Ref.getLore(tool);

        if (!(match = ToolScanner.matcher(Lore)).find()) return;

        processLevel(toolType, Integer.parseInt(match.group("Level")), match.group("CanUse").equals("\u2714"));

        if (!ItemStack.areItemStacksEqual(Ref.mc.player.getHeldItemMainhand(), tool)) return;

        activeTool = toolType;
        ComponentToolDurability.update(toolType, match.group("CurDur"), match.group("MaxDur"));
    }


    private static void processMaterial(ItemStack matt, Matcher match) {
        String name = match.group("Name");
        int tier = 0;
        switch (match.group("Tier")) {
            case "\u272B": tier = 1; break;
            case "\u272B\u272B": tier = 2; break;
            case "\u272B\u272B\u272B": tier = 3; break;
        }

        StringBuffer Lore = Ref.getLore(matt);

        if (!(match = ItemScanner.matcher(Lore)).find()) return;

        NodeType mattType = NodeType.NONE;
        switch (match.group("Profession")) {
            case "Farming": mattType = NodeType.FARM; break;
            case "Woodcutting": mattType = NodeType.WOOD; break;
            case "Mining": mattType = NodeType.MINE; break;
            case "Fishing": mattType = NodeType.FISH; break;
            default: break;
        }

        processLevel(mattType, Integer.parseInt(match.group("Level")), match.group("CanUse").equals("\u2714"));

        countMaterial(mattType, name, matt, tier);
    }


    private static void processLevel(NodeType profession, int level, boolean canUse) {
        switch (profession) {
            case FARM: NodeFarming.updateLevel( level, canUse); break;
            case WOOD: NodeWoodcutting.updateLevel(level, canUse); break;
            case MINE: NodeMining.updateLevel(level, canUse); break;
            case FISH: NodeFishing.updateLevel(level, canUse); break;
        }
    }


    private static void countMaterial(NodeType type, String name, ItemStack matt, int tier) {
        for (DisplayStack hudItem : MattList)
            if (hudItem.isSame(matt)) {
                hudItem.addStack(matt);
                return;
            }

        Matcher match;
        boolean placeNext = false;

        for (int i = 0; i < MattList.size(); i++) {
            if ((match = ItemMatcher.matcher(MattList.get(i).getStack().getDisplayName())).matches() && match.group("Name").equals(name)) {
                switch (tier) {
                    case 1:
                        MattList.add(i, new DisplayStack(type, matt, matt.getCount(), tier));
                        return;
                    case 2:
                        MattList.add((match.group("Tier").equals("\u272B") ? i+1 : i), new DisplayStack(type, matt, matt.getCount(), tier));
                        return;
                    case 3:
                        if (match.group("Tier").equals("\u272B\u272B")) {
                            MattList.add(i+1, new DisplayStack(type, matt, matt.getCount(), tier));
                            return;
                        }
                }
                placeNext = true;
            } else if (placeNext) {
                MattList.add(i, new DisplayStack(type, matt, matt.getCount(), tier));
                return;
            }
        }
        MattList.add(new DisplayStack(type, matt, matt.getCount(), tier));
    }
}
