import java.util.HashMap;
import java.util.Map;

public class Structure {
    private Structure() {
        throw new AssertionError();
    }
    private static Map<String, Byte> map = new HashMap<>(5);
    static {
        map.put("WD", (byte) 1);
        map.put("SY", (byte) 2);
        map.put("TR", (byte) 3);
    }
    static byte getStructureIdBySymbol(String symbol) {
        return map.get(symbol);
    }
}
