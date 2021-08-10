package wynn.pendium.professor.craftsman;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import wynn.pendium.Ref;
import wynn.pendium.professor.professor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class events {

    private static final Pattern XP_CHAT = Pattern.compile("^(?:x(?<Multiplier>\\d+(?:\\.\\d)?) )?\\[\\+\\d+ . (?<Profession>[a-zA-Z]+) XP] \\[(?<Percent>\\d+)%]$");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void eventHandler(final ClientChatReceivedEvent event) {
        if (!Ref.inGame() || !professor.Enabled || event.getType() == ChatType.GAME_INFO) return;

        Matcher match;
        if (!(match = XP_CHAT.matcher(event.getMessage().getUnformattedText())).matches()) return;

        if (Ref.mc.inGameHasFocus || !(Ref.mc.currentScreen instanceof GuiContainer)) return;
        GuiContainer gui = (GuiContainer) Ref.mc.currentScreen;
        if (!gui.inventorySlots.getSlot(0).inventory.getName().replaceAll("\\u00A7[a-z0-9]", "").equals(match.group("Profession"))) return;


    }

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END) || !professor.Enabled) return;

        if (!Ref.inGame()) return;

        if (Ref.mc.inGameHasFocus) scraper.resetOutputMem();
        else scraper.releaseOutputMem();
    }
}
