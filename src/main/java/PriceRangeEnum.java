import java.util.EnumSet;

public enum PriceRangeEnum {
    A("A",1000,1500),
    B("B",1500,2000),
    C("C",2000,2500),
    D("D",2500,-1);


    PriceRangeEnum(String symbol, int priceLowerLimit, int priceUpperLmit) {
        this.symbol = symbol;
        this.priceLowerLimit = priceLowerLimit;
        this.priceUpperLimit = priceUpperLmit;
    }
    private String symbol;
    private int priceLowerLimit;
    private int priceUpperLimit;
    private static EnumSet<PriceRangeEnum> priceRangeEnumSet = EnumSet.allOf(PriceRangeEnum.class);
    public String getSymbol() {
        return symbol;
    }

    public int getPriceLowerLimit() {
        return priceLowerLimit;
    }

    public int getPriceUpperLimit() {
        return priceUpperLimit;
    }

    public static int getPriceLowerLimitBySymbol(String symbol) {
        assert symbol != null;
        for (PriceRangeEnum priceRangeEnum : priceRangeEnumSet) {
            if (priceRangeEnum.symbol.equals(symbol))
                return priceRangeEnum.getPriceLowerLimit();
        }
        return 0;
    }

    public static int getPriceUpperLimitBySymbol(String symbol) {
        assert symbol != null;
        for (PriceRangeEnum priceRangeEnum : priceRangeEnumSet) {
            if (priceRangeEnum.symbol.equals(symbol))
                return priceRangeEnum.getPriceUpperLimit();
        }
        return 0;
    }
}
