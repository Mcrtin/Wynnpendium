package wynn.pendium.hud.screens;

import net.minecraft.client.gui.GuiScreen;


import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wynn.pendium.Ref;
import wynn.pendium.hud.HudEvents;
import wynn.pendium.features.FeatureManager;
import wynn.pendium.features.WynnpendiumGuiFeature;
import wynn.pendium.hud.components.locationui.LocationSettingButton;

import java.io.IOException;
import java.util.*;

public class ScreenEditLocations extends GuiScreen {


    private List<LocationSettingButton> buttons = new LinkedList<>();

    private Map<WynnpendiumGuiFeature, LocationSettingButton> buttonHashMap = new HashMap<>();


    WynnpendiumGuiFeature dragging;
    float xOffset, yOffset;


    @Override
    public void drawBackground(int tint) {
        super.drawBackground(tint);
    }

    @Override
    public void initGui() {
        for (WynnpendiumGuiFeature feature : FeatureManager.enabledFeatures) {
            LocationSettingButton button = new LocationSettingButton(feature);
            buttonList.add(button);
            buttonHashMap.put(feature, button);

        }

        super.initGui();

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        onMouseMove(mouseX, mouseY);
        for (LocationSettingButton button : buttonHashMap.values()) {
//            button.drawButton(Ref.mc, mouseX, mouseY, partialTicks);

            WynnpendiumGuiFeature f = button.component;


            button.checkHoveredAndDrawBox(f.getX() - f.getWidth() / 2, f.getX() + f.getWidth() / 2, f.getY() - f.getHeight(), f.getY() + f.getHeight() / 2, 1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleInput() throws IOException {
        super.handleInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {

            onGuiClosed();
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {

        super.handleKeyboardInput();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof LocationSettingButton) {
            LocationSettingButton buttonLocation = (LocationSettingButton) button;
            dragging = buttonLocation.getComponent();

            ScaledResolution sr = new ScaledResolution(mc);
            float minecraftScale = sr.getScaleFactor();
            float floatMouseX = Mouse.getX() / minecraftScale;
            float floatMouseY = (mc.displayHeight - Mouse.getY()) / minecraftScale;

            xOffset = floatMouseX - dragging.getX();
            yOffset = floatMouseY - dragging.getY();

            System.out.println("button selected");
        }
        System.out.println("action performed");
        super.actionPerformed(button);
    }

    public void onMouseMove(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        float minecraftScale = sr.getScaleFactor();
        float floatMouseX = mouseX / minecraftScale;
        float floatMouseY = (mc.displayHeight - mouseY) / minecraftScale;

        if (dragging != null) {
            LocationSettingButton buttonLocation = buttonHashMap.get(dragging);
            if (buttonLocation == null) {
                System.out.println("location is null");
                return;
            }








//            do update config values here stuff


            dragging.setX(mouseX);
            dragging.setY(mouseY);



        }
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = null;
    }


    @Override
    public void onGuiClosed() {
        System.out.println("closing");
        HudEvents.screen = null;
        Ref.mc.currentScreen = null;
    }
}

