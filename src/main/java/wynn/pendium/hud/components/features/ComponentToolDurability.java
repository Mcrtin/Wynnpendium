package wynn.pendium.hud.components.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;

import java.util.ArrayList;
import java.util.List;

import static wynn.pendium.hud.utils.RenderUtils.drawRect;

public class ComponentToolDurability extends WynnpendiumGuiFeature {

    private static int Percentage = 0;
    private static int Colour = 0;
    private static String Durability = "";

    public static NodeType activeTool = NodeType.NONE;
    public static List<DisplayStack> hudList = new ArrayList<>();

    public ComponentToolDurability() {
        super(500, 645);
    }

    public static void update(NodeType type, String CurDur, String MaxDur) {
        Percentage = (int) Math.ceil(Integer.parseInt(CurDur) * 100 / (double) Integer.parseInt(MaxDur));
        Durability = CurDur;

        switch (type) {
            case FARM:
                Colour = 0xaaffaa00;
                break;
            case WOOD:
                Colour = 0xaa00aa00;
                break;
            case MINE:
                Colour = 0xaaaa0000;
                break;
            case FISH:
                Colour = 0xaa5555ff;
                break;
        }
    }

    public static void showDurability() {
        if (activeTool.equals(NodeType.NONE)) return;

        int width = (int) (new ScaledResolution(Ref.mc).getScaledWidth() * 0.5);
        int height = (int) (new ScaledResolution(Ref.mc).getScaledHeight() * 0.5) + 25;
        drawRect(width - 50, height, width - 50 + Percentage, height + 2, Colour);
        drawRect(width - 50 + Percentage, height, width + 50, height + 2, 0x77000000);
        Ref.mc.fontRenderer.drawString(Durability, (width - Ref.mc.fontRenderer.getStringWidth(Durability) / 2), height + 4, Colour);


        if (!Ref.mc.inGameHasFocus) return;

        RenderHelper.enableGUIStandardItemLighting();
        int offset = width - 15 - (hudList.size() * 15) / 2;
        for (DisplayStack item : hudList) {
            item.display((offset += 15), height + 20);
        }
        GlStateManager.disableLighting();
    }

    /**
     * For drawing inside of the Settings GUI.
     * Displays a basic "default" version of this Component.
     *
     * @param x X position of where we should render this
     * @param y Y position of where we should render this
     */
    public void renderDummy(int x, int y, Minecraft mc) {
//        GlStateManager.translate(x, y, 0);

        int Percentage = 100;


        String Durability = "100";


        drawRect(x - 50, y, x - 50 + Percentage, y + 2, Colour);
        drawRect(x - 50 + Percentage, y, x + 50, y + 2, 0x77000000);
        mc.fontRenderer.drawString(Durability, (x - Ref.mc.fontRenderer.getStringWidth(Durability) / 2), y + 4, Colour);

//        if (!Ref.mc.inGameHasFocus) return;

        RenderHelper.enableGUIStandardItemLighting();
        int offset = x - 15 - (15) / 2;

        DisplayStack item = new DisplayStack(NodeType.MINE, new ItemStack(Items.DIAMOND_PICKAXE), 1, 3);

        item.display((offset += 15), y + 20);

        GlStateManager.disableLighting();

    }

    public int getWidth() {

        return 50;
    }

    public int getHeight() {
        return 20;
    }
}
