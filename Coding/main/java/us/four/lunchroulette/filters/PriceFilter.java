package us.four.lunchroulette.filters;

// Define a public class named PriceFilter that implements the Filter interface
public class PriceFilter implements Filter
{
    //Variable Declarations
    String name = "Price";// Initialize the name variable to "Price"
    String value;// The value variable is initialized in the constructor

    // Define a constructor that takes a String argument valueIn
    public PriceFilter(String valueIn) {
        value = valueIn;
    }

    // getName method from the Filter interface
    @Override
    public String getName() {
        return name;// Return the name instance variable
    }

    // getValue method from the Filter interface
    @Override
    public String getValue() {
        return value;// Return the value instance variable
    }

}
