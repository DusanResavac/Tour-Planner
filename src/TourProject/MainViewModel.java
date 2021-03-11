package TourProject;

import TourProject.model.Tour;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainViewModel {
    // http://openbook.rheinwerk-verlag.de/javainsel/12_004.html
    private final StringProperty input = new SimpleStringProperty("");

    private final ObservableList<Tour> toursListing =
            FXCollections.observableArrayList(
                    new Tour("Tour 1"),
                    new Tour("Tour 2"),
                    new Tour("Tour 3")
            );
    private final ObservableList<Tour> defaultToursListing =
            FXCollections.observableArrayList(
                    new Tour("Tour 1"),
                    new Tour("Tour 2"),
                    new Tour("Tour 3")
            );


    public StringProperty inputProperty() {
        System.out.println("VM: get input prop");
        return input;
    }


    public ObservableList getToursListing() {
        return toursListing;
    }

    public void searchButtonPressed() {
        String text = input.get().toLowerCase().trim();

        toursListing.clear();
        boolean isEmpty = text.equals("");


        for (int i = 0; i < defaultToursListing.size(); i++) {
            if (isEmpty || defaultToursListing.get(i).getTourName().toLowerCase().contains(text)) {
                toursListing.add(defaultToursListing.get(i));
            }
        }


    }
}
