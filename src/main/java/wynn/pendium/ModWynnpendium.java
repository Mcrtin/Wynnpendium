package wynn.pendium;

import net.minecraft.command.CommandHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import wynn.pendium.commands.WynnpendiumCommand;
import wynn.pendium.demolitionist.BombCore;
import wynn.pendium.features.DroppedItemOutline;
import wynn.pendium.gluttony.gluttony;
import wynn.pendium.hud.Hud;
import wynn.pendium.looter.looter;
import wynn.pendium.professor.professor;

@Mod(modid = ModWynnpendium.MODID, name = ModWynnpendium.NAME, version = ModWynnpendium.VERSION, clientSideOnly = true)
public class ModWynnpendium
{
    static final String MODID = "wynnpendium";
    static final String NAME = "WynnPendium";
    static final String VERSION = "0.4.2";

    private static boolean onJoin = false;
    private static boolean outdated = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Ref.isDev = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        MinecraftForge.EVENT_BUS.register(this);
        Hud.Init();
        professor.Init();
        looter.Init();
        gluttony.Init();
        BombCore.Init();
        MinecraftForge.EVENT_BUS.register(new DroppedItemOutline());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientCommandHandler.instance.registerCommand(new WynnpendiumCommand());

        //ClientRegistry.registerKeyBinding(toggle = new KeyBinding("key.wynnpendium.toggle_key", Keyboard.KEY_L, "key.wynnpendium"));
    }

    @SubscribeEvent
    public void eventHandler(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        onJoin = true;
    }

    @SubscribeEvent
    public void eventHandler(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        disable();
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent e) {

        if (e.phase.equals(TickEvent.Phase.END)) return;

        Ref.setLobby(); // Just needs to get called to update the status

        if (Ref.setInGame() && onJoin) {
            enable();
            WebManager.sendRequest(new String[][] {{"action", "load_necessities"}});
            onJoin = false;

            Ref.mc.playerController.processRightClick(Ref.mc.player,Ref.mc.world, EnumHand.MAIN_HAND);
        }
    }

    private static void enable() {
        professor.Enable();
        Hud.Enable();
        looter.Enable();
        gluttony.Enable();
        BombCore.Enable();
    }

    private static void disable() {
        professor.Disable();
        Hud.Disable();
        looter.Disable();
        gluttony.Disable();
        BombCore.Disable();
    }


    static void setOutdated() {
        outdated = true;
        disable();
    }

    static boolean getOutdated() { return outdated; }

    @SubscribeEvent
    public void eventHandler(final RenderGameOverlayEvent.Text event) {
        if (Ref.inGame() && outdated) {
            Ref.mc.fontRenderer.drawString("This is an outdated version of WynnPendium", 10, 10, 0xFFFFAA00, true);
            Ref.mc.fontRenderer.drawString("Please consider updating", 10, 22, 0xFFFFAA00, true);
        }
    }
}
