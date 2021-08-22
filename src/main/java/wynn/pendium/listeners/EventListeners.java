package wynn.pendium.listeners;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;
import wynn.pendium.features.FeatureManager;
import wynn.pendium.features.WynnpendiumGuiFeature;
import wynn.pendium.professor.node.NodeFarming;
import wynn.pendium.professor.node.NodeFishing;
import wynn.pendium.professor.node.NodeMining;
import wynn.pendium.professor.node.NodeWoodcutting;
import wynn.pendium.professor.professor;
import wynn.pendium.professor.toolHud.ToolScraper;
import wynn.pendium.professor.xp.ExperienceCalculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;

public class EventListeners {

    private static Pattern node = Pattern.compile("^.(?<CanHarvest>[ac]).+(?<Profession>Farming|Woodcutting|Mining|Fishing) Lv\\. Min: \\u00A7f(?<Level>\\d+)$");

    @SubscribeEvent
    public void eventHandler(final TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END) || !professor.Enabled) return;
        if (!Ref.inGame()) {
            if (Ref.inServer()) ExperienceCalculation.reset();
            return;
        }

        if (!Ref.mc.inGameHasFocus) {
            ExperienceCalculation.scrapeCompass();
            return;
        }

        ExperienceCalculation.checkUpload();
        //professor.updateReplenTime(Ref.getStatAmount("Gathering speed", "%"));

        Matcher match;
        for (Entity entity : Ref.mc.world.loadedEntityList) {
            if (entity instanceof EntityArmorStand && !entity.getTags().contains("professor")) {
                entity.addTag("professor");

                if ((match = node.matcher(entity.getName())).matches()) {

                    // Obtain Node name
                    String name = null;
                    for (Entity e : Ref.mc.world.getLoadedEntityList())
                        if (e instanceof EntityArmorStand && e.getCustomNameTag().matches("^\\u00A7a[a-zA-Z ]+$") && e.posX == entity.posX && e.posZ == entity.posZ && e.posY - entity.posY == 0.2999999523162842d) {
                            name = e.getCustomNameTag().substring(2);
                            break;
                        }
                    if (name == null) continue;

                    // Try and Create node
                    BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
                    switch (match.group("Profession")) {
                        case "Farming":
                            NodeFarming.register(pos, name, Integer.parseInt(match.group("Level")), match.group("CanHarvest").equals("a"));
                            break;
                        case "Woodcutting":
                            NodeWoodcutting.register(pos, name, Integer.parseInt(match.group("Level")), match.group("CanHarvest").equals("a"));
                            break;
                        case "Mining":
                            NodeMining.register(pos, name, Integer.parseInt(match.group("Level")), match.group("CanHarvest").equals("a"));
                            break;
                        case "Fishing":
                            NodeFishing.register(pos, name, Integer.parseInt(match.group("Level")), match.group("CanHarvest").equals("a"));
                            break;
                    }
                    continue;
                }

                ExperienceCalculation.processXP(entity.getCustomNameTag(), entity.getPosition());
            }
        }

        if (professor.Enabled) {

            ToolScraper.scrapeToolsAndMatts();

            if (ToolScraper.Tools[0]) NodeFarming.update();
            if (ToolScraper.Tools[1]) NodeWoodcutting.update();
            if (ToolScraper.Tools[2]) NodeMining.update();
            if (ToolScraper.Tools[3]) NodeFishing.update();
        }
    }

    @SubscribeEvent
    public void eventHandler(final RenderWorldLastEvent event) {
        if (!Ref.inGame() || !professor.Enabled) return;

        GlStateManager.pushMatrix();

        GlStateManager.glLineWidth(1f);
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        if (ToolScraper.Tools[0]) NodeFarming.highlight();
        if (ToolScraper.Tools[1]) NodeWoodcutting.highlight();
        if (ToolScraper.Tools[2]) NodeMining.highlight();
        if (ToolScraper.Tools[3]) NodeFishing.highlight();

        if (Ref.Dev() && Ref.mc.player.isSneaking()) {
            GlStateManager.depthFunc(GL_ALWAYS);
            if (ToolScraper.Tools[0]) NodeFarming.highlightDebug();
            if (ToolScraper.Tools[1]) NodeWoodcutting.highlightDebug();
            if (ToolScraper.Tools[2]) NodeMining.highlightDebug();
            if (ToolScraper.Tools[3]) NodeFishing.highlightDebug();
        }


        GlStateManager.depthFunc(GL_LEQUAL);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void eventHandler(final RenderGameOverlayEvent.Text event) {
        if (professor.Enabled && Ref.inGame()) {

            FeatureManager.enabledFeatures.forEach(WynnpendiumGuiFeature::doRender);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void eventHandler(final ClientChatReceivedEvent event) {
        if (!Ref.inGame() || !professor.Enabled || event.getType() == ChatType.GAME_INFO) return;

        ExperienceCalculation.levelUpDetect(event.getMessage().getUnformattedText());
    }

    @SubscribeEvent
    public void eventHandler(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!Ref.inGame() || !professor.Enabled) return;

        if (event.getGui() instanceof GuiContainer)
            ExperienceCalculation.primeScrapeCompass();
    }
}
