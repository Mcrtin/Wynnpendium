package wynn.pendium.hud;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private int[] Colours;
    private String[] Text;
    private int[] Offset;

    int Age = 0;

    String getText(int i) {
        if (i > Text.length)
            return "";
        return Text[i];
    }

    int getColour(int i) {
        if (i > Text.length)
            return 16755200;
        return Colours[i];
    }

    int getOffset(int i) {
        i--;
        if (i < 0 || i > Text.length)
            return 0;
        return Offset[i];
    }

    int getLength() {
        return Offset[Offset.length-1];
    }

    int countParts() {
        return Text.length;
    }

    Message(String s) {
        List<String> t = new ArrayList<>();
        List<Integer> c = new ArrayList<>();
        List<Integer> o = new ArrayList<>();

        String[] splits = s.split("\\$");

        for (int i = 0; i < splits.length; i++) {
            if (splits[i].matches("0x[0-9a-fA-F]{6}")) {
                c.add(Integer.decode(splits[i]));
                continue;
            }

            t.add(splits[i]);
            o.add(Minecraft.getMinecraft().fontRenderer.getStringWidth(splits[i]));
            if (t.size() > c.size())
                c.add((c.isEmpty() ? 16755200 : c.get(c.size()-1)));
        }

        int total = 0;
        this.Text = new String[t.size()];
        this.Colours = new int[c.size()];
        this.Offset = new int[o.size()];
        for (int i = 0; i < this.Text.length; i++) {
            this.Text[i] = t.get(i);
            this.Colours[i] = c.get(i);
            this.Offset[i] = (total += o.get(i));
        }
    }

    String text() {
        return String.join("", this.Text);
    }
}
