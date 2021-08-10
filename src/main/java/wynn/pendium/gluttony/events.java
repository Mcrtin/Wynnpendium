package wynn.pendium.gluttony;

import net.minecraft.init.Blocks;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import wynn.pendium.Ref;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class events {

    private static final Pattern CHAT_EFFECT = Pattern.compile("^\\[\\+?(?<Value>-?\\d+)(?<Suffix>%|/4s|/3s| tier)? (?<Stat>[A-Za-z 0-9]+) for (?<Duration>\\d+) seconds]$");
    private static final Pattern CHAT_HEAL = Pattern.compile("^\\[\\+(?<Value>\\d+) \\u2764]$");
    private static final Pattern DEFAULT_POTION = Pattern.compile("^\\[\\+?(?<Value>-?\\d+) .(?: (?<Stat>[A-Za-z]+))? for (?<Duration>\\d+) seconds]$");
    private static final Pattern MISC_POTION = Pattern.compile("^\\[(?<Stat>[A-Za-z 0-9]+)? for (?<Duration>\\d+) seconds]$");

    private static boolean enableHud = true;

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) { // Main logic
        if (event.phase.equals(TickEvent.Phase.START) || !gluttony.Enabled) return;

        if (!Ref.inGame()) {
            effectManager.wipe();
            return;
        }

        effectManager.updateHand(Ref.mc.player.getHeldItemMainhand());
        effectManager.updateTick();
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void eventHandler(final ClientChatReceivedEvent event) {
        if (!Ref.inGame() || !gluttony.Enabled || event.getType() == ChatType.GAME_INFO) return;

        Matcher match;
        if ((match = CHAT_EFFECT.matcher(event.getMessage().getUnformattedText())).matches()) {
            effectManager.processEffect(match.group("Stat"), (match.group("Suffix") == null ? "" : match.group("Suffix")), Integer.parseInt(match.group("Value")), Integer.parseInt(match.group("Duration")));
            return;
        }

        if ((match = CHAT_HEAL.matcher(event.getMessage().getUnformattedText())).matches()) {
            effectManager.processHeal(Integer.parseInt(match.group("Value")));
            return;
        }

        if ((match = DEFAULT_POTION.matcher(event.getMessage().getUnformattedText())).matches()) {
            if (match.group("Stat") == null)
                effectManager.processEffect("Mana", "\u273A", Integer.parseInt(match.group("Value")), Integer.parseInt(match.group("Duration")));
            else
                effectManager.processEffect(match.group("Stat"), "", Integer.parseInt(match.group("Value")), Integer.parseInt(match.group("Duration")));
            return;
        }

        if ((match = MISC_POTION.matcher(event.getMessage().getUnformattedText())).matches()) {
            effectManager.processEffect(match.group("Stat"), "", 0, Integer.parseInt(match.group("Duration")));
            return;
        }

    }

    @SubscribeEvent
    public void eventHandler(final InputEvent.MouseInputEvent e) {
        if (!gluttony.Enabled || !Ref.inGame() || !Ref.mc.inGameHasFocus || !(Mouse.getEventButton() == 1 && Mouse.getEventButtonState())) return;

        if (Consumable.isConsumable(Ref.mc.player.getHeldItemMainhand())) {
            effectManager.addConsumable(new Consumable(Ref.mc.player.getHeldItemMainhand()));
        }
    }

    @SubscribeEvent
    public void eventHandler(final RenderGameOverlayEvent.Text event) {
        if (gluttony.Enabled && Ref.inGame() && enableHud)
            effectHud.showEffects();
    }


    @SubscribeEvent
    public void eventHandler(final InputEvent.KeyInputEvent event) {
        if (Ref.inGame() && gluttony.toggle.isPressed()) {
            enableHud = !enableHud;
            effectManager.updateCache();
        }
    }
}
