package TourProject;

import TourProject.business.MainBusiness;
import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.editTour.EditTourController;
import TourProject.editTour.EditTourSubscriber;
import TourProject.model.Tour;
import TourProject.model.TourLog;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewModel implements EditTourSubscriber {
    // http://openbook.rheinwerk-verlag.de/javainsel/12_004.html
    private final StringProperty input = new SimpleStringProperty("");
    private final StringProperty tourLabel = new SimpleStringProperty("");
    private final StringProperty tourDescription = new SimpleStringProperty("");

    private final ObservableList<Tour> toursListing = FXCollections.observableArrayList();
    private final ObservableList<Tour> selectedTour = FXCollections.observableArrayList();
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();
    private final MainBusiness mainBusiness;

    public MainViewModel(DataAccessLayer dal) {
        mainBusiness = new MainBusiness(dal);
    }

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
        toursListing.addAll(mainBusiness.getTours());
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
        toursListing.addAll(mainBusiness.search(input.get()));
    }

    public void setSelectedTour(Tour selectedItem) {
        if (selectedItem == null) {
            return;
        }
        mainBusiness.setSelectedTour(selectedItem);
        tourLabel.setValue(selectedItem.getName());
        tourDescription.setValue(selectedItem.getDescription());

        tourLogs.clear();
        if (selectedItem.getTourLogs() != null) {
            tourLogs.addAll(selectedItem.getTourLogs());
        }
        selectedTour.clear();
        selectedTour.add(selectedItem);
    }

    public void editTour() throws IOException {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("editTour/editTourWindow.fxml"));


        secondStage.setTitle("Edit " + selectedTour.get(0).getName());
        secondStage.setScene(new Scene(loader.load()));

        EditTourController controller =  loader.getController();
        controller.setSelectedTour(selectedTour.get(0));
        controller.addSubscriber(this);
        secondStage.show();

    }

    @Override
    public void update(Tour tour) {
        System.out.println("TOUR GEUPDATED: " + tour.toString());
    }
}
