package wynn.pendium.professor;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import wynn.pendium.Ref;
import wynn.pendium.WebManager;
import wynn.pendium.hud.Hud;
import wynn.pendium.listeners.EventListeners;
import wynn.pendium.professor.node.NodeFarming;
import wynn.pendium.professor.node.NodeFishing;
import wynn.pendium.professor.node.NodeMining;
import wynn.pendium.professor.node.NodeWoodcutting;

import java.util.List;
import java.util.regex.Matcher;

public class professor {

    public static boolean Enabled = false;

    private static float replenTime = 60000f; // 60000f - Real Time      1200f - World Time
    private static boolean speedBomb = false;

    public static void Init() {
        MinecraftForge.EVENT_BUS.register(new EventListeners());
    }

    public static void Enable() {
        NodeFarming.wipe();
        NodeWoodcutting.wipe();
        NodeMining.wipe();
        NodeFishing.wipe();

        Enabled = true;
    }

    public static void Disable() {
        Enabled = false;

        replenTime = 60000f; // 60000f - Real Time      1200f - World Time
        speedBomb = false;
    }

    public static void loadNodes(List<Matcher> nodes) {
        for (Matcher node : nodes) {
            BlockPos pos = new BlockPos(Integer.parseInt(node.group("X")), Integer.parseInt(node.group("Y")), Integer.parseInt(node.group("Z")));
            switch (node.group("Type")) {
                case "FARM": NodeFarming.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                case "WOOD": NodeWoodcutting.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                case "MINE": NodeMining.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                case "FISH": NodeFishing.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                default:
                    Hud.consoleOut("Error Loading " + node.group("Type") + " node: " + node.group("X") + ", " + node.group("Y") + ", " + node.group("Z") + " [Lv. " + node.group("Level") + "]" );
            }
        }
    }

    public static void saveNode(String profession, String material, BlockPos pos, int Level) {
        WebManager.sendRequest(new String[][] {{"action", "save_node"}, {"profession", profession}, {"material", material}, {"pos_x", String.valueOf(pos.getX())}, {"pos_y", String.valueOf(pos.getY())}, {"pos_z", String.valueOf(pos.getZ())}, {"level", String.valueOf(Level)}});
    }

    public static boolean nodeExists(BlockPos barrier) {
        return NodeFarming.nodeExists(barrier) || NodeWoodcutting.nodeExists(barrier) || NodeMining.nodeExists(barrier) || NodeFishing.nodeExists(barrier);
    }


    public static float getReplenTime() {
        return replenTime;
    }

    public static void updateReplenTime(int speedBonus) {
        replenTime = ((speedBonus / 100f) + 1.0f) * 60000f;

        if (speedBomb)
            replenTime /= 2f;
    }

    public static void setSpeedBomb(boolean set) {
        speedBomb = set;
        updateReplenTime(Ref.getStatAmount("Gather Speed", "%"));

        NodeFarming.speedBombChange();
        NodeWoodcutting.speedBombChange();
        NodeMining.speedBombChange();
        NodeFishing.speedBombChange();
    }
}
