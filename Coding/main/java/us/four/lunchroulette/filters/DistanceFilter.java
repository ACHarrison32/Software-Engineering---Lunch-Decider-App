package us.four.lunchroulette.filters;

// Define a class named DistanceFilter that implements the Filter interface z
public class DistanceFilter implements Filter
{
    //Variable declarations
    // Initialize the name variable to "Distance"
    String name = "Distance";
    // The value variable is initialized in the constructor
    String value;

    // Define a constructor that takes a String argument valueIn
    public DistanceFilter(String valueIn)
    {
        value = valueIn;
    }

    // getName method from the Filter interface
    // Return the name instance variable
    @Override
    public String getName()
    {
        return name;
    }
    // getValue method from the Filter interface
    @Override
    public String getValue()
    {
        // Return the value instance variable
        return value;
    }
}
