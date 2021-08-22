package wynn.pendium.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class TitleReceivedEvent extends Event {

    private String titleText = "", subTitleText = "";
    private int timeFadeIn, displayTime, timeFadeOut = 0;


    public TitleReceivedEvent(String titleText, String subTitleText, int timeFadeIn, int displayTime, int timeFadeOut) {
        this.titleText = titleText;
        this.setSubTitleText(subTitleText);
        this.timeFadeIn = timeFadeIn;
        this.displayTime = displayTime;
        this.timeFadeOut = timeFadeOut;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getSubTitleText() {
        return subTitleText;
    }

    public int getTimeFadeIn() {
        return timeFadeIn;
    }

    public int getDisplayTime() {
        return displayTime;
    }

    public int getTimeFadeOut() {
        return timeFadeOut;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public void setSubTitleText(String subTitleText) {
        this.subTitleText = subTitleText;
    }

    public void setTimeFadeIn(int timeFadeIn) {
        this.timeFadeIn = timeFadeIn;
    }

    public void setDisplayTime(int displayTime) {
        this.displayTime = displayTime;
    }

    public void setTimeFadeOut(int timeFadeOut) {
        this.timeFadeOut = timeFadeOut;
    }
}
