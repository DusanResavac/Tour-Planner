package TourProject.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Tour {

    private StringProperty tourName;
    private ObservableList<TourLog> tourLogs;

    public Tour(StringProperty tourName) {
        this.tourName = tourName;
    }

    public Tour(String tourName) {
        this.tourName = new SimpleStringProperty(tourName);
    }

    public String getTourName() {
        return tourName.get();
    }

    public StringProperty tourNameProperty() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName.set(tourName);
    }

    public ObservableList<TourLog> getTourLogs() {
        return tourLogs;
    }

    public void setTourLogs(ObservableList<TourLog> tourLogs) {
        this.tourLogs = tourLogs;
    }
}
