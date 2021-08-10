package wynn.pendium.gluttony;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import wynn.pendium.Ref;
import wynn.pendium.professor.professor;

public class gluttony {

    static boolean Enabled = false;

    static KeyBinding toggle;

    public static void Init() {
        MinecraftForge.EVENT_BUS.register(new events());

        ClientRegistry.registerKeyBinding(toggle = new KeyBinding("key.wynnpendium.toggle_effect_hud", Keyboard.KEY_L, "key.wynnpendium"));
    }

    public static void Enable() {
        Enabled = true;
    }

    public static void Disable() {
        effectManager.wipe();
        Enabled = false;
    }

    public static void updateStats() {
        professor.updateReplenTime(Ref.getStatAmount("Gather Speed", "%"));
    }

    public static int getStat(String name, String suffix) {
        return effectManager.getStat(name, suffix);
    }
}
