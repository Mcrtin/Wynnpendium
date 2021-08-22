package wynn.pendium.features.dungeon;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class PlayerEnterDungeonEvent extends Event  {

    private String dungeonName;
    private boolean corrupt;

    public PlayerEnterDungeonEvent(String dungeonName, boolean corrupt) {
        this.dungeonName = dungeonName;
        this.corrupt = corrupt;
    }


    @Override
    public boolean isCancelable() {
       return false;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public boolean isCorrupt() {
        return corrupt;
    }
}
