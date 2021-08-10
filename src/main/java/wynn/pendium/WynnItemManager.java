package wynn.pendium;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WynnItemManager {

    private static final Pattern Format = Pattern.compile("^(?<Name>[A-Za-z_]+)=(?<Regex>.+)$");
    private static final Pattern SelfReferenceFormat = Pattern.compile("%(?<Name>[A-Za-z_]+)%(?<Requisites>[R])%");

    private static HashMap<String, Pattern> Matchers;

    private class ItemFormat {
        String Matcher;
        boolean repeats;
        boolean essential;

        ItemFormat(String MatchReference, String RequisiteCompound) {
            this.Matcher = MatchReference;
            this.repeats = RequisiteCompound.contains("R");
            this.essential = RequisiteCompound.contains("E");
        }
    }

    static void load(List<String> list) {
        Matcher match;
        for (String s : list) {
            if (!(match = Format.matcher(s)).matches())
                continue;

            Matchers.put(match.group("Name"), Pattern.compile(match.group("Regex")));
        }
    }
}
