package wynn.pendium.professor;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import wynn.pendium.Ref;
import wynn.pendium.WebManager;
import wynn.pendium.hud.hud;
import wynn.pendium.professor.node.farming;
import wynn.pendium.professor.node.fishing;
import wynn.pendium.professor.node.mining;
import wynn.pendium.professor.node.woodcutting;

import java.util.List;
import java.util.regex.Matcher;

public class professor {

    public static boolean Enabled = false;

    private static float replenTime = 60000f; // 60000f - Real Time      1200f - World Time
    private static boolean speedBomb = false;

    public static void Init() {
        MinecraftForge.EVENT_BUS.register(new events());
    }

    public static void Enable() {
        farming.wipe();
        woodcutting.wipe();
        mining.wipe();
        fishing.wipe();

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
                case "FARM": farming.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                case "WOOD": woodcutting.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                case "MINE": mining.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                case "FISH": fishing.load(pos, node.group("Name"), Integer.parseInt(node.group("Level"))); break;
                default:
                    hud.consoleOut("Error Loading " + node.group("Type") + " node: " + node.group("X") + ", " + node.group("Y") + ", " + node.group("Z") + " [Lv. " + node.group("Level") + "]" );
            }
        }
    }

    public static void saveNode(String profession, String material, BlockPos pos, int Level) {
        WebManager.sendRequest(new String[][] {{"action", "save_node"}, {"profession", profession}, {"material", material}, {"pos_x", String.valueOf(pos.getX())}, {"pos_y", String.valueOf(pos.getY())}, {"pos_z", String.valueOf(pos.getZ())}, {"level", String.valueOf(Level)}});
    }

    public static boolean nodeExists(BlockPos barrier) {
        return farming.nodeExists(barrier) || woodcutting.nodeExists(barrier) || mining.nodeExists(barrier) || fishing.nodeExists(barrier);
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

        farming.speedBombChange();
        woodcutting.speedBombChange();
        mining.speedBombChange();
        fishing.speedBombChange();
    }
}
