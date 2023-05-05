package us.four.lunchroulette.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

// Define a public class named FilterFactory
public class FilterFactory
{
    // Define a public static method that generates a list of filters from a string input
    public static List<Filter> generateFiltersFromString(String input)
    {
        // Create a new ArrayList to hold the filters
        List<Filter> filters = new ArrayList<>();
        // Split the input string into an array of strings using newline character as delimiter
        String[] filtersAsStrings = input.split("\n");
        // Create a new Stack of strings and add the array of strings to it
        Stack<String> stringStack = new Stack<>();
        Collections.addAll(stringStack, filtersAsStrings);
        // Reverse the order of the strings in the stack
        Collections.reverse(stringStack);
        // Create a new instance of each type of filter and add them to the list of filters in the reverse order
        // that they were added to the stack
        filters.add(new FoodTypeFilter(stringStack.pop()));
        filters.add(new RestaurantTypeFilter(stringStack.pop()));
        filters.add(new PriceFilter(stringStack.pop()));
        filters.add(new RatingFilter(stringStack.pop()));
        filters.add(new DistanceFilter(stringStack.pop()));
        // Return the list of filters
        return filters;
    }
}
