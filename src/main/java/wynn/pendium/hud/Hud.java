package wynn.pendium.hud;

import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class Hud {

    public static boolean Enabled = false;

    static MessageDisplay main = new MessageDisplay(0.5f, 3/5f, 1, 0, 0, "center");
    static MessageDisplay announcement = new MessageDisplay(0.5f, 1/5f, 1, 0, 0, "center");
    static MessageDisplay console = new MessageDisplay(0.0f, 0.5f, 0.5f, 10, 0, "left");

    public static void Init() {
        MinecraftForge.EVENT_BUS.register(new HudEvents());
    }

    public static void Enable() {
        Enabled = true;
    }

    public static void Disable() {
        Enabled = false;
        main.clearAll();
        announcement.clearAll();
        console.clearAll();
    }


    public static void addMessages(List<String> list) {
        if (!Enabled) return;

        for (String msg : list) {
            switch(msg.charAt(0)) {
                case 'a': main.addMessage(new Message(msg.substring(2))); break;
                case 'b': announcement.addMessage(new Message(msg.substring(2))); break;
                case 'z':
                default: console.addMessage(new Message(msg.substring(2))); break;
            }
        }
    }

    public static void consoleOut(String msg) {
        if (Enabled)
            console.addMessage(new Message("0xff55ff$" + msg.replace("$", "")));
    }
}
