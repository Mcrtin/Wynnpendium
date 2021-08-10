package wynn.pendium.looter;

import net.minecraftforge.common.MinecraftForge;

public class looter {

    static boolean Enabled = false;

    public static void Init() {
        MinecraftForge.EVENT_BUS.register(new events());
    }

    public static void Enable() {
        Enabled = true;
    }

    public static void Disable() {
        Enabled = false;
    }
}
