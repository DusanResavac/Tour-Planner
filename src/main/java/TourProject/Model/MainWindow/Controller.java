package TourProject.Model.MainWindow;

import TourProject.BusinessLayer.Log4J;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // create custom viewmodel
    public MainViewModel viewModel = new MainViewModel();

    // add fx:id and use intelliJ to create field in controller
    public TextField inputSearch;
    public TableView toursListing;
    public TableColumn tournameColumn;
    public Label tourLabel;
    public TableView tourLogs;
    public TableColumn<TourLog, Date> tourDate;
    public TableColumn<TourLog, Integer> tourDuration;
    public TableColumn<TourLog, Double> tourDistance;
    public TableColumn<TourLog, String> tourReport;
    public TableColumn<TourLog, Double> tourAvgSpeed;
    public TableColumn<TourLog, Double> tourTopSpeed;
    public TableColumn<TourLog, Integer> tourRating;
    public TableColumn<TourLog, Double> tourMaxIncline;
    public TableColumn<TourLog, String> tourWeather;
    public TableColumn<TourLog, Integer> tourBreaks;

    public Label tourDescription;
    public ImageView tourImage;
    public ScrollPane tourImageScrollPane;
    public Pane tourImageTab;
    public Label tourStartEnd;
    public FlowPane flowPane;


    public Controller()
    {
        System.out.println("Controller created");
    }

    private String formatDurationToString (Integer duration) {
        StringBuilder result = new StringBuilder();

        double temp = (double) (duration / (60*60));
        int hours = 0;

        if (temp >= 1) {
            hours = (int) temp;
            duration -= hours * 60 * 60;
            result.append(hours).append("h ");
        }

        temp = (double) (duration / 60);
        int minutes = 0;

        if (temp >= 1) {
            minutes = (int) temp;
            duration -= minutes * 60;
            result.append(minutes).append("min ");
        }

        // Nicht notwendig, da nur minutengenaue Angaben gemacht werden können
        /*int seconds = duration;
        result.append(seconds).append("s");*/
        return result.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Controller init");
        tourLogs.setEditable(true);

        tournameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tourDate.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        tourDate.setCellFactory(column -> {
            TableCell<TourLog, Date> cell = new TableCell<TourLog, Date>() {
                private SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMMM, yyyy HH:mm");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(dateFormat.format(item));
                    }
                }
            };

            return cell;
        });
        tourDate.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<TourLog, Date>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<TourLog, Date> tourLogDateCellEditEvent) {
                viewModel.editTourLogDate();
            }
        });
        tourDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        tourDuration.setCellFactory(column -> {
            TableCell<TourLog, Integer> cell = new TableCell<TourLog, Integer>() {

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(formatDurationToString(item));
                    }
                }
            };
            return cell;
        });
        tourDuration.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<TourLog, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<TourLog, Integer> tourLogIntegerCellEditEvent) {
                viewModel.editTourLogDuration();
            }
        });
        tourDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tourDistance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tourDistance.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setDistance(event.getNewValue());
            viewModel.saveTourLogChanges(t, "distance");
        });
        tourReport.setCellValueFactory(new PropertyValueFactory<>("report"));
        tourReport.setCellFactory(TextFieldTableCell.forTableColumn());
        tourReport.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setReport(event.getNewValue());
            viewModel.saveTourLogChanges(t, "report");
        });
        tourAvgSpeed.setCellValueFactory(new PropertyValueFactory<>("averageSpeed"));
        tourAvgSpeed.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tourAvgSpeed.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setAverageSpeed(event.getNewValue());
            viewModel.saveTourLogChanges(t, "averageSpeed");
        });
        tourTopSpeed.setCellValueFactory(new PropertyValueFactory<>("topSpeed"));
        tourTopSpeed.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tourTopSpeed.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setTopSpeed(event.getNewValue());
            viewModel.saveTourLogChanges(t, "topSpeed");
        });
        tourRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        tourRating.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tourRating.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setRating(Math.min(Math.max(event.getNewValue(), 0), 10));
            viewModel.saveTourLogChanges(t, "rating");
        });
        tourMaxIncline.setCellValueFactory(new PropertyValueFactory<>("maxIncline"));
        tourMaxIncline.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tourMaxIncline.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setMaxIncline(event.getNewValue());
            viewModel.saveTourLogChanges(t, "maxIncline");
        });
        tourWeather.setCellValueFactory(new PropertyValueFactory<>("weather"));
        tourWeather.setCellFactory(TextFieldTableCell.forTableColumn());
        tourWeather.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setWeather(event.getNewValue());
            viewModel.saveTourLogChanges(t, "weather");
        });
        tourBreaks.setCellValueFactory(new PropertyValueFactory<>("numberOfBreaks"));
        tourBreaks.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tourBreaks.setOnEditCommit(event -> {
            TourLog t = createEmptyTourLog(event.getRowValue());
            t.setNumberOfBreaks(event.getNewValue());
            viewModel.saveTourLogChanges(t, "numberOfBreaks");
        });

        tourStartEnd.textProperty().bindBidirectional(viewModel.getTourStartEnd());
        flowPane.prefWrapLengthProperty().bind(tourImageScrollPane.widthProperty());

        ObservableList<Tour> selectedItems = toursListing.getSelectionModel().getSelectedItems();
        ObservableList<TourLog> selectedTourLogs = tourLogs.getSelectionModel().getSelectedItems();
        selectedTourLogs.addListener(new ListChangeListener<TourLog>() {
            @Override
            public void onChanged(Change<? extends TourLog> change) {
                tourLogListener(change);
            }
        });
        tourImageTab.minHeightProperty().bind(tourImageScrollPane.heightProperty());

        selectedItems.addListener(new ListChangeListener<Tour>() {
            @Override
            public void onChanged(Change<? extends Tour> change) {
                tourListener(change);
            }
        });

        viewModel.setupToursListing();

        toursListing.setItems(viewModel.getToursListing());
        tourLogs.setItems(viewModel.getTourLogs());



        /*
        TODO: add binding https://stackoverflow.com/questions/18607624/how-to-bind-a-javafx-label-to-the-selected-item-from-a-listview/20656273#20656273
        toursListing.getSelectionModel().selectedItemProperty().*/
        tourDescription.textProperty().bindBidirectional(viewModel.tourDescription());
        tourLabel.textProperty().bindBidirectional(viewModel.tourLabel());
        inputSearch.textProperty().bindBidirectional(viewModel.inputProperty());
    }

    private TourLog createEmptyTourLog (TourLog tourLog) {
        TourLog tl = new TourLog();
        tl.setId(tourLog.getId());
        tl.setTourId(tourLog.getTourId());
        return tl;
    }

    public void tourLogListener (ListChangeListener.Change<? extends TourLog> change) {
        Log4J.logger.info("Selection changed (TourLog): " + change.getList());
        viewModel.getSelectedTourLog().clear();
        if (change.getList().size() == 1) {
            viewModel.getSelectedTourLog().add(change.getList().get(0));
        }
        tourLogs.setItems(viewModel.getTourLogs());
    }

    public void tourListener (ListChangeListener.Change<? extends Tour> change) {
        Log4J.logger.info("Selection changed: " + change.getList());
        if (change.getList().size() == 1) {
            viewModel.setSelectedTour((Tour) change.getList().get(0));

            tourLogs.setItems(viewModel.getTourLogs());
            var selectedTour = viewModel.getSelectedTour().size() > 0 ? viewModel.getSelectedTour().get(0) : null;

            if (selectedTour != null) {

                if (selectedTour.getImagePath() != null) {
                    String path;
                    if (selectedTour.getImagePath().startsWith("http")) {
                        path = selectedTour.getImagePath();
                    } else {
                        path = "file:@../../" + selectedTour.getImagePath();
                    }

                    tourImage.setImage(new Image(path));
                    tourImage.fitWidthProperty().bind(tourImageTab.widthProperty());
                    tourImage.fitHeightProperty().bind(tourImageTab.heightProperty());
                    tourImageTab.minHeightProperty().bind(tourImageScrollPane.heightProperty());
                    tourImage.maxWidth(500);
                } else {
                    tourImage.setImage(null);
                }
            }
        } else {
            viewModel.setSelectedTour(null);
            tourImage.setImage(null);
            //tourStartEnd.setText("");
        }
    }

    public void search (ActionEvent actionEvent) {
        viewModel.searchButtonPressed();
        //toursListing.setItems(viewModel.getToursListing());
    }

    public void editTour(ActionEvent actionEvent) throws IOException {
        viewModel.editTour();
    }

    public void addTour(ActionEvent actionEvent) throws IOException {
        viewModel.addTour();
    }

    public void deleteTour(ActionEvent actionEvent) {
        viewModel.removeTour();
    }

    public void addTourLog(ActionEvent actionEvent) throws IOException {
        viewModel.addTourLog();
    }

    public void removeTourLog(ActionEvent actionEvent) throws IOException {
        viewModel.removeTourLog();
    }

    public void editTourLog(ActionEvent actionEvent) {
        viewModel.editTourLog();
    }

    public void exportToursPDF(ActionEvent actionEvent) {
        viewModel.exportToursPDF();
    }

    public void exportTourPDF(ActionEvent actionEvent) {
        viewModel.exportTourPDF();
    }

    public void importToursJSON(ActionEvent actionEvent) {
        viewModel.importToursJSON(inputSearch.getScene().getWindow());
    }

    public void exportToursJSON(ActionEvent actionEvent) {
        viewModel.exportToursJSON(actionEvent);
    }
}
