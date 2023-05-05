package us.four.lunchroulette.filters;

import java.util.NoSuchElementException;

// This code defines an enum type named RestaurantType
public enum RestaurantType {
    ANY("Any"),                     // Represents any type of restaurant
    FAST_FOOD("Fast Food"),         //Represents fast food restaurants
    BAR("Bar"),                     //Represents bar restaurants
    CAFE("Cafe"),                   //Represents cafe restaurants
    DINER("Diner");                 //Represents diner restaurants

    // Instance variable to hold the type of restaurant
    private final String type;

    // Constructor to initialize the instance variable with the given string
    RestaurantType(String s) {
        this.type = s;
    }

    // Method to return the type of restaurant
    String getType() {
        return this.type;
    }

    // Method to convert the given string to a RestaurantType enum constant
    public static RestaurantType fromString(String string) {
        for (RestaurantType pt : values()) {
            if (pt.getType().equals(string)) {
                return pt;
            }
        }
        // If no matching enum constant is found, throw an exception
        throw new NoSuchElementException("Element " + string + " does not exist.");
    }
}
