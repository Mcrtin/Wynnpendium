package wynn.pendium.hud.screens;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import wynn.pendium.Ref;
import wynn.pendium.hud.HudEvents;

import java.io.IOException;

public class ScreenDisplaySettings extends GuiScreen {



    @Override
    public void drawBackground(int tint) {
        super.drawBackground(tint);
    }

    @Override
    public void initGui() {
        super.initGui();

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        ComponentToolDurability.renderDummy(500, 500);
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
        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        System.out.println("closing");
        HudEvents.screen = null;
        Ref.mc.currentScreen = null;
    }
}
