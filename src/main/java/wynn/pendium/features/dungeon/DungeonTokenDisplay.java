package wynn.pendium.features.dungeon;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import wynn.pendium.Ref;
import wynn.pendium.events.TitleReceivedEvent;
import wynn.pendium.features.WynnpendiumGuiFeature;
import wynn.pendium.utils.ColorCode;
import wynn.pendium.utils.DungeonUtils;
import wynn.pendium.utils.ItemUtils;

public class DungeonTokenDisplay extends WynnpendiumGuiFeature {


    private DungeonTitleDisplayValues activeDungeon;

    private int currentHeight = 64, currentWidth = 64;

    private FontRenderer fontRenderer;


    private int cachedTokensNeeded = 0, cachedTokensHeld = 0;
    private int cachedSignTokens;


    public DungeonTokenDisplay() {
        super(940, 200, "Display Dungeon Token Amount");
        this.fontRenderer = Ref.mc.fontRenderer;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void renderDummy(int x, int y, Minecraft mc) {

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableBlend();
        fontRenderer.drawString(ChatFormatting.YELLOW +
                "Tokens"
                + ChatFormatting.GRAY + ": "
                + ChatFormatting.GREEN + 6
                + ChatFormatting.GRAY + " / "
                + ChatFormatting.YELLOW + 26, x - fontRenderer.getStringWidth("Tokens: " + 6 + "(+" + 3 + ") / " + 26) / 2, y - fontRenderer.FONT_HEIGHT, 0);

        Ref.mc.getRenderItem().renderItemIntoGUI(ItemUtils.getDungeonToken(), x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();

    }

    @Override
    public void doRender(int x, int y, Minecraft mc) {

        if (Ref.mc.world == null)
            return;
        if (this.cachedTokensNeeded == 0)
            return;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableBlend();

        fontRenderer.drawString(ChatFormatting.YELLOW +
                "Tokens"
                + ChatFormatting.GRAY + ": "
                + ChatFormatting.GREEN + (this.cachedSignTokens + this.cachedTokensHeld) + ChatFormatting.GRAY
                + ChatFormatting.GRAY + " / "
                + ChatFormatting.YELLOW + this.cachedTokensNeeded, x - fontRenderer.getStringWidth("Tokens: " + (cachedSignTokens + this.cachedTokensHeld) +  " / " + this.cachedTokensNeeded) / 2, y - fontRenderer.FONT_HEIGHT, 0);

        Ref.mc.getRenderItem().renderItemIntoGUI(ItemUtils.getDungeonToken(), x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
    }

    @Override
    public int getWidth() {
        return currentWidth;
    }

    @Override
    public int getHeight() {
        return currentHeight;
    }

    /**
     * Check for any dungeon token armor stands.
     */
    @SubscribeEvent
    public void onRender(RenderLivingEvent.Post<EntityArmorStand> event) {
        if (!(event.getEntity() instanceof EntityArmorStand))
            return;

        EntityArmorStand entity = (EntityArmorStand) event.getEntity();
        ITextComponent displayName;
        if ((displayName = entity.getDisplayName()).getFormattedText().equals("Armor Stand"))
            return;

        if (DungeonUtils.getAmountNeededFromSign(displayName) != -1) {
            this.cachedTokensNeeded = DungeonUtils.getAmountNeededFromSign(displayName);
            this.cachedSignTokens   = DungeonUtils.getAmountContainedFromSign(displayName);
        }


    }
    @SubscribeEvent
    public void updateTokenCount(TickEvent.ClientTickEvent e) {
        boolean flag = false;
        for (ItemStack item : Ref.mc.player.inventoryContainer.getInventory()) {
            if (item.hasDisplayName()) {
                if (item.getDisplayName().contains("Token")) {
                    flag = true;
                    this.cachedTokensHeld = item.getCount();
                }
            }
        }
        if (!flag)
            this.cachedTokensHeld = 0;
    }


    @SubscribeEvent
    public void onRecieve(TitleReceivedEvent e) {

        DungeonTitleDisplayValues dungeon;
        if (e.getTitleText() == null)
            return;

        if ((dungeon = DungeonTitleDisplayValues.fromTitle(Ref.noColour(e.getTitleText()))) != null)
            DungeonUtils.setPlayerDungeonMetadata(this.activeDungeon = dungeon);
    }

    @SubscribeEvent
    public void dungeonExitEvent(ClientChatReceivedEvent e) {
        if (DungeonUtils.DUNGEON_COMPLETION_PATTERN.matcher(e.getMessage().getFormattedText()).matches()) {
            DungeonUtils.removeDungeonPlayerMetadata();
        }
        if (DungeonUtils.isPlayerInDungeon() && e.getMessage().getFormattedText().contains("You have died..."))
            DungeonUtils.removeDungeonPlayerMetadata();  // hah u lose loser
    }

    @SubscribeEvent
    public void updateTokens(PlayerEvent.ItemPickupEvent e) {
        if (DungeonUtils.isToken(e.pickedUp.getItem())) {
            this.cachedTokensHeld += e.pickedUp.getItem().getCount();
        }

    }

    public enum DungeonTitleDisplayValues {

        DECREPIT_SEWERS("Decrepit Sewers"),
        INFESTED_PIT("Infested Pit"),
        LOST_SANCTUARY("Lost Sanctuary"),
        UNDERWORLD_CRYPT("Underworld Crypt"),
        SAND_SWEPT_TOMB("Sand-Swept Tomb"),
        ICE_BARROWS("Ice Barrows"),
        UNDERGROWTH_RUINS("Undergrowth Ruins"),
        GALLEONS_GRAVEYARD("Galleon's Graveyard"),
        FALLEN_FACTORY("Fallen Factory"),
        ELDRITCH_OUTLOOK("Eldritch Outlook"),
        ;
        private final String key;

        DungeonTitleDisplayValues(String key) {

            this.key = key;

        }

        public String getKey() {
            return key;
        }

        public static DungeonTitleDisplayValues fromTitle(String titleText) {


            for (DungeonTitleDisplayValues val : values()) {
                if (val.key.equals(titleText) || ("Corrupted " + val.key).equals(titleText))
                    return val;
            }
            return null;

        }

    }

}
