package TourProject.business;

import TourProject.database.Database;
import TourProject.model.Tour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainBusiness {

    private static ObservableList<Tour> tours;
    private static Tour selectedTour;

    // TODO: Implement Constructor - move from static implementation to instance-based


    public static ObservableList<Tour> getTours () {
        if (tours == null) {
            tours = FXCollections.observableArrayList(
                    Database.getInstance().getTourList()
            );
        }
        return tours;
    }

    public static Tour getSelectedTour() {
        return selectedTour;
    }

    public static void setSelectedTour(Tour selectedTour) {
        MainBusiness.selectedTour = selectedTour;
    }

    public static ObservableList<Tour> search(String text) {
        text = text.toLowerCase().trim();
        ObservableList<Tour> tempTours = FXCollections.observableArrayList();
        boolean isEmpty = text.equals("");

        for (Tour tour : tours) {
            if (isEmpty || tour.getName().toLowerCase().contains(text)) {
                tempTours.add(tour);
            }
        }

        return tempTours;
    }
}
