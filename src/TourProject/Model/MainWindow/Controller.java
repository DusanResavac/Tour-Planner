package TourProject.Model.MainWindow;

import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
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
    public TableColumn tourDate;
    public TableColumn<TourLog, String> tourDuration;
    public TableColumn tourDistance;
    public TableColumn tourAvgSpeed;
    public Label tourDescription;
    public ImageView tourImage;
    public ScrollPane tourImageScrollPane;
    public Pane tourImageTab;

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

        int seconds = duration;
        result.append(seconds).append("s");
        return result.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Controller init");

        tournameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tourDate.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        tourDuration.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(formatDurationToString(cellData.getValue().getDuration()));
        });
        tourDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tourAvgSpeed.setCellValueFactory(new PropertyValueFactory<>("averageSpeed"));


        var selectionModel = toursListing.getSelectionModel();
        ObservableList<Tour> selectedItems = selectionModel.getSelectedItems();
        tourImageTab.minHeightProperty().bind(tourImageScrollPane.heightProperty());
        selectedItems.addListener(new ListChangeListener<Tour>() {
            @Override
            public void onChanged(Change<? extends Tour> change) {
                System.out.println("Selection changed: " + change.getList());
                if (change.getList().size() == 1) {
                    viewModel.setSelectedTour((Tour) change.getList().get(0));
                    tourLogs.setItems(viewModel.getTourLogs());
                    var selectedTour = viewModel.getSelectedTour().size() > 0 ? viewModel.getSelectedTour().get(0) : null;
                    if (selectedTour != null && selectedTour.getImagePath() != null) {
                        System.out.println("Change image");
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
                    }
                }
            }
        });
/*
        toursListing.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                var b= toursListing.getSelectionModel();
                viewModel.setSelectedTour((Tour) toursListing.getSelectionModel().getSelectedItem());
                //tourImage.setImage(new Image(tours));

            }
        });*/

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

    public void printText(MouseEvent mouseEvent) {
        System.out.println("Clicked Label");
        System.out.println(mouseEvent);
        System.out.println(mouseEvent.getButton());
    }

    public void search (ActionEvent actionEvent) {
        viewModel.searchButtonPressed();
        toursListing.setItems(viewModel.getToursListing());
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

}
