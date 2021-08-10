package wynn.pendium.gluttony;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;

public class DisplayEffect extends Gui {

    String name;
    String suffix;
    int value;
    ItemStack miscIcon = null;

    private String cache;
    private String change = null;

    private long durationEnd;
    private String time;

    // Array of what consumables are contributing to the effect
    // Default is miscIcon, generic potions
    //                      Potion, Scroll, Food, Default
    boolean[] consumables = {false, false, false, false};

    DisplayEffect(String Name, int Value, String Suffix, long endStamp) {
        this.name = Name;
        this.value = Value;
        this.suffix = Suffix;
        this.durationEnd = endStamp;

        updateTime();
    }

    void setMiscIcon(ItemStack icon) {
        this.miscIcon = icon;
    }

    void updateCache() {
        this.cache = (this.value >= 0 ? "+" + this.value + this.suffix : this.value + this.suffix);
    }

    void setDurationEnd(long durationEnd) {
        this.durationEnd = durationEnd;
    }

    long getDurationEnd() {
        return durationEnd;
    }

    void setChange(int change) {
        if (change == 0)
            this.change = null;
        else
            this.change = (change >= 0 ? "+" + change : String.valueOf(change)) + (this.value == 0 ? this.suffix : "");
    }

    void updateTime() {
        if (this.durationEnd == 0) {
            this.time = "";
            return;
        }


        //long timeTmp = (this.durationEnd - Ref.mc.world.getTotalWorldTime()) / 20; // world time
        long timeTmp = (this.durationEnd - System.currentTimeMillis()) / 1000; // real time
        this.time = (timeTmp/60 < 10 ? "0" : "") + timeTmp/60 + ":" + (timeTmp%60 < 10 ? "0" : "") + timeTmp%60;
    }

    void draw(int x, int y, float size, float inverse, int i) {

        int OffsetBox = (int) ((Ref.mc.fontRenderer.getStringWidth(this.name) + (this.value == 0 ? 0 : Ref.mc.fontRenderer.getStringWidth(this.cache) + 4) + (this.time.equals("") ? 0 : Ref.mc.fontRenderer.getStringWidth(this.time) + 4)) * size);
        drawRect((int) ((x - OffsetBox)*inverse) -1, (int) ((y * inverse) + (i*12)) -1, (int) ((x + 27)*inverse), (int) ((y * inverse) + (i*12) + 8), 0x55000000);

        Ref.mc.fontRenderer.drawString("\u24B6", (int) ((x + 18) * inverse), (int) ((y * inverse) + (i*12)), (this.consumables[2] ? 0xffffffff : 0x7f7f7f7f));
        Ref.mc.fontRenderer.drawString("\u24BA", (int) ((x + 10) * inverse), (int) ((y * inverse) + (i*12)), (this.consumables[1] ? 0xffffffff : 0x7f7f7f7f));
        Ref.mc.fontRenderer.drawString("\u24C1", (int) ((x + 2) * inverse), (int) ((y * inverse) + (i*12)), (this.consumables[0] ? 0xffffffff : 0x7f7f7f7f));



        int Offset = (int) (Ref.mc.fontRenderer.getStringWidth(this.name) * size);
        Ref.mc.fontRenderer.drawString(this.name, (int) ((x - Offset) * inverse), (int) ((y * inverse) + (i*12)), 0xffaaaaaa);


        if (this.value != 0) {
            Offset += (int) ((Ref.mc.fontRenderer.getStringWidth(this.cache) + 4) * size);
            Ref.mc.fontRenderer.drawString(this.cache, (int) ((x - Offset) * inverse), (int) ((y * inverse) + (i * 12)), (this.cache.charAt(0) == '+' ? (this.suffix.equals("\u2764") ? 0xffaa0000 : (this.suffix.equals("\u273A") ? 0xff55ffff : 0xff00aa00)) : 0xffaa0000));
        }

        if (!this.time.equals("")) {
            Offset += (int) ((Ref.mc.fontRenderer.getStringWidth(this.time) + 4) * size);
            Ref.mc.fontRenderer.drawString(this.time, (int) ((x - Offset) * inverse), (int) ((y * inverse) + (i * 12)), 0xffffaa00);
        }

        if (this.change != null) {
            Offset += (int) ((Ref.mc.fontRenderer.getStringWidth(this.change) + 4) * size);
            drawRect((int) ((x - Offset) * inverse) - 1, (int) ((y * inverse) + (i * 12)) - 1, (int) ((x - Offset + Ref.mc.fontRenderer.getStringWidth(this.change)) * inverse) + 1, (int) ((y * inverse) + (i * 12) + 8), 0x55000000);
            Ref.mc.fontRenderer.drawString(this.change, (int) ((x - Offset) * inverse), (int) ((y * inverse) + (i * 12)), (this.change.charAt(0) == '+' ? 0xff55ff55 : 0xffff5555));
        }

        if (miscIcon != null) {
            drawRect((int) ((x + 27)*inverse), (int) ((y * inverse) + (i*12)) -1, (int) ((x + 35)*inverse), (int) ((y * inverse) + (i*12) + 8), 0x55000000);
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            RenderHelper.enableGUIStandardItemLighting();
            Ref.mc.getRenderItem().renderItemIntoGUI(this.miscIcon, (x +26) * 2, (int) ((y * inverse) + (i*12))*2);
            RenderHelper.disableStandardItemLighting();
            GL11.glScalef(2f, 2f, 2f);
        }

    }
}
