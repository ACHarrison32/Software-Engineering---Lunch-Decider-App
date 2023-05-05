package us.four.lunchroulette.filters;

import java.util.List;


// Class for storing user's preferences for restaurant search
public class Preferences
{
    private final List<Filter> filters;// List of filters (criteria) to apply to restaurant search
    private String name;// User-defined name for preferences

    // Constructor for Preferences class that takes a List of Filter objects and a name for the preferences
    public Preferences(List<Filter> filterList, String name)
    {
        this.filters = filterList;
        this.name = name;
    }

    // Default constructor for Preferences class that creates a list of "Any" filters and sets the name to "Any"
    public Preferences() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++)
            sb.append("Any\n");
        sb.append("Any");

        this.filters = FilterFactory.generateFiltersFromString(sb.toString());
        this.name = "Any";
    }

    // Getter method for the list of filters
    public List<Filter> getFilters()
    {
        return filters;
    }

    // Method to get the search radius in meters based on the user's distance filter selection
    public int getDistance()
    {
        // If distance filter is set to "Any", return max distance (32186 meters)
        if(filters.get(4).getValue().equals("Any"))
        {
            return 32186;
        }
        else
        {
            // Convert distance from miles to meters
            return (int) (Integer.parseInt(String.valueOf(filters.get(4).getValue())) * 1609.344);
        }
    }

    // Method to get the minimum rating for a restaurant based on the user's rating filter selection
    public int getRating()
    {
        // If rating filter is set to "Any", return 0 (no rating filter)
        if(filters.get(3).getValue().equals("Any"))
        {
            return 0;
        }
        else
        {
            // Return length of string value of rating filter (1-5)
            return String.valueOf(filters.get(3).getValue()).trim().length();
        }
    }

    // Method to get the price range for a restaurant based on the user's price filter selection
    public int getPriceRange()
    {
        // If price filter is set to "Any", return 0 (no price filter)
        if(filters.get(2).getValue().equals("Any"))
        {
            return 0;
        }
        else
        {
            // Divide length of string value of price filter by 2 to get price range (1-4)
            return String.valueOf(filters.get(2).getValue()).trim().length() / 2;
        }
    }

    // Method to get the restaurant type (e.g. fast food, cafe, etc.) based on the user's restaurant type filter selection
    public RestaurantType getRestaurantType()
    {
        // Convert string value of restaurant type filter to RestaurantType enum
        return RestaurantType.fromString(filters.get(1).getValue());
    }

    // Method to get the food type (e.g. Italian, Mexican, etc.) based on the user's food type filter selection
    public FoodType getFoodType()
    {
        // Convert string value of food type filter to FoodType enum
        return FoodType.valueOf(filters.get(0).getValue().toUpperCase());
    }

    // Getter method for the name of the preferences
    public String getName()
    {
        return this.name;
    }

    // Setter method for the name of the preferences
    public void setName(String name)
    {
        this.name = name;
    }
}

