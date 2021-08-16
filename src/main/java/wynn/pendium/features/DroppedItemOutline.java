package wynn.pendium.features;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import scala.collection.mutable.HashTable;
import wynn.pendium.Ref;

import java.util.*;
import java.util.function.Function;

public class DroppedItemOutline extends Feature {




    private ScorePlayerTeam team;

    public DroppedItemOutline() {
        super("highlight_item_drops");


    }



    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END)
            return;
        if (Ref.mc.world == null)
            return;

        for (EntityItem ent: Ref.mc.world.getEntities(EntityItem.class, o -> true)) {
            ent.setGlowing(true);
        }
    }



}
