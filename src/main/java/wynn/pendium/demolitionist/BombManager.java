package wynn.pendium.demolitionist;

import wynn.pendium.Ref;
import wynn.pendium.professor.professor;

import java.util.ArrayList;
import java.util.List;

public class BombManager {

    private static List<Bomb> bombs = new ArrayList<>();

    static void wipe() {
        bombs.clear();
    }

    static void registerBomb(Bomb.Type type, String thrower, int duration) {
        for (Bomb bomb : bombs)
            if (bomb.type == type) {
                bomb.updateTime(duration);
                return;
            }

        bombs.add(new Bomb(type, thrower, duration));
        if (type == Bomb.Type.PSPEED)
            professor.setSpeedBomb(true);
    }

    static void tickBombs() {
        for (int i = 0; i < bombs.size(); i++)
            if (bombs.get(i).worldStampEnd < Ref.mc.world.getTotalWorldTime()) {
                if (bombs.get(i).type == Bomb.Type.PSPEED)
                    professor.setSpeedBomb(false);
                bombs.remove(i);
                i--;
            }
    }
}
