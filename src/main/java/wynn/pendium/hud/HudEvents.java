package wynn.pendium.hud;

import com.sun.javafx.stage.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import wynn.pendium.Ref;
import wynn.pendium.hud.screens.ScreenDisplaySettings;

public class HudEvents {

    private ScreenDisplaySettings settings;


    public static GuiScreen screen;

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START) || !Hud.Enabled) return;

        Hud.main.updateMessages();
        Hud.announcement.updateMessages();
        Hud.console.updateMessages();




    }
    @SubscribeEvent
    public void onRender(TickEvent.ClientTickEvent e) {
        if (screen != null && Ref.mc.currentScreen == null && Ref.mc.currentScreen != screen) {
            Ref.mc.displayGuiScreen(screen);
            Ref.mc.currentScreen = screen;
        }
    }



    @SubscribeEvent
    public void eventHandler(final RenderGameOverlayEvent.Text event) {
        if (Ref.inGame() && Hud.Enabled) {
            Hud.main.show();
            Hud.announcement.show();
            Hud.console.show();
        }
    }
}
