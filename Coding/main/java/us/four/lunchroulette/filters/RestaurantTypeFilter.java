package us.four.lunchroulette.filters;

public class RestaurantTypeFilter extends Filter {
    String name = "Restaurant Type";
    String value;
    public RestaurantTypeFilter(String valueIn) {
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
