package us.four.lunchroulette.filters;

// Define a public class named FoodTypeFilter that implements the Filter interface
public class FoodTypeFilter implements Filter
{
    //Variable Declarations
    String name = "Food Type";// Initialize the name variable to "Food Type"
    String value;// The value variable is initialized in the constructor

    // Define a constructor that takes a String argument valueIn
    public FoodTypeFilter(String valueIn)
    {
        value = valueIn;
    }

    // getName method from the Filter interface
    @Override
    public String getName()
    {
        return name;// Return the name instance variable
    }

    // getValue method from the Filter interface
    @Override
    public String getValue()
    {
        return value;// Return the value instance variable
    }

}
