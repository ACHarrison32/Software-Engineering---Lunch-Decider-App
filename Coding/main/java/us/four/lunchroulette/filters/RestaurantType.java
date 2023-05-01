package us.four.lunchroulette.filters;

import java.util.NoSuchElementException;

public enum RestaurantType {
    ANY("Any"),
    FAST_FOOD("Fast Food"),
    BAR("Bar"),
    CAFE("Cafe"),
    DINER("Diner");
    private final String type;

    RestaurantType(String s) {
        this.type = s;
    }

    String getType() {
        return this.type;
    }

    public static RestaurantType fromString(String string) {
        for (RestaurantType pt : values()) {
            if (pt.getType().equals(string)) {
                return pt;
            }
        }
        throw new NoSuchElementException("Element " + string + " does not exist.");
    }
}
