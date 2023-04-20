package us.four.lunchroulette.filters;

public class FoodTypeFilter implements Filter {
    String name = "Food Type";
    String value;
    public FoodTypeFilter(String valueIn) {
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
