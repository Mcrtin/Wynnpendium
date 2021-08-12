package wynn.pendium.hud.components;

import net.minecraft.client.gui.GuiButton;
import wynn.pendium.hud.components.features.WynnpendiumGuiFeature;

public class ButtonComponent extends GuiButton {

    // The feature that this button moves.
    public WynnpendiumGuiFeature component;

    /**
     * Create a button that is assigned a feature (to toggle/change color etc.).
     */
    public ButtonComponent(int buttonId, int x, int y, String buttonText, WynnpendiumGuiFeature buttonComponent) {
        super(buttonId, x, y, buttonText);
        this.component = buttonComponent;
    }

    public WynnpendiumGuiFeature getComponent() {
        return component;
    }
}


