package wynn.pendium.hud.components.features;

import net.minecraft.client.Minecraft;

import java.io.File;

/**
 * Could not think of a name for this, yikes.
 */
public abstract class WynnpendiumGuiFeature {


    private int x;
    private int y;
    private int defaultX, defaultY;

    public WynnpendiumGuiFeature(int defaultX, int defaultY) {



        this.defaultX = defaultX;
        this.defaultY = defaultY;

    }


    public abstract void renderDummy(int x, int y, Minecraft mc);


    public abstract int getWidth();
    public abstract int getHeight();

    public int getX() {

        return this.x != 0 ? this.x : this.defaultX;
    }
    public int getY() {

        return this.y != 0 ? this.y : defaultY;
    }


    public int getDefaultX() {
        return this.defaultX;
    }
    public int getDefaultY() {
        return this.defaultY;
    }


    public int xOffset() {
        return 0;
    }
    public int yOffset() {
        return 0;
    }
    private void tryLoadLocationFromConfigFile(File config) {

    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
}
