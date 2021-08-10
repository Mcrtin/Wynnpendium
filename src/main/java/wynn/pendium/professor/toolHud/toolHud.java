package wynn.pendium.professor.toolHud;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;

import java.util.ArrayList;
import java.util.List;

public class toolHud extends Gui {

    private static int Percentage = 0;
    private static int Colour = 0;
    private static String Durability = "";

    static NodeType ActiveTool = NodeType.NONE;
    static List<DisplayStack> hudList = new ArrayList<>();

    static void update(NodeType type, String CurDur, String MaxDur) {
        Percentage = (int) Math.ceil(Integer.parseInt(CurDur) * 100 / (double) Integer.parseInt(MaxDur));
        Durability = CurDur;

        switch (type) {
            case FARM: Colour = 0xaaffaa00; break;
            case WOOD: Colour = 0xaa00aa00; break;
            case MINE: Colour = 0xaaaa0000; break;
            case FISH: Colour = 0xaa5555ff; break;
        }
    }

    public static void showDurability() {
        if (ActiveTool.equals(NodeType.NONE)) return;

        int width = (int)(new ScaledResolution(Ref.mc).getScaledWidth() * 0.5);
        int height = (int)(new ScaledResolution(Ref.mc).getScaledHeight() * 0.5) + 25;
        drawRect(width -50, height, width -50 + Percentage, height+2, Colour);
        drawRect(width -50 + Percentage, height, width +50, height+2, 0x77000000);
        Ref.mc.fontRenderer.drawString(Durability, (width - Ref.mc.fontRenderer.getStringWidth(Durability) / 2), height + 4, Colour);


        if (!Ref.mc.inGameHasFocus) return;

        RenderHelper.enableGUIStandardItemLighting();
        int offset = width -15 - (hudList.size()*15)/2;
        for (DisplayStack item : hudList) {
            item.display((offset += 15), height + 20);
        }
        GlStateManager.disableLighting();
    }
}
