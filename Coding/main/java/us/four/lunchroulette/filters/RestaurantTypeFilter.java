package us.four.lunchroulette.filters;

public class RestaurantTypeFilter extends Filter {
    String name = "Restaurant Type";
    Object value;
    public RestaurantTypeFilter(String valueIn) {
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
