package wynn.pendium.utils;

import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import wynn.pendium.Ref;
import wynn.pendium.features.dungeon.DungeonTokenDisplay;

import java.util.regex.Pattern;

public class DungeonUtils {

    public static final Pattern DUNGEON_COMPLETION_PATTERN = Pattern.compile(Ref.COLOR_START + "8Great job! You've completed the .+ ?.+? Dungeon!");

    public static boolean isTokenSign(ITextComponent component) {
        String text;
        if ((text = component.getFormattedText()).equals(""))
            return false;
        if (!text.startsWith(Ref.COLOR_START + "7Get "))
            return false;
        if (!text.contains(Ref.COLOR_START + "e["))
            return false;
        String noColor = Ref.noColour(text);
        return noColor.matches("Get \\[\\d+ Tokens]");
    }

    /**
     *
     * @param component The display name of this EntityArmorStand
     * @return true IFF it matches the regex \d+/\d+
     */
    public static boolean isSimpleTokenSign(ITextComponent component) {
        return Ref.noColour(component.getFormattedText()).matches("\\d+/\\d+");
    }

//    this is for i.e x/y where x is the amount had, y is the amount needed.
    public static int getAmountNeededFromSign(ITextComponent component) {

        if (!isSimpleTokenSign(component))
            return -1;
        return Integer.parseInt(Ref.noColour(component.getFormattedText()).split("/")[1]);

    }

    /**
     *
     * @param component The display name of this EntityArmorStand
     * @return The parsed text on the left side of the /
     */
    public static int getAmountContainedFromSign(ITextComponent component) {
        if (!isSimpleTokenSign(component))
            return -1;
        return Integer.parseInt(Ref.noColour(component.getFormattedText()).split("/")[0]);
    }

    /**
     *
     * @param component The display name of this EntityArmorStand
     * @return {getAmountNeededFromSign} - {getAmountContainedFromSign}
     */
    public static int getAmountLeftFromSign(ITextComponent component) {
        if (!isSimpleTokenSign(component))
            return -1;
        return getAmountNeededFromSign(component) - getAmountContainedFromSign(component);
    }
    public static boolean isToken(ItemStack item) {
        if (!(item.getItem() instanceof ItemSkull))
            return false;
        if (!item.hasDisplayName())
            return false;

        return item.getDisplayName().contains("Token");
    }
    public static boolean setPlayerDungeonMetadata(DungeonTokenDisplay.DungeonTitleDisplayValues dung) {
        if (Ref.mc.player.getEntityData().hasKey("activeDungeon"))
        {
            return false;
        }
        Ref.mc.player.getEntityData().setString("activeDungeon", dung.name());
        return true;
    }
    public static boolean removeDungeonPlayerMetadata() {

        if (!Ref.mc.player.getEntityData().hasKey("activeDungeon"))
            return false;

        Ref.mc.player.getEntityData().removeTag("activeDungeon");
        return true;
    }


    public static boolean isPlayerInDungeon() {
        return Ref.mc.player.getEntityData().hasKey("activeDungeon");
    }
}
