package us.four.lunchroulette.filters;

public class RatingFilter extends Filter {
    String name = "Rating";
    String value;
    public RatingFilter(String valueIn) {
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
