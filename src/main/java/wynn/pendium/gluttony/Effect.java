package wynn.pendium.gluttony;

public class Effect {
    String name;
    String suffix;
    int value;
    boolean found = false;

    Effect(String Name, int Value, String Suffix) {
        this.name = Name;
        this.value = Value;
        this.suffix = Suffix;
    }

    String getDisplay() {
        return (this.value > 0 ? "+" + this.value : String.valueOf(this.value)) + this.suffix + " " + this.name;
    }
}
