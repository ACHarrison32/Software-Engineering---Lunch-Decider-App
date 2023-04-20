package us.four.lunchroulette.filters;

public class PriceFilter extends Filter {
    String name = "Price";
    Object value;
    public PriceFilter(String valueIn) {
        value = valueIn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

}
