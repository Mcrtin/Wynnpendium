package wynn.pendium.professor.node;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;
import wynn.pendium.professor.professor;

import java.util.ArrayList;
import java.util.List;

public class NodeMining {

    private static List<Node> nodes = new ArrayList<>();
    private static int playerLevel = 0;

    public static void register(BlockPos origin, String name, int level, boolean canHarvest) {
        if (canHarvest && playerLevel < level) playerLevel = level;
        if (!canHarvest && playerLevel >= level) playerLevel = level-1;

        BlockPos barrier = getBarrier(origin);
        if (barrier == null || nodeExists(barrier)) return;

        professor.saveNode("MINE", name, barrier, level);
        nodes.add(new Node(barrier, getCheckMark(barrier), getHudMark(barrier), (byte) 3, name, level, false, NodeType.MINE));
    }

    public static void load(BlockPos barrier, String name, int level) {
        if (nodeExists(barrier)) return;
        nodes.add(new Node(barrier, new BlockPos(0,0,0), barrier.add(0,0,0), (byte) 3, name, level, false, NodeType.MINE));
    }

    public static void wipe() {
        nodes.clear();
    }

    static BlockPos getBarrier(BlockPos origin) {
        for (int i = -1; i <= 1; i++)
            if (Ref.mc.world.getBlockState(origin.add(0,i,0)).getBlock().equals(Blocks.BARRIER))
                return origin.add(0, i, 0);
        return null;
    }

    static BlockPos getCheckMark(BlockPos barrier) {
        return barrier.add(0, -1, 0);
    }

    static BlockPos getHudMark(BlockPos barrier) {
        if (Ref.mc.world.getBlockState(barrier.add(0,-1,0)).getBlock() != Blocks.AIR && !Ref.mc.world.getBlockState(barrier.add(0,-1,0)).isSideSolid(Ref.mc.world, barrier.add(0,-1,0), EnumFacing.UP))
            return barrier;
        return barrier.add(0,1,0);
    }

    public static boolean nodeExists(BlockPos check) {
        for (int i = 0; i < nodes.size(); i++)
            if (nodes.get(i).isSame(check))
                return true;
        return false;
    }

    public static Node getNode(BlockPos origin) {
        BlockPos Barrier = getBarrier(origin);
        if (Barrier == null) return null;

        for (Node node : nodes)
            if (node.isSame(Barrier))
                return node;
        return null;
    }

    public static void updateLevel(int level, boolean canUse) {
        if (canUse && playerLevel < level) playerLevel = level;
        if (!canUse && playerLevel >= level) playerLevel = level-1;
    }

    public static void update() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).update(playerLevel);
            if (nodes.get(i).needRecalc()) {
                nodes.get(i).setCheckMark(getCheckMark(nodes.get(i).getBarrier()));
                nodes.get(i).setHudCentre(getHudMark(nodes.get(i).getBarrier()));
            }
        }
    }

    public static void speedBombChange() {
        for (int i = 0; i < nodes.size(); i++)
            nodes.get(i).recalcHarvestable();
    }

    public static void highlight() {
        for (int i = 0; i < nodes.size(); i++)
            nodes.get(i).draw();
    }

    public static void highlightDebug() {
        for (int i = 0; i < nodes.size(); i++)
            nodes.get(i).drawDebug();
    }
}
