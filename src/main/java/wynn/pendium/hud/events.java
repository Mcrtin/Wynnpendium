package wynn.pendium.hud;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import wynn.pendium.Ref;

public class events {

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START) || !hud.Enabled) return;

        hud.main.updateMessages();
        hud.announcement.updateMessages();
        hud.console.updateMessages();
    }

    @SubscribeEvent
    public void eventHandler(final RenderGameOverlayEvent.Text event) {
        if (Ref.inGame() && hud.Enabled) {
            hud.main.show();
            hud.announcement.show();
            hud.console.show();
        }
    }
}
