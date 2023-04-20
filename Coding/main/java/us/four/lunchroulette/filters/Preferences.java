package us.four.lunchroulette.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

enum ResturantType {
    ANY("Any"),
    SIT_DOWN("Sit-Down"),
    FAST_FOOD("Fast Food"),
    BUFFET("Buffet"),
    DRIVE_THRU("Drive Through"),
    OUTDOORS("Outdoors");
    private String type;
    ResturantType(String s) {
        this.type = s;
    }
    String getType() {
        return this.type;
    }
    public static ResturantType fromString(String string) {
        for (ResturantType pt : values()) {
            if (pt.getType().equals(string)) {
                return pt;
            }
        }
        throw new NoSuchElementException("Element " + string + " does not exist.");
    }
}
enum FoodType {
    ANY,
    AMERICAN,
    ITALIAN,
    MEXICAN,
    ASIAN,
    VEGETARIAN,
    BREAKFAST,
    BBQ;
}

public class Preferences {
    private List<Filter> filters;
    private String name = "Placeholder";
    public Preferences(List<Filter> filterList, String name) {
        this.filters = filterList;
        this.name = name;
    }
    public Preferences() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++)
            sb.append("Any\n");
        sb.append("Any");

        this.filters = FilterFactory.generateFiltersFromString(sb.toString());
        this.name = "Any";
    }
    public List<Filter> getFilters() {
        return filters;
    }
    public int getDistance() {
        if(filters.get(4).getValue().equals("Any"))
            return 0;
        else
            return Integer.parseInt(String.valueOf(filters.get(4).getValue()));
    }
    public int getRating() {
        if(filters.get(3).getValue().equals("Any"))
            return 0;
        else
            return String.valueOf(filters.get(3).getValue()).trim().length()/2;
    }
    public int getPriceRange() {
        if(filters.get(2).getValue().equals("Any"))
            return 0;
        else
            return String.valueOf(filters.get(2).getValue()).trim().length()/2;
    }
    public ResturantType getRestaurantType() {
        return ResturantType.fromString(filters.get(1).getValue().toString());
    }
    public FoodType getFoodType() {
        return FoodType.valueOf(filters.get(0).getValue().toString().toUpperCase());
    }
    public String getName() {
        return this.name;
    }

}
