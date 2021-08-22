package wynn.pendium.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;
import wynn.pendium.utils.RenderUtils;
import wynn.pendium.professor.NodeType;

public class HudExperienceBar extends WynnpendiumGuiFeature {

    private static int Percentage = 0;
    private static int Colour = 0xaa5555ff;
    private static String profession = "";
    private static long LastUpdate = 0;

    public HudExperienceBar() {
        super(900, 120, "Display Profession Experience Bar");
    }

    public static void updateXPBar(NodeType type, int level, int percentage) {
        Percentage = percentage;
        LastUpdate = System.currentTimeMillis();

        switch (type) {
            case FARM:
                Colour = 0xaaffaa00;
                profession = (level == 0 ? "Farming" : "Farming " + level);
                break;
            case WOOD:
                Colour = 0xaa00aa00;
                profession = "🪓 " + (level == 0 ? "Woodcutting" : "Woodcutting " + level);
                break;
            case MINE:
                Colour = 0xaaaa0000;
                profession = "\u26CF " + (level == 0 ? "Mining" : "Mining " + level);
                break;
            case FISH:
                Colour = 0xaa5555ff;
                profession = "\uD83D\uDC1F " + (level == 0 ? "Fishing" : "Fishing " + level);
                break;
            default:
                Colour = 0x0000000;
                profession = "";
        }
    }


    @Override
    public void doRender(int x, int y, Minecraft mc) {

        if (LastUpdate + 20000 < System.currentTimeMillis()) return;

        String msg = Percentage + "%";

        float scale = 4;

        GL11.glScalef((float) Math.pow(scale, -1), (float) Math.pow(scale, -1), (float) Math.pow(scale, -1));
//        RenderUtils.drawRect((x - 150) * scale, y * scale, (x - 150 + Percentage * 3) * scale, (y + 2) * scale, Colour);
//        RenderUtils.drawRect((x - 150 + Percentage * 3) * scale, y * scale, (x + 150) * scale, (y + 2) * scale, 0x77000000);


        RenderUtils.drawRect((x - 75) * scale, y * scale, (x - 75 + Percentage * 3) * scale, (y + 2) * scale, Colour);
        RenderUtils.drawRect((x - 75 + Percentage * 3) * scale, y * scale, (x + 75) * scale, (y + 2) * scale, 0x77000000);

        GL11.glScalef(scale, scale, scale);

        Ref.mc.fontRenderer.drawString(msg, (x - Ref.mc.fontRenderer.getStringWidth(msg) / 2), y + 4, Colour);
        Ref.mc.fontRenderer.drawString(profession, (x - Ref.mc.fontRenderer.getStringWidth(profession) / 2), y - 10, Colour);
    }

    public static void showXP() {
        if (LastUpdate + 20000 < System.currentTimeMillis()) return;

        int width = (int) (new ScaledResolution(Ref.mc).getScaledWidth() * 0.5);
        int height = (int) (new ScaledResolution(Ref.mc).getScaledHeight() * 0.075) + 25;
        String msg = Percentage + "%";

        int scale = 1;

        GL11.glScalef((float) Math.pow(scale, -1), (float) Math.pow(scale, -1), (float) Math.pow(scale, -1));
        RenderUtils.drawRect((width - 150) * scale, height * scale, (width - 150 + Percentage * 3) * scale, (height + 2) * scale, Colour);
        RenderUtils.drawRect((width - 150 + Percentage * 3) * scale, height * scale, (width + 150) * scale, (height + 2) * scale, 0x77000000);
        GL11.glScalef(scale, scale, scale);

        Ref.mc.fontRenderer.drawString(msg, (width - Ref.mc.fontRenderer.getStringWidth(msg) / 2), height + 4, Colour);
        Ref.mc.fontRenderer.drawString(profession, (width - Ref.mc.fontRenderer.getStringWidth(profession) / 2), height - 10, Colour);
    }

    @Override
    public void renderDummy(int x, int y, Minecraft mc) {

        Percentage = 54;
        String profession = "Example Profession";


        String msg = Percentage + "%";

        float scale = 4;

        GlStateManager.disableBlend();
        GL11.glScalef((float) Math.pow(scale, -1), (float) Math.pow(scale, -1), (float) Math.pow(scale, -1));
        RenderUtils.drawRect((x - 75) * scale, y * scale, (x - 75 + Percentage * 3) * scale, (y + 2) * scale, Colour);
        RenderUtils.drawRect((x - 75 + Percentage * 3) * scale, y * scale, (x + 75) * scale, (y + 2) * scale, 0x77000000);
        GL11.glScalef(scale, scale, scale);

        Ref.mc.fontRenderer.drawString(msg, (x - Ref.mc.fontRenderer.getStringWidth(msg) / 2), y + 4, Colour);
        Ref.mc.fontRenderer.drawString(profession, (x - Ref.mc.fontRenderer.getStringWidth(profession) / 2), y - 10, Colour);
        GlStateManager.enableBlend();
    }

    public int getWidth() {

        return Percentage * 3;
    }

    public int getHeight() {
        return 14;
    }
}
