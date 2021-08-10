package wynn.pendium.professor.xp;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;

public class xpHud extends Gui {

    private static int Percentage = 0;
    private static int Colour = 0xaa5555ff;
    private static String profession = "";
    private static long LastUpdate = 0;

    static void updateXPBar(NodeType type, int level, int percentage) {
        Percentage = percentage;
        LastUpdate = System.currentTimeMillis();

        switch (type) {
            case FARM: Colour = 0xaaffaa00; profession = (level == 0 ? "Farming" : "Farming " + level); break;
            case WOOD: Colour = 0xaa00aa00; profession = (level == 0 ? "Woodcutting" : "Woodcutting " + level); break;
            case MINE: Colour = 0xaaaa0000; profession = (level == 0 ? "Mining" : "Mining " + level); break;
            case FISH: Colour = 0xaa5555ff; profession = (level == 0 ? "Fishing" : "Fishing " + level); break;
        }
    }

    public static void showXP() {
        if (LastUpdate + 20000 < System.currentTimeMillis()) return;

        int width = (int)(new ScaledResolution(Ref.mc).getScaledWidth() * 0.5);
        int height = (int)(new ScaledResolution(Ref.mc).getScaledHeight() * 0.075) + 25;
        String msg = Percentage + "%";

        int scale = 1;

        GL11.glScalef((float)Math.pow(scale,-1), (float)Math.pow(scale,-1), (float)Math.pow(scale,-1));
        drawRect((width -150)*scale, height*scale, (width -150 + Percentage*3)*scale, (height+2)*scale, Colour);
        drawRect((width -150 + Percentage*3)*scale, height*scale, (width +150)*scale, (height+2)*scale, 0x77000000);
        GL11.glScalef(scale, scale, scale);

        Ref.mc.fontRenderer.drawString(msg, (width - Ref.mc.fontRenderer.getStringWidth(msg) / 2), height + 4, Colour);
        Ref.mc.fontRenderer.drawString(profession, (width - Ref.mc.fontRenderer.getStringWidth(profession) / 2), height - 10, Colour);
    }
}
