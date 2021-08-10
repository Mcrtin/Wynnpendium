package wynn.pendium;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class WynnItem {

    Pattern ITEM_NAME = Pattern.compile("^\\u00A7[6cbdeaf](?<Name>[A-Za-z0-9\\-_']+)$");
    Pattern CRAFT_NAME = Pattern.compile("^\\u00A73(?<Name>[A-Za-z ]+)\\u00A7b \\[(?<Percentage>\\d+)%].?$");


    Pattern INGREDIENT_STAT = Pattern.compile("^\\u00A7(?:c|a)\\+?(?<Min>-?\\d+)(?:\\u00A7(?:4|2)(?: to )?\\u00A7(?:c|a)\\+?(?<Max>-?\\d+))?(?<Suffix>%|/4s|/3s| tier)?\\u00A77 (?<Stat>[A-Za-z0-9 ]+)$");
    Pattern CRAFT_STAT = Pattern.compile("^(?:\\u00A77)?\\u00A7(?:c|a)\\+?(?<Value>-?\\d+)(?<Suffix>%|/4s|/3s| tier)?\\u00A78/(?<Max>-?\\d+)(?:%|/4s|/3s| tier)? \\u00A77(?<Stat>[A-Za-z0-9 ]+)$");
    Pattern ITEM_STAT = Pattern.compile("^(?:\\u00A77)?\\u00A7(?:c|a)\\+?(?<Value>-?\\d+)(?<Suffix>%|/4s|/3s| tier)?(?:\\u00A7(?:4|2)\\*{1,3})? \\u00A77(?<Stat>[A-Za-z0-9 ]+)$");
    Pattern ITEM_HEALTH = Pattern.compile("^\\u00A74. Health: \\+?(?<Value>-?\\d+)$");
    Pattern ITEM_DEFENCE = Pattern.compile("^\\u00A7[0-9a-f]. (?<Name>[A-Za-z]+) Defence: \\+?(?<Value>-?\\d+)$");
    Pattern ITEM_DAMAGE = Pattern.compile("^\\u00A7[0-9a-f]. (?<Name>[A-Za-z]+)(?:\\u00A77)? Damage: (?<Min>\\d+)-(?<Max>\\d+)$");
    Pattern ITEM_ATTACK_SPEED = Pattern.compile("^\\u00A77(?<Speed>(?:Very |Super )?(?:Fast|Normal|Slow)) Attack Speed$");
    Pattern CLASS_REQUIREMENT = Pattern.compile("^\\u00A7(?<Pass>c|a).\\u00A77 Class Req: (?<Value>[A-Za-z ]+/[A-Za-z ]+)$");
    Pattern QUEST_REQUIREMENT = Pattern.compile("^\\u00A7(?<Pass>c|a).\\u00A77 Quest Req: (?<Value>[A-Za-z?'\" ]+).+?$");
    Pattern LEVEL_REQUIREMENT = Pattern.compile("^\\u00A7(?<Pass>c|a).\\u00A77 Combat Lv\\. Min: (?<Value>\\d+)$");
    Pattern STAT_REQUIREMENT = Pattern.compile("^\\u00A7(?<Pass>c|a).\\u00A77 (?<Name>[A-Za-z ]+) Min: (?<Value>-?\\d+)$");
    Pattern POWDERS = Pattern.compile("^\\u00A77\\[(?<Filled>\\d+)/(?<Max>\\d+)] Powder Slots?(?<Powders> \\[(?: ?\\u00A7[0-9a-f].)+\\u00A77])?$");
    Pattern ITEM_QUALITY = Pattern.compile("^\\u00A7[6cbdeaf](?<Quality>Mythic|Fabled|Legendary|Rare|Unique|Set|Normal) Item(?: \\[(?<Rolls>\\d+)])?$");
    Pattern CRAFT_QUALITY = Pattern.compile("^\\u00A73Crafted (?:by (?<Crafter>[A-Za-z0-9_]{3,16})|(?<Type>[A-Za-z]+))\\u00A78 \\[(?<Durability>\\d+)/(?<MaxDurability>\\d+)]$");
    Pattern ITEM_RESTRICTION = Pattern.compile("^\\u00A7c(?<Restriction>Quest|Untradable) Item$");

    Map<String, Pattern> matchers;

    ItemClass itemClass = ItemClass.ERROR;
    String name;
    List<String> flavourText = new ArrayList<>();

    enum ItemClass {
        WEAPON,
        ARMOUR,
        ACCESSORY,
        UNIDENTIFIED,
        TOOL,
        MATERIAL,
        INGREDIENT,
        MISC_POTION,
        POTION,
        FOOD,
        SCROLL,
        HORSE,
        POWDER,
        TELEPORT_SCROLL,
        EMERALDS,
        SCRAP,
        KEYS,
        RUNE,
        DUNGEON_ITEM,
        TOKEN,
        MISC,

        ERROR
    }


    WynnItem(ItemStack item) {
        if (item == null || item.isEmpty()) return;

        this.name = item.getDisplayName();


        StringBuffer Lore = Ref.getLoreRaw(item);


    }
}
