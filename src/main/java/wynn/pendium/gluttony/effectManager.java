package wynn.pendium.gluttony;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import wynn.pendium.Ref;

import java.util.ArrayList;
import java.util.List;

public class effectManager {

    // Selected Consumable
    static Consumable selected = null;
    static ItemStack handMem = ItemStack.EMPTY;

    // List of active consumables
    static List<Consumable> noms = new ArrayList<>();

    // Cached Display
    static List<DisplayEffect> effectCache = new ArrayList<>();



    static void wipe() {
        noms.clear();
        effectCache.clear();
        gluttony.updateStats();
    }

    static void updateHand(ItemStack hand) {
        if (!(hand.getItem() == Items.AIR && selected != null) && ItemStack.areItemStacksEqual(handMem, hand))
            return;
        handMem = hand;

        if (hand.isEmpty() || hand.getItem() == Items.AIR || !Consumable.isConsumable(hand))
            selected = null;
        else
            selected = new Consumable(hand);

        updateCache();
    }

    static void updateTick() {
        for (int i = 0; i < noms.size(); i++) {
            if (!noms.get(i).applied) {
                for (Effect effect : noms.get(i).getEffects())
                    if (effect.found) {
                        noms.get(i).consume();
                        updateCache();
                        break;
                    }
            }
            if (noms.get(i).hasElapsed()) {
                noms.remove(i);
                updateCache();
                i--;
            }
        }
        for (DisplayEffect effect : effectCache) {
            effect.updateTime();
        }
    }

    static void processEffect(String stat, String suffix, int value, int duration) {
        if (stat.equals("XP"))
            stat = "XP Bonus";

        for (Consumable nom : noms)
            if (!nom.applied && nom.getDuration() == duration)
                for (Effect effect : nom.getEffects())
                    if (effect.name.equals(stat) && effect.suffix.equals(suffix)&& effect.value == value) {
                        effect.found = true;
                        if (nom.getType() == Consumable.Type.DEFAULT)
                            for (Effect effect2 : nom.getEffects())
                                if (!effect2.found)
                                    effect2.found = true;
                        return;
                    }

        // Manually add all other effects to system, and assume they are scroll effects from other players
        Consumable other = new Consumable(null);
        other.setDuration(duration);
        // Add effect and apply it to manual consumable
        Effect effect = new Effect(stat, value, suffix);
        effect.found = true;
        other.getEffects().add(effect);
        noms.add(other);
    }

    static void processHeal(int value) {
        for (Consumable nom : noms)
            if (!nom.applied)
                for (Effect effect : nom.getEffects()) {
                    if (effect.name.equals("Heal") && effect.value == value) {
                        effect.found = true;
                        return;
                    }
                }
    }

    static void addConsumable(Consumable newNom) {
        for (Consumable nom : noms)
            if (nom.getType() == newNom.getType())
                for (Effect effect : nom.getEffects())
                    for (int i = 0; i < newNom.getEffects().size(); i++)
                        if (effect.name.equals(newNom.getEffects().get(i).name) && effect.suffix.equals(newNom.getEffects().get(i).suffix)) {
                            newNom.getEffects().remove(i);
                            i--;
                        }
        if (!newNom.getEffects().isEmpty())
            noms.add(newNom);
    }

    static int getStat(String name, String suffix) {
        int result = 0;
        for (Consumable nom : noms)
            if (nom.applied)
                for (Effect effect : nom.getEffects())
                    if (effect.name.equals(name) && effect.suffix.equals(suffix))
                        result += effect.value;
        return result;
    }


    // Updates the DisplayEffect cache
    static void updateCache() {
        effectCache.clear();
        gluttony.updateStats();

        for (Consumable nom : noms)
            if (nom.applied)
                for (Effect effect : nom.getEffects()) {
                    boolean found = false;
                    for (DisplayEffect display : effectCache) {
                        if (effect.name.equals(display.name) && effect.suffix.equals(display.suffix)) {
                            display.value += effect.value;
                            display.updateCache();
                            switch (nom.getType()) {
                                case POTION: display.consumables[0] = true; break;
                                case SCROLL: display.consumables[1] = true; break;
                                case FOOD: display.consumables[2] = true; break;
                                case DEFAULT: display.consumables[3] = true; display.setMiscIcon(nom.getIcon()); break;
                            }
                            if (display.getDurationEnd() > nom.getDurationEnd())
                                display.setDurationEnd(nom.getDurationEnd());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        DisplayEffect temp = new DisplayEffect(effect.name, effect.value, effect.suffix, nom.getDurationEnd());
                        switch (nom.getType()) {
                            case POTION: temp.consumables[0] = true; break;
                            case SCROLL: temp.consumables[1] = true; break;
                            case FOOD: temp.consumables[2] = true; break;
                            case DEFAULT: temp.consumables[3] = true; temp.setMiscIcon(nom.getIcon()); break;
                        }
                        temp.updateCache();
                        effectCache.add(temp);
                    }
                }

        // Effect reordering
        for (int i = 0; i < effectCache.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (Ref.getStatHierarchy(effectCache.get(i).name, effectCache.get(i).suffix) < Ref.getStatHierarchy(effectCache.get(j).name, effectCache.get(j).suffix)) {
                    effectCache.add(j, effectCache.get(i));
                    effectCache.remove(i+1);
                    break;
                }
            }
        }



        if (selected == null)
            return;

        // Overlay changes to effects based on selected item
        for (Effect effect : selected.getEffects()) {
            boolean found = false;
            for (DisplayEffect display : effectCache) {
                if (effect.name.equals(display.name) && effect.suffix.equals(display.suffix)) {
                    switch (selected.getType()) {
                        case POTION: if (!display.consumables[0]) display.setChange(effect.value); break;
                        case SCROLL: if (!display.consumables[1]) display.setChange(effect.value); break;
                        case FOOD: if (!display.consumables[2]) display.setChange(effect.value); break;
                        case DEFAULT: if (!display.consumables[3]) display.setChange(effect.value); break;
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                DisplayEffect temp = new DisplayEffect(effect.name, 0, effect.suffix, 0);
                temp.setChange(effect.value);
                temp.updateCache();
                effectCache.add(temp);
            }
        }
    }

}
