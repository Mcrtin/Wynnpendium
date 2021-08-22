package wynn.pendium.professor.node;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;
import wynn.pendium.professor.professor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {

    private BlockPos barrier;
    private BlockPos checkMark;
    private BlockPos hudCentre;
    private byte[] hudMeta = new byte[] {0,0,0};

    private String material;
    private int level;
    private boolean canHarvest;
    private boolean checkBlock;
    private Entity entityCheck;

    private boolean Harvestable;
    private long lastHarvested;
    private boolean inRange = false;

    private float[] typeColour;
    private float[] colours;
    private float percent = 1f;

    Node(BlockPos barrier, BlockPos checkMark, BlockPos hudMark, byte maxHudRange, String material, int level, boolean blockCheck, NodeType type) {
        this.barrier = barrier;
        this.checkMark = checkMark;
        this.hudCentre = hudMark;
        this.hudMeta[2] = maxHudRange;

        this.material = material;
        this.level = level;
        this.canHarvest = true;
        this.checkBlock = blockCheck;
        this.Harvestable = false;
        this.colours = getTierColour();

        switch (type) {
            default: this.typeColour = new float[] {0f, 0f, 0f}; break;
            case FARM: this.typeColour = new float[] {1f, 0.66666f, 0f}; break;
            case WOOD: this.typeColour = new float[] {0f, 0.66666f, 0f}; break;
            case MINE: this.typeColour = new float[] {0.66666f, 0f, 0f}; break;
            case FISH: this.typeColour = new float[] {0.33333f, 0.33333f, 1f}; break;
        }
    }

    boolean isSame(BlockPos check) {
        return this.barrier.equals(check) || this.checkMark.equals(check);
    }

    boolean needRecalc() { return this.inRange && this.checkMark.equals(new BlockPos(0,0,0)) && Ref.mc.world.isBlockLoaded(this.barrier);}

    void setCheckMark(BlockPos checkMark) { this.checkMark = checkMark; }

    void setHudCentre(BlockPos hudCentre) { this.hudCentre = hudCentre; this.hudMeta[0] = 0; this.hudMeta[1] = 0; }

    BlockPos getBarrier() { return this.barrier; }

    public String getMaterial() { return this.material; }

    public int getLevel() { return this.level; }


    void update(int playerLevel) {
        this.inRange = Math.sqrt(Ref.mc.player.getDistanceSqToCenter(this.barrier)) < 48;
        if (!this.inRange) return;

        if (this.canHarvest && playerLevel < this.level) this.canHarvest = false;
        if (!this.canHarvest && playerLevel >= this.level) this.canHarvest = true;

        updateHudMeta();

        if (this.Harvestable) {
            /*if (checkHarvested()) {
                this.Harvestable = false;
                this.percent = 0f;

                this.lastHarvested = System.currentTimeMillis();      // Real Time
                //this.lastHarvested = Ref.mc.world.getTotalWorldTime();  // World Time
            }*/
            return;
        }

        if (this.lastHarvested > System.currentTimeMillis()) this.lastHarvested = 0;                    // Real Time
        this.percent = Math.min((System.currentTimeMillis() - this.lastHarvested) / professor.getReplenTime(), 1);                    // Real Time
        //if (this.lastHarvested > Ref.mc.world.getTotalWorldTime()) this.lastHarvested = 0;              // World Time
        //this.percent = Math.min((Ref.mc.world.getTotalWorldTime() - this.lastHarvested) / professor.getReplenTime(), 1);    // World Time

        //if (this.percent == 1 || checkHarvestable()) { // With block detection
        if (this.percent == 1) { // Force reset only
            this.Harvestable = true;
        }
    }

    public void setHarvestable(boolean harvestable) {
        this.Harvestable = harvestable;

        if (!this.Harvestable) {
            this.percent = 0f;

            this.lastHarvested = System.currentTimeMillis();      // Real Time
            //this.lastHarvested = Ref.mc.world.getTotalWorldTime();  // World Time
        }
    }

    void recalcHarvestable() {
        if (this.lastHarvested > 0) return;

        if (this.Harvestable ^ System.currentTimeMillis() - this.lastHarvested > professor.getReplenTime())
            this.Harvestable = !this.Harvestable;
    }

    private boolean checkHarvestable() {
        if (this.checkBlock)
            return (!Ref.mc.world.getBlockState(this.checkMark).getBlock().equals(Blocks.BARRIER) && !Ref.mc.world.getBlockState(this.checkMark).getBlock().equals(Blocks.STAINED_GLASS) && !Ref.mc.world.getBlockState(this.checkMark).getBlock().equals(Blocks.AIR));

        int count = 0;
        for (Entity e : Ref.mc.world.loadedEntityList)
            if (e instanceof EntityArmorStand && (e.posX >= this.checkMark.getX()-0.25 && e.posX <= this.checkMark.getX()+1.25 && e.posY >= this.checkMark.getY()-0.75 && e.posY <= this.checkMark.getY()+0.75 && e.posZ >= this.checkMark.getZ()-0.25 && e.posZ <= this.checkMark.getZ()+1.25)) {
                count++;
                if (count > 1) {
                    this.entityCheck = e;
                    return true;
                }
            }
        return false;
    }

    private boolean checkHarvested() {
        if (this.checkBlock)
            return (Ref.mc.world.getBlockState(this.checkMark).getBlock().equals(Blocks.BARRIER) || Ref.mc.world.getBlockState(this.checkMark).getBlock().equals(Blocks.STAINED_GLASS) || Ref.mc.world.getBlockState(this.checkMark).getBlock().equals(Blocks.AIR));

        return this.entityCheck.isDead;
    }

    private float[] getTierColour() {
        switch (this.level) {
            default: return new float[] {1f, 1f, 1f};
            case 10: return new float[] {1f, 1f, 0.33333f};
            case 20: return new float[] {1f, 0.66666f, 0f};
            case 30: return new float[] {0.33333f, 1f, 0.33333f};
            case 40: return new float[] {0f, 0.66666f, 0f};
            case 50: return new float[] {0.33333f, 1f, 1f};
            case 60: return new float[] {0f, 0.66666f, 0.66666f};
            case 70: return new float[] {0.33333f, 0.33333f, 1f};
            case 80: return new float[] {0f, 0f, 0.66666f};
            case 90: return new float[] {1f, 0.33333f, 1f};
            case 100: return new float[] {0.66666f, 0f, 0.66666f};
            case 110: return new float[] {1f, 0.33333f, 0.33333f};
            case 120: return new float[] {0.66666f, 0f, 0f};
            case 130: return new float[] {0f, 0f, 0f};
        }
    }

    private void updateHudMeta() {
        if (this.hudMeta[0] == -1 || this.hudMeta[2] <= 0) return;

        float dif_x = (float) (this.hudCentre.getX() + 0.5 - Ref.mc.player.posX);
        float dif_z = (float) (this.hudCentre.getZ() + 0.5 - Ref.mc.player.posZ);
        byte dir;

        if (Math.abs(dif_x) > Math.abs(dif_z))
            if (Math.abs(dif_x) - Math.abs(dif_z) < Math.abs(dif_x)/2)
                if ((dif_x < 0 && dif_z < 0) || (dif_x > 0 && dif_z > 0))
                    dir = 3;
                else
                    dir = 4;
            else
                dir = 1;
        else
            if (Math.abs(dif_z) - Math.abs(dif_x) < Math.abs(dif_z)/2)
                if ((dif_x < 0 && dif_z < 0) || (dif_x > 0 && dif_z > 0))
                    dir = 3;
                else
                    dir = 4;
            else
                dir = 2;
        if (dir == this.hudMeta[0]) return;

        Block Check = Ref.mc.world.getBlockState(this.hudCentre).getBlock();
        if (Check == Blocks.AIR) {
            this.hudMeta[0] = -1;
            return;
        }

        this.hudMeta[0] = dir;
        this.hudMeta[1] = 0;

        switch (dir) {
            case 1:
                while (Math.abs(dif_x < 0 ? this.hudMeta[1]++ : this.hudMeta[1]--) < this.hudMeta[2]) {
                    if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(this.hudMeta[1], 0, 0)).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                        if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(this.hudMeta[1] + (dif_x < 0 ? 1 : -1), 0, 0)).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                            return;
                }
                break;
            case 2:
                while (Math.abs(dif_z < 0 ? this.hudMeta[1]++ : this.hudMeta[1]--) < this.hudMeta[2]) {
                    if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(0, 0, this.hudMeta[1])).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                        if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(0, 0, this.hudMeta[1] + (dif_z < 0 ? 1 : -1))).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                            return;
                }
                break;
            case 3:
                while (Math.abs(dif_x < 0 ? this.hudMeta[1]++ : this.hudMeta[1]--) < this.hudMeta[2]) {
                    if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(this.hudMeta[1], 0, this.hudMeta[1])).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                        if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(this.hudMeta[1] + (dif_x < 0 ? 1 : -1), 0, this.hudMeta[1] + (dif_x < 0 ? 1 : -1))).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                            return;
                }
                break;
            case 4:
                while (Math.abs(dif_x < 0 ? this.hudMeta[1]++ : this.hudMeta[1]--) < this.hudMeta[2]) {
                    if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(this.hudMeta[1], 0, this.hudMeta[1] * -1)).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                        if ((Check = Ref.mc.world.getBlockState(this.hudCentre.add(this.hudMeta[1] + (dif_x < 0 ? 1 : -1), 0, this.hudMeta[1] * -1 + (dif_x < 0 ? -1 : 1))).getBlock()) == Blocks.AIR || Check == Blocks.STRUCTURE_VOID)
                            return;
                }
                break;
        }
        this.hudMeta[1] = 0;
    }

    void draw() {
        if (!inRange) return;

        RenderManager renderManager = Ref.mc.getRenderManager();

        Double[] pos = new Double[]{
                this.hudCentre.getX() - renderManager.viewerPosX,
                this.hudCentre.getY() - renderManager.viewerPosY,
                this.hudCentre.getZ() - renderManager.viewerPosZ
        };

        switch (this.hudMeta[0]) {
            case -1:
            case 0: break;
            case 1: pos[0] += this.hudMeta[1]; break;
            case 2: pos[2] += this.hudMeta[1]; break;
            case 3: pos[0] += this.hudMeta[1]; pos[2] += this.hudMeta[1]; break;
            case 4: pos[0] += this.hudMeta[1]; pos[2] -= this.hudMeta[1]; break;
        }

        if (!this.Harvestable) {
            if (renderManager.viewerPosY <= this.hudCentre.getY() - 1.3) {
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1], pos[2] + 0.35, pos[0] + 0.65, pos[1] + 0.125, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.25f);
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1] + 0.875, pos[2] + 0.35, pos[0] + 0.65, pos[1] + 1, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.25f);
            } else {
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1] + 0.875, pos[2] + 0.35, pos[0] + 0.65, pos[1] + 1, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.25f);
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1], pos[2] + 0.35, pos[0] + 0.65, pos[1] + 0.125, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.25f);
            }

            RenderGlobal.drawBoundingBox(pos[0] + 0.375, pos[1] + 0.5, pos[2] + 0.375, pos[0] + 0.625, pos[1] + 0.5, pos[2] + 0.625, 0.0f, 0.0f, 0.0f, 0.5f);

            RenderGlobal.renderFilledBox(pos[0] + 0.375, pos[1] + 0.125, pos[2] + 0.375, pos[0] + 0.625, pos[1] + 0.125 + Math.min(this.percent * 0.75, 0.75), pos[2] + 0.625, this.typeColour[0], this.typeColour[1], this.typeColour[2], 0.35f);
            if (this.percent < 1.0f)
                RenderGlobal.renderFilledBox(pos[0] + 0.375, pos[1] + 0.125 + Math.min(this.percent * 0.75, 0.75), pos[2] + 0.375, pos[0] + 0.625, pos[1] + 0.875, pos[2] + 0.625, 0.5f, 0.5f, 0.5f, 0.25f);


        } else {
            if (renderManager.viewerPosY <= this.hudCentre.getY() - 1.3) {
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1], pos[2] + 0.35, pos[0] + 0.65, pos[1] + 0.125, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.5f);
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1] + 0.875, pos[2] + 0.35, pos[0] + 0.65, pos[1] + 1, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.5f);
            } else {
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1] + 0.875, pos[2] + 0.35, pos[0] + 0.65, pos[1] + 1, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.5f);
                RenderGlobal.renderFilledBox(pos[0] + 0.35, pos[1], pos[2] + 0.35, pos[0] + 0.65, pos[1] + 0.125, pos[2] + 0.65, this.colours[0], this.colours[1], this.colours[2], 0.5f);
            }

            if (this.canHarvest)
                RenderGlobal.renderFilledBox(pos[0] + 0.375, pos[1] + 0.125, pos[2] + 0.375, pos[0] + 0.625, pos[1] + 0.875, pos[2] + 0.625, this.typeColour[0], this.typeColour[1], this.typeColour[2], 0.35f);
            else
                RenderGlobal.renderFilledBox(pos[0] + 0.375, pos[1] + 0.125, pos[2] + 0.375, pos[0] + 0.625, pos[1] + 0.875, pos[2] + 0.625, 0f, 0, 0, 0.35f);
        }
    }









    void drawDebug() {
        if (!inRange) return;
        RenderManager renderManager = Ref.mc.getRenderManager();

        Double[] pos = new Double[]{
                this.barrier.getX() - renderManager.viewerPosX,
                this.barrier.getY() - renderManager.viewerPosY,
                this.barrier.getZ() - renderManager.viewerPosZ,
                this.checkMark.getX() - renderManager.viewerPosX,
                this.checkMark.getY() - renderManager.viewerPosY,
                this.checkMark.getZ() - renderManager.viewerPosZ,
                this.hudCentre.getX() - renderManager.viewerPosX,
                this.hudCentre.getY() - renderManager.viewerPosY,
                this.hudCentre.getZ() - renderManager.viewerPosZ
        };

        Ref.drawLine(pos[0]+0.5, pos[1]+0.5, pos[2]+0.5, pos[3]+0.5, pos[4]+0.5, pos[5]+0.5, 0.5f,0.5f,0.5f,0.75f);

        if (Ref.mc.world.getBlockState(this.barrier).getBlock() == Blocks.BARRIER)
            RenderGlobal.renderFilledBox(pos[0]+0.25, pos[1] + 0.25, pos[2]+0.25, pos[0]+0.75, pos[1]+0.75, pos[2]+0.75, 0.6f, 0.0f, 0.0f, 1);
        else
            RenderGlobal.renderFilledBox(pos[0]+0.25, pos[1] + 0.25, pos[2]+0.25, pos[0]+0.75, pos[1]+0.75, pos[2]+0.75, 0f, 0.6f, 0.0f, 1);
        RenderGlobal.renderFilledBox(pos[3]+0.35, pos[4] + 0.35, pos[5]+0.35, pos[3]+0.65, pos[4]+0.65, pos[5]+0.65, 1.0f, 0.6f, 0.0f, 1);
        RenderGlobal.drawBoundingBox(pos[3], pos[4], pos[5], pos[3]+1, pos[4]+1, pos[5]+1, 1.0f, 0.6f, 0.0f, 1);

        RenderGlobal.drawBoundingBox(pos[6]+0.35, pos[7], pos[8]+0.35, pos[6]+0.65, pos[7]+1, pos[8]+0.65, 0.6f, 0.0f, 0.6f, 1);
    }
}
