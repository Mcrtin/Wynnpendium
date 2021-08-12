package wynn.pendium.hud.screens;

import com.google.common.collect.Maps;
import javafx.util.Pair;
import net.minecraft.client.gui.GuiScreen;


import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wynn.pendium.Ref;
import wynn.pendium.hud.HudEvents;
import wynn.pendium.hud.components.features.ComponentToolDurability;
import wynn.pendium.hud.components.features.FeatureManager;
import wynn.pendium.hud.components.features.WynnpendiumGuiFeature;
import wynn.pendium.hud.components.locationui.LocationSettingButton;

import javax.xml.stream.Location;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        for (WynnpendiumGuiFeature feature: FeatureManager.enabledFeatures) {
            LocationSettingButton button = new LocationSettingButton(feature);
            buttonList.add(button);
            buttonHashMap.put(feature, button);

        }

        super.initGui();

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        for (LocationSettingButton button : buttonHashMap.values()) {
            button.drawButton(Ref.mc, mouseX, mouseY, partialTicks);

            WynnpendiumGuiFeature f = button.component;


            button.checkHoveredAndDrawBox(f.getX() - f.getWidth() / 2, f.getX() + f.getWidth() / 2, f.getY() - f.getHeight(), f.getY() + f.getHeight() / 2, 1);
        }
        onMouseMove(mouseX, mouseY);
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
        float floatMouseX = Mouse.getX() / minecraftScale;
        float floatMouseY = (mc.displayHeight - Mouse.getY()) / minecraftScale;

        if (dragging != null) {
            LocationSettingButton buttonLocation = buttonHashMap.get(dragging);
            if (buttonLocation == null) {
                System.out.println("location is null");
                return;
            }


            float x = floatMouseX - dragging.getX();
            float y = floatMouseY - dragging.getY();

            float scaledX1 = buttonLocation.getBoxXOne() * buttonLocation.getScale();
            float scaledY1 = buttonLocation.getBoxYOne() * buttonLocation.getScale();
            float scaledX2 = buttonLocation.getBoxXTwo() * buttonLocation.getScale();
            float scaledY2 = buttonLocation.getBoxYTwo() * buttonLocation.getScale();
            float scaledWidth = scaledX2 - scaledX1;
            float scaledHeight = scaledY2 - scaledY1;

            boolean xSnapped = false;
            boolean ySnapped = false;


            if (!xSnapped) {
                x -= xOffset;
            }

            if (!ySnapped) {
                y -= yOffset;
            }

//            do update config values here stuff

            System.out.println("setting new things");
            dragging.setX((int) x);
            dragging.setY((int) y);



        }
    }

    @Override
    public void onGuiClosed() {
        System.out.println("closing");
        HudEvents.screen = null;
        Ref.mc.currentScreen = null;
    }
}

