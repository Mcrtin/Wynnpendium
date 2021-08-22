package wynn.pendium.features;

import wynn.pendium.features.dungeon.DungeonTokenDisplay;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FeatureManager {


    public static ComponentToolDurability i_componentToolDurability;
    public static HudExperienceBar        i_hudExperienceBar;
    public static DungeonTokenDisplay i_dungeonTokenDisplay;

    public static List<WynnpendiumGuiFeature> enabledFeatures;


    public static void addFeature(WynnpendiumGuiFeature feature) {
        enabledFeatures.add(feature);

    }


    public static void init() {

    }




    static {
        enabledFeatures = new LinkedList<>();
        i_componentToolDurability = new ComponentToolDurability();
        i_hudExperienceBar = new HudExperienceBar();
        i_dungeonTokenDisplay = new DungeonTokenDisplay();
        Collections.addAll(enabledFeatures, i_componentToolDurability, i_hudExperienceBar, i_dungeonTokenDisplay);
    }

}
