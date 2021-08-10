package wynn.pendium.demolitionist;

import wynn.pendium.Ref;

public class Bomb {

    String thrower;
    long worldStampEnd;
    Type type;

    enum Type {
        DXP, DLOOT, PXP, PSPEED, DBOMB, SOUL, ITEM, INGREDIENT, DISGUISE, PARTY, UNKNOWN
    }

    Bomb(Type Type, String Thrower, int Duration) {
        this.type = Type;
        this.thrower = Thrower;
        this.worldStampEnd = Ref.mc.world.getTotalWorldTime() + Duration*1200;
    }

    void updateTime(int Duration) {
        if (this.worldStampEnd < Ref.mc.world.getTotalWorldTime() + Duration*1200)
            this.worldStampEnd = Ref.mc.world.getTotalWorldTime() + Duration*1200;
    }
}
