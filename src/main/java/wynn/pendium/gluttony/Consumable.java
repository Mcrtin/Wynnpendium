package wynn.pendium.gluttony;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import wynn.pendium.Ref;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumable {

    private static final Pattern CONSUMABLE_CRAFT = Pattern.compile("^(?:\\n|.)*\\nCrafted (Food|Scroll|Potion|by [a-zA-Z_0-9]{1,16})(?:\\n(?:\\n|.)*)?$");
    private static final Pattern CONSUMABLE_POTION = Pattern.compile("^Potion of (?:. )?(?<Name>[a-zA-Z0-9 ]+)(?: \\[\\d+/\\d+])?$");
    private static final Pattern DURATION = Pattern.compile("\\n- Duration: (?<Duration>\\d+) Seconds\\n");
    private static final Pattern HEALTH_SCAN = Pattern.compile("\\n- Heal: (?<Heals>\\d+) (?<Suffix>.)\\n");
    private static final Pattern MANA_SCAN = Pattern.compile("\\n- Mana: (?<Mana>\\d+) (?<Suffix>.)\\n");
    private static final Pattern EFFECT_SCAN = Pattern.compile("\\n- Effect: \\+?(?<Value>-?\\d+)(?<Suffix>%|/4s|/3s| tier)? (?:. )?(?<Name>[A-Za-z 0-9\\-]+)");
    private static final Pattern MISC_SCAN = Pattern.compile("\\n- Effect: (?<Name>[A-Za-z \\-]+)");
    private static final Pattern STAT_SCAN = Pattern.compile("\\n\\+?(?<Value>-?\\d+)(?<Suffix>%|/4s|/3s| tier)? (?<Name>[A-Za-z 0-9\\-]+)(?: .+?)?");

    private String name = null;
    private List<Effect> effects = new ArrayList<>();
    private Type type = null;
    private int duration = 0;
    private long stamp = 0;
    boolean applied = false;

    private ItemStack icon; // Used for HUD for Default type

    enum Type {
        FOOD,POTION,SCROLL,DEFAULT
    }

    Consumable(ItemStack item) {
        if (item == null || item.isEmpty()) {
            this.name = "Party Scroll";
            this.type = Type.SCROLL;
            this.duration = 0;
            //this.stamp = Ref.mc.world.getTotalWorldTime(); // world time
            this.stamp = System.currentTimeMillis(); // real time
            return;
        }

        // Set Type
        if (item.getItem() == Items.POTIONITEM || item.getItem() == Items.SPLASH_POTION)
            if (CONSUMABLE_CRAFT.matcher(Ref.getLore(item)).matches())
                this.type = Type.POTION;
            else
                this.type = Type.DEFAULT;
        else if (item.getItem() == Items.DIAMOND_AXE){
            if (item.getItemDamage() >= 69 && item.getItemDamage() < 76)
                this.type = Type.FOOD;
            else if (item.getItemDamage() >= 42 && item.getItemDamage() < 45)
                this.type = Type.SCROLL;
            else
                return;
        } else
            return;


        // Obviously set name
        this.name = item.getDisplayName();
        this.icon = item;
        //this.stamp = Ref.mc.world.getTotalWorldTime(); // world time
        this.stamp = System.currentTimeMillis(); // real time


        StringBuffer lore = Ref.getLore(item);
        Matcher scan;

        if (!(scan = DURATION.matcher(lore)).find()) return;
        this.duration = Integer.parseInt(scan.group("Duration"));

        if ((scan = HEALTH_SCAN.matcher(lore)).find())
            this.effects.add(new Effect("Heal", Integer.parseInt(scan.group("Heals")), scan.group("Suffix")));

        if ((scan = MANA_SCAN.matcher(lore)).find())
            this.effects.add(new Effect("Mana", Integer.parseInt(scan.group("Mana")), scan.group("Suffix")));

        scan = EFFECT_SCAN.matcher(lore);
        while (scan.find()) {
            this.effects.add(new Effect((scan.group("Name").equals("XP") ? "XP Bonus" : scan.group("Name")), Integer.parseInt(scan.group("Value")), (scan.group("Suffix") == null ? "" : scan.group("Suffix"))));
        }

        scan = MISC_SCAN.matcher(lore);
        while (scan.find()) {
            this.effects.add(new Effect(scan.group("Name"), 0, ""));
        }

        scan = STAT_SCAN.matcher(lore);
        while (scan.find()) {
            this.effects.add(new Effect(scan.group("Name"), Integer.parseInt(scan.group("Value")), (scan.group("Suffix") == null ? "" : scan.group("Suffix"))));
        }
    }

    static boolean isConsumable(ItemStack test) {
        StringBuffer lore = Ref.getLore(test);
        return CONSUMABLE_CRAFT.matcher(lore).matches() || CONSUMABLE_POTION.matcher(Ref.noColour(test.getDisplayName())).matches();
    }

    void consume() {
        this.applied = true;
        //this.stamp = Ref.mc.world.getTotalWorldTime() - 1; // world time
        this.stamp = System.currentTimeMillis() - 50; // real time

        for (int i = 0; i < this.effects.size(); i++)
            if (!this.effects.get(i).found) {
                this.effects.remove(i);
                i--;
            }
    }

    Type getType() {
        return this.type;
    }

    List<Effect> getEffects() {
        return this.effects;
    }

    boolean hasElapsed() {
        if (this.applied)
            //return this.stamp + (this.duration * 20) <= Ref.mc.world.getTotalWorldTime(); // world time
            return this.stamp + (this.duration * 1000) <= System.currentTimeMillis(); // real time0
        //return this.stamp + 30 <= Ref.mc.world.getTotalWorldTime(); // world time
        return this.stamp + 1500 <= System.currentTimeMillis(); // real time
    }

    void setDuration(int duration) {
        this.duration = duration;
    }

    int getDuration() {
        return this.duration;
    }

    long getDurationEnd() {
        //return this.stamp + (this.duration * 20); // world time
        return this.stamp + (this.duration * 1000); // real time
    }

    ItemStack getIcon() {
        return this.icon;
    }
}
