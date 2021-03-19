package TourProject.business;

import TourProject.database.Database;
import TourProject.model.Tour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainBusiness {

    private static ObservableList<Tour> tours;
    private static Tour selectedTour;

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

        for (int i = 0; i < tours.size(); i++) {
            if (isEmpty || tours.get(i).getName().toLowerCase().contains(text)) {
                tempTours.add(tours.get(i));
            }
        }

        return tempTours;
    }
}
