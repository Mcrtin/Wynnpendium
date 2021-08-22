package wynn.pendium.features;

import net.minecraftforge.common.MinecraftForge;

public abstract class Feature {

    private String  name;
    /**
     * Will be set through a config thing.
     */
    private boolean enabled = true;


    protected Feature(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
