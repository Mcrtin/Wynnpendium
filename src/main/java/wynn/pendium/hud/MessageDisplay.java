package wynn.pendium.hud;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import wynn.pendium.Ref;

import java.util.ArrayList;
import java.util.List;

public class MessageDisplay {

    private FontRenderer font = Ref.mc.fontRenderer;

    private List<Message> msgs = new ArrayList<>();


    private float location_x;
    private float location_y;
    private int offset_x;
    private int offset_y;
    private int pos_x;
    private int pos_y;

    private Align align;
    private float size;
    private float size_inverse;

    enum Align {
        LEFT,
        RIGHT,
        CENTER
    }

    MessageDisplay(float x_pos, float y_pos, float size, int x_offset, int y_offset, String align) {
        this.location_x = x_pos;
        this.location_y = y_pos;
        this.size = size;
        this.offset_x = x_offset;
        this.offset_y = y_offset;

        switch (align.toUpperCase()) {
            case "LEFT": this.align = Align.LEFT; break;
            case "RIGHT": this.align = Align.RIGHT; break;
            default: this.align = Align.CENTER; break;
        }

        updatePosition();
    }

    void addMessage(Message msg) {
        if (font == null) font = Ref.mc.fontRenderer;
        this.msgs.add(msg);

        updatePosition();
    }

    void clearAll() {
        this.msgs.clear();
    }

    void updateMessages() {
        for (int i = 0; i < msgs.size(); i++) {
            msgs.get(i).Age--;

            if (msgs.get(i).Age < 0 && Ref.mc.inGameHasFocus)
                msgs.get(i).Age = 100;

            if (msgs.get(i).Age == 0) {
                msgs.remove(i);
                i--;
            }
        }
    }

    void updatePosition() {
        this.pos_x = (int)(new ScaledResolution(Ref.mc).getScaledWidth() * this.location_x) + this.offset_x;
        this.pos_y = (int)(new ScaledResolution(Ref.mc).getScaledHeight() * this.location_y) + this.offset_y;
        this.size_inverse = (float)Math.pow(this.size,-1);
    }

    void show() {
        if (this.msgs.isEmpty()) return;

        GL11.glScalef(this.size,this.size,this.size);
        switch (this.align) {
            case LEFT: show_left(); break;
            case RIGHT: show_right(); break;
            case CENTER: show_center(); break;
        }
        GL11.glScalef(this.size_inverse,this.size_inverse,this.size_inverse);
    }


    private void show_left() {
        for (int i = 0; i < this.msgs.size(); i++) {
            Message msg = this.msgs.get(i);
            for (int j = 0; j < msg.countParts(); j++)
                font.drawString(msg.getText(j), (this.pos_x * this.size_inverse) + msg.getOffset(j), (int)(this.pos_y * this.size_inverse) + (i * 12), msg.getColour(j), true);
        }
    }

    private void show_right() {
        for (int i = 0; i < this.msgs.size(); i++) {
            Message msg = this.msgs.get(i);
            for (int j = 0; j < msg.countParts(); j++)
                font.drawString(msg.getText(j), (this.pos_x * this.size_inverse) + msg.getOffset(j) - msg.getLength(), (int)(this.pos_y * this.size_inverse) + (i * 12), msg.getColour(j), true);
        }
    }

    private void show_center() {
        for (int i = 0; i < this.msgs.size(); i++) {
            Message msg = this.msgs.get(i);
            for (int j = 0; j < msg.countParts(); j++) {
                font.drawString(msg.getText(j), (this.pos_x * this.size_inverse) + msg.getOffset(j) - (msg.getLength() / 2), (int) (this.pos_y * this.size_inverse) + (i * 12), msg.getColour(j), true);
            }
        }
    }
}
