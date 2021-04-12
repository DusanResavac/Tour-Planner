package TourProject;

import TourProject.business.MainBusiness;
import TourProject.model.Tour;
import TourProject.model.TourLog;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainViewModel {
    // http://openbook.rheinwerk-verlag.de/javainsel/12_004.html
    private final StringProperty input = new SimpleStringProperty("");
    private final StringProperty tourLabel = new SimpleStringProperty("");
    private final StringProperty tourDescription = new SimpleStringProperty("");

    private final ObservableList<Tour> toursListing = FXCollections.observableArrayList();
    private final ObservableList<Tour> selectedTour = FXCollections.observableArrayList();
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public StringProperty inputProperty() {
        return input;
    }

    public Property<String> tourLabel() {
        return tourLabel;
    }

    public Property<String> tourDescription() {
        return tourDescription;
    }

    public void setupToursListing() {
        toursListing.addAll(MainBusiness.getTours());
    }

    public ObservableList<Tour> getSelectedTour() {
        return selectedTour;
    }

    public ObservableList getToursListing() {
        return toursListing;
    }

    public ObservableList getTourLogs() {
        return tourLogs;
    }

    public void searchButtonPressed() {
        toursListing.clear();
        toursListing.addAll(MainBusiness.search(input.get()));
    }

    public void setSelectedTour(Tour selectedItem) {
        if (selectedItem == null) {
            return;
        }
        MainBusiness.setSelectedTour(selectedItem);
        tourLabel.setValue(selectedItem.getName());
        tourDescription.setValue(selectedItem.getDescription());

        tourLogs.clear();
        if (selectedItem.getTourLogs() != null) {
            tourLogs.addAll(selectedItem.getTourLogs());
        }
        selectedTour.clear();
        selectedTour.add(selectedItem);
    }
}
