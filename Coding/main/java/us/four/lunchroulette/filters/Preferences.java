package us.four.lunchroulette.filters;

import java.util.List;



public class Preferences {
    private final List<Filter> filters;
    private String name;
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
            return 32186;
        else {
            return (int) (Integer.parseInt(String.valueOf(filters.get(4).getValue())) * 1609.344);
        }
    }
    public int getRating() {
        if(filters.get(3).getValue().equals("Any"))
            return 0;
        else
            return String.valueOf(filters.get(3).getValue()).trim().length();
    }
    public int getPriceRange() {
        if(filters.get(2).getValue().equals("Any"))
            return 0;
        else
            return String.valueOf(filters.get(2).getValue()).trim().length()/2;
    }
    public RestaurantType getRestaurantType() {
        return RestaurantType.fromString(filters.get(1).getValue());
    }
    public FoodType getFoodType() {
        return FoodType.valueOf(filters.get(0).getValue().toUpperCase());
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

}

