package us.four.lunchroulette.filters;

public class FoodTypeFilter extends Filter {
    String name = "Food Type";
    Object value;
    public FoodTypeFilter(String valueIn) {
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
