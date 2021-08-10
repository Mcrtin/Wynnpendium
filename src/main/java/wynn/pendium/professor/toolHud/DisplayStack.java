package wynn.pendium.professor.toolHud;

import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;

public class DisplayStack {

    private NodeType Type;
    private ItemStack Stack;
    private int Amount;
    private String Tier;

    DisplayStack(NodeType type, ItemStack stack, int amount, int tier) {
        this.Type = type;
        this.Stack = stack;
        this.Amount = amount;

        switch (tier) {
            default: this.Tier = ""; break;
            case 1: this.Tier = "\u00A7e\u272B\u00A78\u272B\u272B"; break;
            case 2: this.Tier = "\u00A7e\u272B\u272B\u00A78\u272B"; break;
            case 3: this.Tier = "\u00A7e\u272B\u272B\u272B"; break;
        }
    }

    void addStack(ItemStack stack) {
        this.Amount += stack.getCount();
    }

    ItemStack getStack() {
        return this.Stack;
    }

    NodeType getType() {
        return this.Type;
    }

    boolean isSame(ItemStack item) {
        return item.getDisplayName().equals(this.Stack.getDisplayName());
    }

    void display(int x, int y) {
        Ref.mc.getRenderItem().renderItemIntoGUI(this.Stack, x, y);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Ref.mc.getRenderItem().renderItemOverlayIntoGUI(Ref.mc.fontRenderer, this.Stack, x*2 + Ref.mc.fontRenderer.getStringWidth(this.Tier)/2, y*2 - 10, this.Tier);
        Ref.mc.getRenderItem().renderItemOverlayIntoGUI(Ref.mc.fontRenderer, this.Stack, x*2 + 13, y*2 + 13, String.valueOf(this.Amount));
        GL11.glScalef(2f, 2f, 2f);
    }
}
