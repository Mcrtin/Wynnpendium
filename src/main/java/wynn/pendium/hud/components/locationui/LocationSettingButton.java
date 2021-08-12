package wynn.pendium.hud.components.locationui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import wynn.pendium.hud.components.ButtonComponent;
import wynn.pendium.hud.components.features.WynnpendiumGuiFeature;
import wynn.pendium.hud.utils.ColorCode;
import wynn.pendium.hud.utils.RenderUtils;

public class LocationSettingButton extends ButtonComponent {




    private float boxXOne;
    private float boxXTwo;
    private float boxYOne;
    private float boxYTwo;

    private float scale;
    private WynnpendiumGuiFeature feature;
    private WynnpendiumGuiFeature lastHoveredFeature;

    /**
     * Create a button that allows you to change the location of a GUI element.
     */
    public LocationSettingButton(WynnpendiumGuiFeature gui) {
        super(-1, 0, 0, null, gui);
        this.feature = gui;
        this.lastHoveredFeature = null;

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
//        float scale = main.getConfigValues().getGuiScale(feature);
        float scale = 1.0f;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        feature.renderDummy(feature.getX(), feature.getY(), mc);
        GlStateManager.popMatrix();

        if (hovered) {
            lastHoveredFeature = feature;
        }
    }

    /**
     * This just updates the hovered status and draws the box around each feature. To avoid repetitive code.
     */
    public void checkHoveredAndDrawBox(float boxXOne, float boxXTwo, float boxYOne, float boxYTwo, float scale) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float minecraftScale = sr.getScaleFactor();
        float floatMouseX = Mouse.getX() / minecraftScale;
        float floatMouseY = (Minecraft.getMinecraft().displayHeight - Mouse.getY()) / minecraftScale;

        hovered = floatMouseX >= boxXOne * scale && floatMouseY >= boxYOne * scale && floatMouseX < boxXTwo * scale && floatMouseY < boxYTwo * scale;
        int boxAlpha = 70;
        if (hovered) {
            boxAlpha = 120;
        }
        int boxColor = ColorCode.GRAY.getColor(boxAlpha);
        RenderUtils.drawRect(boxXOne, boxYOne, boxXTwo, boxYTwo, boxColor);

        this.boxXOne = boxXOne;
        this.boxXTwo = boxXTwo;
        this.boxYOne = boxYOne;
        this.boxYTwo = boxYTwo;

//        if (this.feature == Feature.DEFENCE_ICON) {
//            this.boxXOne *= scale;
//            this.boxXTwo *= scale;
//            this.boxYOne *= scale;
//            this.boxYTwo *= scale;
//        }

        this.scale = scale;
    }

    /**
     * Because the box changes with the scale, have to override this.
     */
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && hovered;
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    public float getBoxYOne() {
        return boxYOne;
    }

    public void setBoxYOne(float boxYOne) {
        this.boxYOne = boxYOne;
    }

    public float getBoxXTwo() {
        return boxXTwo;
    }

    public void setBoxXTwo(float boxXTwo) {
        this.boxXTwo = boxXTwo;
    }

    public float getBoxXOne() {
        return boxXOne;
    }

    public void setBoxXOne(float boxXOne) {
        this.boxXOne = boxXOne;
    }

    public float getBoxYTwo() {
        return boxYTwo;
    }

    public void setBoxYTwo(float boxYTwo) {
        this.boxYTwo = boxYTwo;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
