package wynn.pendium.demolitionist;

import wynn.pendium.Ref;

public class Bomb {

    String thrower;
    long worldStampEnd;
    Type type;

    enum Type {
        DXP, DLOOT, PXP, PSPEED, DBOMB, SOUL, ITEM, INGREDIENT, DISGUISE, PARTY, UNKNOWN
    }

    Bomb(Type type, String thrower, int duration) {
        this.type = type;
        this.thrower = thrower;
        this.worldStampEnd = Ref.mc.world.getTotalWorldTime() + duration*1200;
    }

    void updateTime(int Duration) {
        if (this.worldStampEnd < Ref.mc.world.getTotalWorldTime() + Duration*1200)
            this.worldStampEnd = Ref.mc.world.getTotalWorldTime() + Duration*1200;
    }
}
