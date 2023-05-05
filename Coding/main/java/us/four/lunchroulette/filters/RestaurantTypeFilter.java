package us.four.lunchroulette.filters;

// Define a public class named RestaurantTypeFilter that implements the Filter interface
public class RestaurantTypeFilter implements Filter
{
    //Variable Declarations
    String name = "Restaurant Type";// Initialize the name variable to "Restaurant Type"
    String value;// The value variable is initialized in the constructor

    // Define a constructor that takes a String argument valueIn
    public RestaurantTypeFilter(String valueIn) {
        value = valueIn;
    }

    // Override the getName method from the Filter interface
    @Override
    public String getName() {
        return name;// Return the name instance variable
    }

    // Override the getValue method from the Filter interface
    @Override
    public String getValue() {
        return value;// Return the value instance variable
    }

}
