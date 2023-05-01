package us.four.lunchroulette.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class FilterFactory {
    public static List<Filter> generateFiltersFromString(String input) {
        List<Filter> filters = new ArrayList<>();
        String[] filtersAsStrings = input.split("\n");
        Stack<String> stringStack = new Stack<>();
        Collections.addAll(stringStack, filtersAsStrings);
        Collections.reverse(stringStack);
        filters.add(new FoodTypeFilter(stringStack.pop()));
        filters.add(new RestaurantTypeFilter(stringStack.pop()));
        filters.add(new PriceFilter(stringStack.pop()));
        filters.add(new RatingFilter(stringStack.pop()));
        filters.add(new DistanceFilter(stringStack.pop()));
        return filters;
    }
}
