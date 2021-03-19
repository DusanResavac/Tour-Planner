package TourProject.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Tour {

    private String name;
    private String description;
    private ObservableList<TourLog> tourLogs;


    public Tour(String tourName, String description) {
        this.name = tourName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObservableList<TourLog> getTourLogs() {
        return tourLogs;
    }

    public void setTourLogs(ObservableList<TourLog> tourLogs) {
        this.tourLogs = tourLogs;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "tourName=" + name +
                ", tourLogs=" + tourLogs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tour tour = (Tour) o;

        if (name != null ? !name.equals(tour.name) : tour.name != null) return false;
        return tourLogs != null ? tourLogs.equals(tour.tourLogs) : tour.tourLogs == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tourLogs != null ? tourLogs.hashCode() : 0);
        return result;
    }
}
