package us.four.lunchroulette.filters;

public class PriceFilter implements Filter {
    String name = "Price";
    String value;
    public PriceFilter(String valueIn) {
        value = valueIn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

}
