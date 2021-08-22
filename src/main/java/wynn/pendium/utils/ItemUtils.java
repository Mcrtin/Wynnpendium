package wynn.pendium.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ItemUtils {

    public static String encodeSkinTextureURL(String textureURL) {
        JsonObject skin = new JsonObject();
        skin.addProperty("url", "http://textures.minecraft.net/texture/" + textureURL);

        JsonObject textures = new JsonObject();
        textures.add("SKIN", skin);

        JsonObject root = new JsonObject();
        root.add("textures", textures);

        return Base64.getEncoder().encodeToString(new Gson().toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    public static ItemStack createSkullItemStack(String name, String skullID, String textureURL) {
        ItemStack stack = new ItemStack(Items.SKULL, 1, 3);

        NBTTagCompound texture = new NBTTagCompound();
        texture.setString("Value", encodeSkinTextureURL(textureURL));

        NBTTagList textures = new NBTTagList();
        textures.appendTag(texture);

        NBTTagCompound properties = new NBTTagCompound();
        properties.setTag("textures", textures);

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.setTag("Properties", properties);

        skullOwner.setString("Id", skullID);

        stack.setTagInfo("SkullOwner", skullOwner);

        if (name != null) {
            stack.setStackDisplayName(name);
        }


        return stack;
    }

    public static ItemStack getDungeonToken() {
        return createSkullItemStack(null, "fdea850d-ae8b-4e10-8b03-6883494ae266", "54bf893fc6defad218f7836efefbe636f1c2cc1bb650c82fccd99f2c1ee6");
    }


}
