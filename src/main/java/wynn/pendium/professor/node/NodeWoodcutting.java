package wynn.pendium.professor.node;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;
import wynn.pendium.professor.professor;

import java.util.ArrayList;
import java.util.List;

public class NodeWoodcutting {

    private static List<Node> nodes = new ArrayList<>();
    private static int playerLevel = 0;

    public static void register(BlockPos origin, String name, int level, boolean canHarvest) {
        if (canHarvest && playerLevel < level) playerLevel = level;
        if (!canHarvest && playerLevel >= level) playerLevel = level-1;

        BlockPos barrier = getBarrier(origin);
        if (barrier == null || professor.nodeExists(barrier)) return;

        professor.saveNode("WOOD", name, barrier, level);
        BlockPos[] marks = getMarks(barrier, level);
        nodes.add(new Node(barrier, marks[0], marks[1], (byte) 7, name, level, true, NodeType.WOOD));
    }

    public static void load(BlockPos barrier, String name, int level) {
        if (professor.nodeExists(barrier)) return;
        BlockPos[] marks = getMarks(barrier, level);
        nodes.add(new Node(barrier, new BlockPos(0,0,0), new BlockPos(0,0,0), (byte) 7, name, level, true, NodeType.WOOD));
    }

    public static void wipe() {
        nodes.clear();
    }

    static BlockPos getBarrier(BlockPos origin) {
        for (int j = -2; j <= 0; j++)
            for (int i = 1; i <= 6; i++) {
                if (Ref.mc.world.getBlockState(origin.add(i*-1,j,0)).getBlock().equals(Blocks.BARRIER))
                    return origin.add(i*-1,j,0);
                if (Ref.mc.world.getBlockState(origin.add(i,j,0)).getBlock().equals(Blocks.BARRIER))
                    return origin.add(i,j,0);
                if (Ref.mc.world.getBlockState(origin.add(0,j,i*-1)).getBlock().equals(Blocks.BARRIER))
                    return origin.add(0,j,i*-1);
                if (Ref.mc.world.getBlockState(origin.add(0,j,i)).getBlock().equals(Blocks.BARRIER))
                    return origin.add(0,j,i);
            }
        return null;
    }

    static BlockPos[] getMarks(BlockPos barrier, int level) {
        BlockPos checker = barrier;
        BlockPos hud = new BlockPos(0,0,0);
        int limit = 0;
        BlockPos Tmp;

        while (limit < 11 && !(Tmp = stepMarkerUp(checker, level)).equals(checker)) {
            checker = Tmp;
            limit++;
            if (hud.equals(new BlockPos(0,0,0)) && checker.getY() - barrier.getY() == 3) {
                hud = checker;
            }
        }

        if (checker.getY() == barrier.getY()) checker = barrier.add(0,7,0);
        if (hud.equals(new BlockPos(0,0,0))) hud = barrier.add(0,3,0);
        return new BlockPos[] {checker, hud};
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
                BlockPos[] marks = getMarks(nodes.get(i).getBarrier(), nodes.get(i).getLevel());
                nodes.get(i).setCheckMark(marks[0]);
                nodes.get(i).setHudCentre(marks[1]);
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


    private final static Block[] ACCEPTED_TREES = new Block[] {Blocks.LOG, Blocks.LOG2, Blocks.PURPUR_BLOCK, Blocks.STAINED_HARDENED_CLAY, Blocks.CONCRETE, Blocks.PLANKS};
    private final static Block[] DERNIC_TREE = new Block[] {Blocks.PRISMARINE, Blocks.NETHER_BRICK, Blocks.PLANKS, Blocks.DOUBLE_STONE_SLAB, Blocks.DOUBLE_WOODEN_SLAB};

    private static boolean isTree(Block block, int level) {
        for (Block tree : (level == 110 ? DERNIC_TREE : ACCEPTED_TREES))
            if (tree.equals(block)) return true;
        return false;
    }

    private static BlockPos stepMarkerUp(BlockPos Start, int level) {
        if (isTree(Ref.mc.world.getBlockState(Start.add(0,1,0)).getBlock(), level) || Ref.mc.world.getBlockState(Start.add(0,1,0)).getBlock().equals(Blocks.LOG2))
            return Start.add(0,1,0);

        if (isTree(Ref.mc.world.getBlockState(Start.add(-1,1,0)).getBlock(), level))
            return Start.add(-1,1,0);
        if (isTree(Ref.mc.world.getBlockState(Start.add(1,1,0)).getBlock(), level))
            return Start.add(1,1,0);
        if (isTree(Ref.mc.world.getBlockState(Start.add(0,1,-1)).getBlock(), level))
            return Start.add(0,1,-1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(0,1,1)).getBlock(), level))
            return Start.add(0,1,1);

        if (isTree(Ref.mc.world.getBlockState(Start.add(-1,1,-1)).getBlock(), level))
            return Start.add(-1,1,-1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(1,1,-1)).getBlock(), level))
            return Start.add(1,1,-1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(-1,1,1)).getBlock(), level))
            return Start.add(-1,1,1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(1,1,1)).getBlock(), level))
            return Start.add(1,1,1);

        if (isTree(Ref.mc.world.getBlockState(Start.add(-1,0,0)).getBlock(), level))
            return Start.add(-1,0,0);
        if (isTree(Ref.mc.world.getBlockState(Start.add(1,0,0)).getBlock(), level))
            return Start.add(1,0,0);
        if (isTree(Ref.mc.world.getBlockState(Start.add(0,0,-1)).getBlock(), level))
            return Start.add(0,0,-1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(0,0,1)).getBlock(), level))
            return Start.add(0,0,1);

        if (isTree(Ref.mc.world.getBlockState(Start.add(-1,0,-1)).getBlock(), level))
            return Start.add(-1,0,-1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(1,0,-1)).getBlock(), level))
            return Start.add(1,0,-1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(-1,0,1)).getBlock(), level))
            return Start.add(-1,0,1);
        if (isTree(Ref.mc.world.getBlockState(Start.add(1,0,1)).getBlock(), level))
            return Start.add(1,0,1);
        return Start;
    }
}
