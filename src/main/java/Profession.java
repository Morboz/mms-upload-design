import java.util.HashMap;
import java.util.Map;

public class Profession {
    private Profession() {
        throw new AssertionError();
    }
    private static Map<String, Byte> map = new HashMap<>(5);
    static {
        map.put("I", (byte) 1);
        map.put("II", (byte) 2);
        map.put("III", (byte) 3);
        map.put("IV", (byte) 4);
    }
    static byte getProfessionIdBySymbol(String symbol) {
        return map.get(symbol);
    }
}
