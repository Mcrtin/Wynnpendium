package wynn.pendium.gluttony;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;

public class effectHud extends Gui {

    public static void showEffects() {
        //if (LastUpdate + 20000 < System.currentTimeMillis()) return;

        int width = new ScaledResolution(Ref.mc).getScaledWidth() - 38;
        int height = (int)(new ScaledResolution(Ref.mc).getScaledHeight() * 0.5) + 25;


        float size = 1f;
        float inverse = (float) Math.pow(size, -1);
        int rowNum = 0;

        GL11.glScalef(size, size, size);

        for (DisplayEffect row : effectManager.effectCache) {
            row.draw(width, height, size, inverse, rowNum++);
        }

        GL11.glScalef((float)Math.pow(size,-1), (float)Math.pow(size,-1), (float)Math.pow(size,-1));
    }
}
