package TourProject.Model;

import TourProject.Model.TourLog.TourLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FilterCondition {
    private final Map<String, String> attributeMethodMapper = Map.ofEntries(
            Map.entry("rating", "getRating"),
            Map.entry("breaks", "getNumberOfBreaks"),
            Map.entry("distance", "getDistance")
    );
    private final Map<String, Datatype> attributeDatatypeMapper = Map.ofEntries(
        Map.entry("rating", Datatype.INTEGER),
        Map.entry("breaks", Datatype.INTEGER),
        Map.entry("distance", Datatype.DOUBLE)
    );

    private String attribute = null;
    private Comparison comparison = null;
    private Integer intValue = null;
    private Double doubleValue = null;

    public FilterCondition(String attribute, String value, Comparison comparison) throws IllegalArgumentException {
        if (attributeDatatypeMapper.get(attribute) == null || attributeMethodMapper.get(attribute) == null) {
            throw new IllegalArgumentException("Das Attribut wird nicht nicht unterstützt");
        }
        try {
            switch (attributeDatatypeMapper.get(attribute)) {
                case INTEGER -> {
                    this.intValue = Integer.parseInt(value);
                }
                case DOUBLE -> {
                    this.doubleValue = Double.parseDouble(value);
                }
            }

            this.attribute = attribute;
            this.comparison = comparison == null ? Comparison.EQUALS : comparison;
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("Der Wert konnte nicht umgewandelt werden");
        }
    }


    public boolean isFilterMet (TourLog tourLog) {
        if (comparison == null || attribute == null) {
            return false;
        }

        try {
            Method method = tourLog.getClass().getMethod(attributeMethodMapper.get(attribute));

            if (intValue != null) {
                Integer value = (Integer) method.invoke(tourLog);
                switch (comparison) {
                    case EQUALS -> {
                        return intValue.equals(value);
                    }
                    case GREATER_OR_EQUALS -> {
                        return value >= intValue;
                    }
                    case SMALLER_OR_EQUALS -> {
                        return value <= intValue;
                    }
                }
            } else if (doubleValue != null) {
                Double value = (Double) method.invoke(tourLog);
                switch (comparison) {
                    case EQUALS -> {
                        return doubleValue.equals(value);
                    }
                    case GREATER_OR_EQUALS -> {
                        return value >= doubleValue;
                    }
                    case SMALLER_OR_EQUALS -> {
                        return value <= doubleValue;
                    }
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) { }

        return false;
    }

}
