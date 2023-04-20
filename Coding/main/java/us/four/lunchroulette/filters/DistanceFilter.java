package us.four.lunchroulette.filters;

public class DistanceFilter extends Filter {
    String name = "Distance";
    String value;
    public DistanceFilter(String valueIn) {
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
