package wynn.pendium.professor.toolHud;

import wynn.pendium.utils.ColorCode;

public enum PercentColors {

    PERCENT_100(100, ColorCode.GREEN),
    PERCENT_75(75, ColorCode.DARK_GREEN),
    PERCENT_65(65, ColorCode.GOLD),
    PERCENT_50(50, ColorCode.YELLOW),
    PERCENT_30(30, ColorCode.RED),
    PERCENT_5(5, ColorCode.DARK_RED);


    public ColorCode color;
    public double    percent;

    PercentColors(double percent, ColorCode color) {

        this.color = color;
        this.percent = percent;


    }

    public static PercentColors valueOf(double percent) {
        for (PercentColors color: values()) {
            boolean triggered = color.percent <= percent;
            //            once color.percent >= percent, end this loop.
            if (!triggered)
                return color;
        }
        return PERCENT_5;

    }
    public static PercentColors valueOf(int percent) {
        for (PercentColors color: values()) {
            boolean triggered = color.percent <= percent;
            //            once color.percent >= percent, end this loop.
            if (!triggered)
                return color;
        }
        return PERCENT_5;

    }








}
