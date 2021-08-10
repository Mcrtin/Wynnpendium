package wynn.pendium.looter;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import wynn.pendium.Ref;

public class events {

    private static BlockPos lastClickedChest = new BlockPos(0,64,0);
    private static long lastClickedTime = 0L;

    @SubscribeEvent
    public void eventHandler(final InputEvent.MouseInputEvent e) {
        if (!Ref.inGame() || !looter.Enabled || !Ref.mc.inGameHasFocus || !(Mouse.getEventButton() == 1 && Mouse.getEventButtonState())) return;

        RayTraceResult hover = Ref.mc.objectMouseOver;
        if (hover == null ||  hover.typeOfHit == null || !hover.typeOfHit.equals(RayTraceResult.Type.BLOCK)) return;
        if (Ref.mc.world.getBlockState(hover.getBlockPos()).getBlock().equals(Blocks.CHEST)) {
            lastClickedChest = hover.getBlockPos();
            lastClickedTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void eventHandler(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!Ref.inGame() || !looter.Enabled) return;

        if (event.getGui() instanceof GuiContainer && lastClickedTime > System.currentTimeMillis() - 1000)
            scraper.prime(lastClickedChest, Ref.getStatAmount("Loot Bonus", "%"), Ref.getStatAmount("Loot Quality", "%"));
    }

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END) || !Ref.inGame() || !looter.Enabled) return;

        if (!Ref.mc.inGameHasFocus)
            scraper.scrape();
    }
}
