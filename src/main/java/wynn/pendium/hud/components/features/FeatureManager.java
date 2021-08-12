package wynn.pendium.hud.components.features;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FeatureManager {

    public static List<WynnpendiumGuiFeature> enabledFeatures;


    public static void addFeature(WynnpendiumGuiFeature feature) {
        enabledFeatures.add(feature);

    }

    static {
        enabledFeatures = new LinkedList<>();
        Collections.addAll(enabledFeatures, new HudExperienceBar(), new ComponentToolDurability());
    }

}
