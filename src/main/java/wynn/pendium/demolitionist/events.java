package wynn.pendium.demolitionist;

import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import wynn.pendium.Ref;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class events {

    private static final Pattern BOMBBAR = Pattern.compile("^(?<Bomb>[a-zA-Z ]+) from (?<Thrower>[a-zA-Z0-9_]{3,16}) \\[(?<Duration>\\d+) min]$");
    private static final Pattern CHATBOMB = Pattern.compile("^(?<Thrower>[a-zA-Z0-9_]{3,16}) has thrown a (?<Bomb>[a-zA-Z ]+) Bomb!? .+?(?: (?<Duration>\\d+) minutes!)?$");
    private static String bossBarMem;

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) { // Main logic
        if (event.phase.equals(TickEvent.Phase.START) || !demolitionist.Enabled) return;

        if (Ref.inLobby()) bombManager.wipe();
        if (!Ref.inGame()) return;

        bombManager.tickBombs();


        List<String> bossNames;
        if ((bossNames = getBossBarNames()).isEmpty()) return;



//        Bomb Checker
        String bossBar = bossNames.get(0);
        if (!bossBar.equals(bossBarMem)) {
            bossBarMem = bossBar;
            Matcher match;
            if ((match = BOMBBAR.matcher(Ref.noColour(bossBar))).matches())
                switch (match.group("Bomb")) {
                    case "Double Combat XP":
                        bombManager.registerBomb(Bomb.Type.DXP, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                        break;
                    case "Double Loot":
                        bombManager.registerBomb(Bomb.Type.DLOOT, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                        break;
                    case "Free Dungeon Entry":
                        bombManager.registerBomb(Bomb.Type.DBOMB, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                        break;
                    case "Double Profession XP":
                        bombManager.registerBomb(Bomb.Type.PXP, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                        break;
                    case "Double Profession Speed":
                        bombManager.registerBomb(Bomb.Type.PSPEED, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                        break;
                    default:
                }
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void eventHandler(final ClientChatReceivedEvent event) {
        if (!Ref.inGame() || !demolitionist.Enabled || event.getType() == ChatType.GAME_INFO) return;

        Matcher match;
        if ((match = CHATBOMB.matcher(event.getMessage().getUnformattedText())).matches()) {
            switch (match.group("Bomb")) {
                case "Party":
                    bombManager.registerBomb(Bomb.Type.PARTY, match.group("Thrower"), 45);
                    break;
                case "Disguise":
                    bombManager.registerBomb(Bomb.Type.DISGUISE, match.group("Thrower"), 30);
                    break;
                case "Item":
                    bombManager.registerBomb(Bomb.Type.ITEM, match.group("Thrower"), 25);
                    break;
                case "Ingredient":
                    bombManager.registerBomb(Bomb.Type.INGREDIENT, match.group("Thrower"), 25);
                    break;
                case "Soul Point":
                    bombManager.registerBomb(Bomb.Type.SOUL, match.group("Thrower"), 2);
                    break;

                case "Combat XP":
                    bombManager.registerBomb(Bomb.Type.DXP, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                    break;
                case "Loot":
                    bombManager.registerBomb(Bomb.Type.DLOOT, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                    break;
                case "Dungeon":
                    bombManager.registerBomb(Bomb.Type.DBOMB, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                    break;
                case "Profession XP":
                    bombManager.registerBomb(Bomb.Type.PXP, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                    break;
                case "Profession Speed":
                    bombManager.registerBomb(Bomb.Type.PSPEED, match.group("Thrower"), Integer.parseInt(match.group("Duration")));
                    break;
                default:
            }
        }

    }

    @SuppressWarnings("unchecked")
    private static List<String> getBossBarNames() {//throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        GuiBossOverlay bossOverlay = Ref.mc.ingameGUI.getBossOverlay();

        List<String> names = new ArrayList<String>();

        if (demolitionist.bossGUIfield != null) {
            try {
                Field bossField = GuiBossOverlay.class.getDeclaredField(demolitionist.bossGUIfield);
                bossField.setAccessible(true);

                Map<UUID, BossInfoClient> mapBossInfos = (Map<UUID, BossInfoClient>) bossField.get(bossOverlay);

                for (BossInfoClient bIL : mapBossInfos.values())
                    names.add(bIL.getName().getFormattedText());
            } catch (Exception ignored) {}
        }

        return names;
    }
}
