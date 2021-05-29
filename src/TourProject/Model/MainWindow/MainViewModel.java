package TourProject.Model.MainWindow;

import TourProject.BusinessLayer.TourBusiness;
import TourProject.BusinessLayer.TourLogBusiness;
import TourProject.Model.CustomDialog.CustomDialogController;
import TourProject.Model.TourLog.TourLogController;
import TourProject.Model.TourLog.TourLogSubscriber;
import TourProject.Model.TourLog.TourLogViewModel;
import TourProject.Model.addTour.AddTourController;
import TourProject.Model.addTour.AddTourViewModel;
import TourProject.BusinessLayer.MainBusiness;
import TourProject.Model.editTour.EditTourController;
import TourProject.Model.Tour.TourSubscriber;
import TourProject.Model.editTour.EditTourViewModel;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewModel implements TourSubscriber, TourLogSubscriber {
    // http://openbook.rheinwerk-verlag.de/javainsel/12_004.html
    private final StringProperty input = new SimpleStringProperty("");
    private final StringProperty tourLabel = new SimpleStringProperty("");
    private final StringProperty tourDescription = new SimpleStringProperty("");

    private final ObservableList<Tour> toursListing = FXCollections.observableArrayList();
    private final ObservableList<Tour> selectedTour = FXCollections.observableArrayList();
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();
    private boolean currentlyRemovingTour = false;
    private final MainBusiness mainBusiness;

    public MainViewModel() {
        mainBusiness = new MainBusiness();
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
        mainBusiness.retrieveTours().thenApplyAsync(tours -> {
            toursListing.addAll(mainBusiness.getTours());
            return null;
        });
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

    public void addTour() throws IOException {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Tour/TourWindow.fxml"));
        var addTourViewModel = new AddTourViewModel();
        addTourViewModel.setTourBusiness(new TourBusiness());
        var addTourController = new AddTourController(addTourViewModel);
        loader.setController(addTourController);


        secondStage.setTitle("Add Tour");
        secondStage.setScene(new Scene(loader.load()));
        addTourController.getViewModel().setSelectedTour(new Tour());
        //controller.setSelectedTour(selectedTour.get(0));
        addTourController.getViewModel().addSubscriber(this);
        secondStage.show();
    }


    public void removeTour() {
        if (selectedTour.size() <= 0) {
            CustomDialogController dialog = new CustomDialogController("Delete tour error", "No Tour selected. Please select a tour before clicking the delete button!", true);
            dialog.showAndWait();
            return;
        }
        Tour temp = selectedTour.get(0);
        CustomDialogController dialog = new CustomDialogController("Removing tour", "A tour is currently being removed. Please wait till the process is finished.", false);
        dialog.showProcess(true);

        // start tour removal
        var thread = new Thread(() -> {
            mainBusiness.removeTour(temp)
                    .handle((tourRemoved, error) -> {
                        dialog.showProcess(false);
                        // if an error occured, display an error message

                        if (!tourRemoved || error != null) {
                            if (error != null) {
                                error.printStackTrace();
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setResult(Boolean.TRUE);
                                    dialog.close();
                                    var dialog2 = new CustomDialogController(
                                            "Remove tour error",
                                            "The selected tour could not be deleted. Please restart the application and try again or check if the database server is running.",
                                            true);
                                    dialog2.showAndWait();
                                }
                            });

                            return null;
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < toursListing.size(); i++) {
                                    if (toursListing.get(i).getTourId().equals(temp.getTourId())) {
                                        toursListing.remove(i);
                                        break;
                                    }
                                }
                            }
                        });

                        return null;
                    });
        });
        thread.start();
        dialog.show();
    }

    public void editTour() throws IOException {
        if (selectedTour.size() <= 0) {
            CustomDialogController dialog = new CustomDialogController("Edit tour error", "No Tour selected. Please select a tour before clicking the edit button!", true);
            dialog.showAndWait();
            return;
        }
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Tour/TourWindow.fxml"));
        var editTourViewModel = new EditTourViewModel();
        editTourViewModel.setTourBusiness(new TourBusiness());
        EditTourController editTourController = new EditTourController(editTourViewModel);
        loader.setController(editTourController);


        secondStage.setTitle("Edit " + selectedTour.get(0).getName());
        secondStage.setScene(new Scene(loader.load()));
        editTourController.getViewModel().setSelectedTour(selectedTour.get(0));
        //controller.setSelectedTour(selectedTour.get(0));
        editTourController.getViewModel().addSubscriber(this);
        secondStage.show();

    }

    @Override
    public void updateEditedTour(Tour tour) {
        for (int i = 0; i < toursListing.size(); i++) {
            if (toursListing.get(i).getTourId().equals(tour.getTourId())) {
                toursListing.set(i, tour);
                setSelectedTour(toursListing.get(i));
                break;
            }
        }
    }

    @Override
    public void updateAddedTour(Tour tour) {
        toursListing.add(tour);
    }

    public void addTourLog() throws IOException {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../TourLog/TourLogWindow.fxml"));
        var tourLogViewModel = new TourLogViewModel();
        tourLogViewModel.setTourLogBusiness(new TourLogBusiness());

        var tourLogController = new TourLogController(tourLogViewModel);
        loader.setController(tourLogController);


        secondStage.setTitle("Add Tour-Log");
        secondStage.setScene(new Scene(loader.load()));
        tourLogController.setSelectedTour(new TourLog());
        tourLogController.getViewModel().setSelectedTour(new TourLog());
        //controller.setSelectedTour(selectedTour.get(0));
        tourLogController.getViewModel().addSubscriber(this);
        secondStage.show();
    }

    public void removeTourLog() {

    }

    public void editTourLog() {

    }

    @Override
    public void updateEditedTourLog(TourLog tourLog) {

    }

    @Override
    public void updateAddedTourLog(TourLog tourLog) {

    }
}
