package TourProject.Model.MainWindow;

import TourProject.BusinessLayer.Log4J;
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
import TourProject.Model.editTourLog.TourLogDatetime;
import TourProject.Model.editTourLog.TourLogDatetimeVM;
import TourProject.Model.editTourLog.TourLogDuration;
import TourProject.Model.editTourLog.TourLogDurationVM;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        toursListing.clear();
        tourLogs.clear();
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
        toursListing.addAll(mainBusiness.search(input.get(), mainBusiness.getTours()));
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
        if (selectedItem.getStart() != null && selectedItem.getEnd() != null && selectedItem.getDistance() != null) {
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete tour \"" + temp.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.NO) {
            return;
        }

        CustomDialogController dialog = new CustomDialogController("Removing tour", "A tour is currently being removed. Please wait till the process is finished.", false);
        dialog.showProcess(true);

        // start tour removal
        var thread = new Thread(() -> {
            mainBusiness.removeTour(temp)
                    .handle((tourRemoved, error) -> {
                        dialog.showProcess(false);
                        // if an error occurred, display an error message

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

                                // MainBusiness Tours aktualisieren
                                mainBusiness.getTours().clear();
                                mainBusiness.getTours().addAll(toursListing);
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

                        // if an error occurred, display an error message
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

                                // mainBusiness Tours aktualisieren
                                mainBusiness.getTours().clear();
                                mainBusiness.getTours().addAll(toursListing);
                            }
                        });

                        return null;
                    });
        });
        thread.start();
        dialog.show();
    }

    public void editTourLog() {
        CustomDialogController dialog = new CustomDialogController(
                "Edit TourLog Information",
                "Editing a tourlog can be achieved by clicking on a cell after selecting a tourlog.",
                true);
        dialog.showAndWait();
    }


    public void setTourStartEnd(String start, String end, Double distance) {
        tourStartEnd.set(start + " - " + end + " (" + (Math.round(distance * 10.0) / 10.0) + " km)");
    }

    public void editTourLogDate() {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../editTourLog/tourLogDatetime.fxml"));

        var viewmodel = new TourLogDatetimeVM();
        viewmodel.setTourLogBusiness(new TourLogBusiness());
        viewmodel.setTourLog((TourLog) selectedTourLog.get(0).clone());
        viewmodel.addSubscriber(this);

        var tourLogController = new TourLogDatetime(viewmodel);
        loader.setController(tourLogController);

        try {
            secondStage.setTitle("Edit Date and Time");
            secondStage.setScene(new Scene(loader.load()));
            secondStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editTourLogDuration() {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../editTourLog/tourLogDuration.fxml"));

        var viewmodel = new TourLogDurationVM();
        viewmodel.setTourLogBusiness(new TourLogBusiness());
        viewmodel.setTourLog((TourLog) selectedTourLog.get(0).clone());
        viewmodel.addSubscriber(this);

        var tourLogController = new TourLogDuration(viewmodel);
        loader.setController(tourLogController);

        try {
            secondStage.setTitle("Edit Duration");
            secondStage.setScene(new Scene(loader.load()));
            secondStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAddedTour(Tour tour) {
        toursListing.add(tour);

        mainBusiness.getTours().clear();
        mainBusiness.getTours().addAll(toursListing);
    }

    @Override
    public void updateAddedTourLog(TourLog tourLog) {
        // TODO: Neuladen oder Anpassen?
        for (Tour tour : toursListing) {
            if (tour.getTourId().equals(tourLog.getTourId())) {
                tour.getTourLogs().add(tourLog);
                break;
            }
        }
        if (selectedTour.size() > 0 && selectedTour.get(0).getTourId().equals(tourLog.getTourId())) {
            tourLogs.add(tourLog);
        }

        mainBusiness.getTours().clear();
        mainBusiness.getTours().addAll(toursListing);
    }

    @Override
    public Tour updateEditedTour(Tour tour) {
        // TODO: Neuladen oder Anpassen
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

                mainBusiness.getTours().clear();
                mainBusiness.getTours().addAll(toursListing);
                return builtTour;
            }
        }

        mainBusiness.getTours().clear();
        mainBusiness.getTours().addAll(toursListing);
        return null;
    }

    @Override
    public void updateEditedTourLogDatetime(TourLog tourLog) {
       /*
       TODO: Entscheiden ob neugeladen oder angepasst werden soll
       Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupToursListing();
            }
        });*/
        updateEditedTour(tourLog, "datetime");
    }

    @Override
    public void updateEditedTourLogDuration(TourLog tourLog) {
       /*
       TODO: Entscheiden ob neugeladen oder angepasst werden soll
       Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupToursListing();
            }
        });*/

        updateEditedTour(tourLog, "duration");
    }

    /**
     * Vereinfacht das Updaten von einzelnen TourLog Werten in der TourListing und tourLogs Tableview,
     * indem über Reflection die richtigen Methoden aufgerufen werden
     *
     * @param tourLog       der Tourlog mit den neuen Informationen
     * @param attributeName der Attributname des TourLogs bestimmt welches Attribut verändert werden soll
     */
    public void updateEditedTour(TourLog tourLog, String attributeName) {
        final Map<String, AttributeMapper> attributeMapper = Map.ofEntries(
                Map.entry("datetime", new AttributeMapper("getDatetime", "setDatetime", Date.class)),
                Map.entry("report", new AttributeMapper("getReport", "setReport", String.class)),
                Map.entry("distance", new AttributeMapper("getDistance", "setDistance", Double.class)),
                Map.entry("duration", new AttributeMapper("getDuration", "setDuration", Integer.class)),
                Map.entry("rating", new AttributeMapper("getRating", "setRating", Integer.class)),
                Map.entry("maxIncline", new AttributeMapper("getMaxIncline", "setMaxIncline", Double.class)),
                Map.entry("averageSpeed", new AttributeMapper("getAverageSpeed", "setAverageSpeed", Double.class)),
                Map.entry("topSpeed", new AttributeMapper("getTopSpeed", "setTopSpeed", Double.class)),
                Map.entry("weather", new AttributeMapper("getWeather", "setWeather", String.class)),
                Map.entry("numberOfBreaks", new AttributeMapper("getNumberOfBreaks", "setNumberOfBreaks", Integer.class))
        );
        if (attributeMapper.get(attributeName) == null) {
            System.err.println("Attribute unknown: " + attributeName);
            return;
        }
        try {
            AttributeMapper attributeMapped = attributeMapper.get(attributeName);

            Method methodGet = tourLog.getClass().getMethod(attributeMapped.getGetAttribute());
            Method methodSet = tourLog.getClass().getMethod(attributeMapped.getSetAttribute(), attributeMapped.getAttributeClass());

            for (int i = 0; i < toursListing.size(); i++) {
                Tour t = toursListing.get(i);
                if (t.getTourId().equals(tourLog.getTourId())) {
                    // Wenn's die richtige Tour ist, hol das richtige log
                    for (int b = 0; b < t.getTourLogs().size(); b++) {
                        TourLog tl = t.getTourLogs().get(b);
                        if (tl.getId().equals(tourLog.getId())) {
                            methodSet.invoke(tl, methodGet.invoke(tourLog));
                            /*tl.setDuration(tourLog.getDuration());*/
                            break;
                        }
                    }
                    break;
                }
            }

            // TourLogs müssen geupdated werden
            for (int i = 0; i < tourLogs.size(); i++) {
                TourLog tl = tourLogs.get(i);
                if (tl.getId().equals(tourLog.getId())) {
                    methodSet.invoke(tl, methodGet.invoke(tourLog));
                    /*tl.setDuration(tourLog.getDuration());*/
                    tourLogs.set(i, tl);
                    break;
                }
            }

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        mainBusiness.getTours().clear();
        mainBusiness.getTours().addAll(toursListing);
    }

    public void saveTourLogChanges(TourLog t, String attribute) {
        TourLogBusiness b = new TourLogBusiness();
        b.updateTourLog(t)
                .handle((tourLog, error) -> {
                    if (tourLog != null && error == null) {
                        // TODO: Anpassen oder neuladen?
                        updateEditedTour(tourLog, attribute);
                    }
                    return null;
                });
    }

    public void exportToursPDF() {
        mainBusiness.exportToursPDF(toursListing);
    }

    public void exportTourPDF() {
        if (selectedTour.size() <= 0) {
            CustomDialogController dialog = new CustomDialogController("Pdf creation notice", "Please select a tour before trying to generate a tour report.", true);
            dialog.showAndWait();
            return;
        }
        mainBusiness.exportTourPDF(selectedTour.get(0));
    }

    public void importToursJSON(Window w) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JSON File with tours");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showOpenDialog(w);
        CustomDialogController dialog = new CustomDialogController("Importing tours", "Tours are being imported. Please wait till the process is finished.", false);
        dialog.showProcess(true);

        if (file != null) {
            Gson gson = new Gson();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete existing tours?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            try (Reader reader = new FileReader(file)) {

                ArrayList<Tour> toursImport = gson.fromJson(reader, new TypeToken<List<Tour>>() {}.getType());

                mainBusiness.importToursJSON(toursImport, alert.getResult() == ButtonType.YES)
                        .whenComplete((s, ex) -> {
                            Platform.runLater(() -> {

                                dialog.showProcess(false);
                                if (s == null || !s || ex != null) {

                                    dialog.setResult(Boolean.TRUE);
                                    dialog.close();
                                    var dialog2 = new CustomDialogController(
                                            "Import tours error",
                                            "An error occurred while importing tours. Please restart the application and try again.",
                                            true);
                                    dialog2.showAndWait();

                                }

                                setupToursListing();
                            });
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log4J.logger.warn("Could not read selected file");
        }

        dialog.show();
    }

    public void exportToursJSON(ActionEvent event) {
        if (!mainBusiness.exportToursJSON(event)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    CustomDialogController dialog = new CustomDialogController("Tours JSON export error", "Could not export tours in JSON format.", true);
                    dialog.showAndWait();
                }
            });
        }
    }
}
