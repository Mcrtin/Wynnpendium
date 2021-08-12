package wynn.pendium.hud.components.features;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;
import wynn.pendium.professor.NodeType;

public class DisplayStack {

    private NodeType Type;
    private ItemStack Stack;
    private int Amount;
    private String Tier;

    public DisplayStack(NodeType type, ItemStack stack, int amount, int tier) {
//        super(240, 301);
        this.Type = type;
        this.Stack = stack;
        this.Amount = amount;

        switch (tier) {
            default:
                this.Tier = "";
                break;
            case 1:
                this.Tier = "\u00A7e\u272B\u00A78\u272B\u272B";
                break;
            case 2:
                this.Tier = "\u00A7e\u272B\u272B\u00A78\u272B";
                break;
            case 3:
                this.Tier = "\u00A7e\u272B\u272B\u272B";
                break;
        }
    }

    public void addStack(ItemStack stack) {
        this.Amount += stack.getCount();
    }

    public ItemStack getStack() {
        return this.Stack;
    }

    public NodeType getType() {
        return this.Type;
    }

    public boolean isSame(ItemStack item) {
        return item.getDisplayName().equals(this.Stack.getDisplayName());
    }

    public void display(int x, int y) {
        Ref.mc.getRenderItem().renderItemIntoGUI(this.Stack, x, y);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Ref.mc.getRenderItem().renderItemOverlayIntoGUI(Ref.mc.fontRenderer, this.Stack, x * 2 + Ref.mc.fontRenderer.getStringWidth(this.Tier) / 2, y * 2 - 10, this.Tier);
        Ref.mc.getRenderItem().renderItemOverlayIntoGUI(Ref.mc.fontRenderer, this.Stack, x * 2 + 13, y * 2 + 13, String.valueOf(this.Amount));
        GL11.glScalef(2f, 2f, 2f);
    }

//    @Override
//    public void renderDummy(int x, int y, Minecraft mc) {
//
//        ItemStack Stack = new ItemStack(Items.DIAMOND);
//        String Tier = "3";
//        int Amount = 5;
//
//
//        Ref.mc.getRenderItem().renderItemIntoGUI(Stack, x, y);
//        GL11.glScalef(0.5f, 0.5f, 0.5f);
//        Ref.mc.getRenderItem().renderItemOverlayIntoGUI(Ref.mc.fontRenderer, Stack, x * 2 + Ref.mc.fontRenderer.getStringWidth(Tier) / 2, y * 2 - 10, Tier);
//        Ref.mc.getRenderItem().renderItemOverlayIntoGUI(Ref.mc.fontRenderer, Stack, x * 2 + 13, y * 2 + 13, String.valueOf(Amount));
//        GL11.glScalef(2f, 2f, 2f);
//    }
}
