package us.four.lunchroulette.filters;

public class DistanceFilter extends Filter {
    String name = "Distance";
    Object value;
    public DistanceFilter(int valueIn) {
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
