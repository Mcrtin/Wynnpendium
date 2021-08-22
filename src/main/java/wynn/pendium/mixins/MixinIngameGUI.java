package wynn.pendium.mixins;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wynn.pendium.Ref;
import wynn.pendium.events.TitleReceivedEvent;

import java.lang.annotation.Target;

@Mixin(GuiIngame.class)
public abstract class MixinIngameGUI {

    @Shadow
    protected String displayedTitle;

    @Shadow
    protected String displayedSubTitle;
    @Shadow
    protected int titlesTimer;
    @Shadow
    protected int titleFadeIn;
    @Shadow
    protected int titleFadeOut;
    @Shadow
    protected int titleDisplayTime;


    /**
     * @author
     * @reason to add a custom event listener.
     */
    @Overwrite
    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut)
    {

        TitleReceivedEvent event = new TitleReceivedEvent(title, subTitle, timeFadeIn, displayTime, timeFadeOut);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        title       = event.getTitleText();
        subTitle    = event.getSubTitleText();
        timeFadeIn  = event.getTimeFadeIn();
        timeFadeOut = event.getTimeFadeOut();
        displayTime = event.getDisplayTime();
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0)
        {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        }
        else if (title != null)
        {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        }
        else if (subTitle != null)
        {
            this.displayedSubTitle = subTitle;
        }
        else
        {
            if (timeFadeIn >= 0)
            {
                this.titleFadeIn = timeFadeIn;
            }

            if (displayTime >= 0)
            {
                this.titleDisplayTime = displayTime;
            }

            if (timeFadeOut >= 0)
            {
                this.titleFadeOut = timeFadeOut;
            }

            if (this.titlesTimer > 0)
            {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }


}
