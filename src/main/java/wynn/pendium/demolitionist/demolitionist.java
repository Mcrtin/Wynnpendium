package wynn.pendium.demolitionist;

import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;

public class demolitionist {

    static boolean Enabled = false;

    static String bossGUIfield = null;

    public static void Init() {
        MinecraftForge.EVENT_BUS.register(new events());
    }

    public static void Enable() {
        Enabled = true;

        bossGUIfield = null;
        for (Field s : GuiBossOverlay.class.getDeclaredFields())
            if (s.getType().getName().equals("java.util.Map")) {
                bossGUIfield = s.getName();
                break;
            }
    }

    public static void Disable() {
        Enabled = false;
        bombManager.wipe();
    }
}
