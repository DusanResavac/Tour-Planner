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
import lombok.Getter;

import java.io.IOException;
import java.util.List;

public class MainViewModel implements TourSubscriber, TourLogSubscriber {
    // http://openbook.rheinwerk-verlag.de/javainsel/12_004.html
    private final StringProperty input = new SimpleStringProperty("");
    @Getter
    private final StringProperty tourLabel = new SimpleStringProperty("");
    @Getter
    private final StringProperty tourDescription = new SimpleStringProperty("");
    @Getter
    private final StringProperty tourStartEnd = new SimpleStringProperty("");

    private final ObservableList<Tour> toursListing = FXCollections.observableArrayList();
    private final ObservableList<Tour> selectedTour = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<TourLog> selectedTourLog = FXCollections.observableArrayList();
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();
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
        tourLogs.clear();
        if (selectedItem == null) {
            mainBusiness.setSelectedTour(null);
            tourLabel.setValue("");
            tourDescription.setValue("");
            tourStartEnd.setValue("");
            selectedTour.clear();
            return;
        }
        if (selectedItem.getImagePath() != null) {
            setTourStartEnd(selectedItem.getStart(), selectedItem.getEnd(), selectedItem.getDistance());
        }

        mainBusiness.setSelectedTour(selectedItem);
        tourLabel.setValue(selectedItem.getName());
        tourDescription.setValue(selectedItem.getDescription());

        selectedTour.clear();
        if (selectedItem.getTourLogs() != null) {
            tourLogs.addAll(selectedItem.getTourLogs());
        }
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

                        if (error != null || tourRemoved == null || !tourRemoved) {
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
                                        if (selectedTour.get(0).getTourId().equals(toursListing.get(i).getTourId())) {
                                            selectedTour.clear();
                                        }
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
        editTourController.getViewModel().setSelectedTour((Tour) selectedTour.get(0).clone());
        //controller.setSelectedTour(selectedTour.get(0));
        editTourController.getViewModel().addSubscriber(this);
        secondStage.show();

    }

    @Override
    public Tour updateEditedTour(Tour tour) {
        for (int i = 0; i < toursListing.size(); i++) {
            Tour t = toursListing.get(i);
            if (t.getTourId().equals(tour.getTourId())) {
                Tour.TourBuilder temp = new Tour().builder();
                if (tour.getStart() != null && tour.getEnd() != null && tour.getImagePath() != null) {
                    temp
                            .setStart(tour.getStart())
                            .setEnd(tour.getEnd())
                            .setImagePath(tour.getImagePath())
                            .setDistance(tour.getDistance());
                } else {
                    temp
                            .setStart(t.getStart())
                            .setEnd(t.getEnd())
                            .setImagePath(t.getImagePath())
                            .setDistance(t.getDistance());
                }
                temp
                        .setTourId(t.getTourId())
                        .setDescription(tour.getDescription())
                        .setName(tour.getName())
                        .setTourLogs(tour.getTourLogs());

                Tour builtTour = temp.build();
                setTourStartEnd(builtTour.getStart(), builtTour.getEnd(), builtTour.getDistance());
                toursListing.set(i, builtTour);
                return builtTour;
            }
        }

        return null;
    }

    @Override
    public void updateAddedTour(Tour tour) {
        toursListing.add(tour);
    }

    public void addTourLog() throws IOException {
        if (selectedTour.size() == 0 || selectedTour.get(0) == null) {
            CustomDialogController dialog = new CustomDialogController("Add tourlog error", "No Tour selected. Please select a tour before trying to add a tourlog.", true);
            dialog.showAndWait();
            return;
        }
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../TourLog/TourLogWindow.fxml"));
        var tourLogViewModel = new TourLogViewModel();
        tourLogViewModel.setTourLogBusiness(new TourLogBusiness());
        tourLogViewModel.setSelectedTour(selectedTour.get(0));
        tourLogViewModel.addSubscriber(this);

        var tourLogController = new TourLogController(tourLogViewModel);
        loader.setController(tourLogController);


        secondStage.setTitle("Add Tour-Log");
        secondStage.setScene(new Scene(loader.load()));
        tourLogController.setSelectedTour(new TourLog());
        secondStage.show();
    }

    public void removeTourLog() {
        if (selectedTourLog.size() == 0 || selectedTourLog.get(0) == null) {
            CustomDialogController dialog = new CustomDialogController("Remove tourlog error", "No Tourlog selected. Please select a tourlog first.", true);
            dialog.showAndWait();
            return;
        }

        CustomDialogController dialog = new CustomDialogController("Removing tour", "A tour is currently being removed. Please wait till the process is finished.", false);
        dialog.showProcess(true);
        TourLog tempTourLog = (TourLog) selectedTourLog.get(0).clone();
        // start tour removal
        var thread = new Thread(() -> {
            mainBusiness.removeTourLog(tempTourLog)
                    .handle((tourLogRemoved, error) -> {
                        dialog.showProcess(false);

                        // if an error occured, display an error message
                        if (error != null || tourLogRemoved == null || !tourLogRemoved) {
                            if (error != null) {
                                error.printStackTrace();
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setResult(Boolean.TRUE);
                                    dialog.close();
                                    var dialog2 = new CustomDialogController(
                                            "Remove tourLog error",
                                            "The selected tourLog could not be deleted. Please restart the application and try again or check whether the database server is running.",
                                            true);
                                    dialog2.showAndWait();
                                }
                            });

                            return null;
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                // delete it from the tourLogs tableview and clear selection
                                for (int i = 0; i < tourLogs.size(); i++) {
                                    TourLog t = tourLogs.get(i);
                                    if (t.getId().equals(tempTourLog.getId())) {
                                        tourLogs.remove(i);
                                        break;
                                    }
                                }

                                // delete it from the selected tour, so that it doesn't appear when the tour is selected
                                // after switching to a different tour
                                for (int i = 0; i < toursListing.size(); i++) {
                                    Tour t = toursListing.get(i);
                                    for (int b = 0; b < t.getTourLogs().size(); b++) {
                                        TourLog tl = t.getTourLogs().get(b);
                                        if (tl.getId().equals(tempTourLog.getId())) {
                                            // if we found the tour with the corresponding log, delete it and refresh
                                            // the view by setting the tour without the deleted log
                                            List<TourLog> tlogs = t.getTourLogs();
                                            tlogs.remove(b);
                                            t.setTourLogs(tlogs);
                                            toursListing.set(i, t);

                                            return;
                                        }
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

    public void editTourLog() {

    }

    @Override
    public void updateEditedTourLog(TourLog tourLog) {

    }

    @Override
    public void updateAddedTourLog(TourLog tourLog) {
        for (Tour tour : toursListing) {
            if (tour.getTourId().equals(tourLog.getTourId())) {
                tour.getTourLogs().add(tourLog);
                break;
            }
        }
        if (selectedTour.size() > 0 && selectedTour.get(0).getTourId().equals(tourLog.getTourId())) {
            tourLogs.add(tourLog);
        }
    }

    public void setTourStartEnd (String start, String end, Double distance) {
        tourStartEnd.set(start + " - " + end + " (" + (Math.round(distance * 10.0) / 10.0) + " km)");
    }
}
