package wynn.pendium.features;

import wynn.pendium.hud.components.features.ComponentToolDurability;
import wynn.pendium.hud.components.features.HudExperienceBar;
import wynn.pendium.hud.components.features.WynnpendiumGuiFeature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FeatureManager {


    public static ComponentToolDurability i_componentToolDurability;
    public static HudExperienceBar i_hudExperienceBar;

    public static List<WynnpendiumGuiFeature> enabledFeatures;


    public static void addFeature(WynnpendiumGuiFeature feature) {
        enabledFeatures.add(feature);

    }

    static {
        enabledFeatures = new LinkedList<>();
        i_componentToolDurability = new ComponentToolDurability();
        i_hudExperienceBar = new HudExperienceBar();
        Collections.addAll(enabledFeatures, i_componentToolDurability, i_hudExperienceBar);
    }

}
