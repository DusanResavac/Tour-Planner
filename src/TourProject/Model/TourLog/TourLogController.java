package TourProject.Model.TourLog;

import TourProject.Model.Tour.TourViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class TourLogController implements Initializable {

    public ScrollPane scrollPane;
    public AnchorPane anchorPane;
    public Button cancelButton;
    public DatePicker date;
    public TextField report;
    public TextField distance;
    public Label ratingLabel;
    public Slider ratingSlider;
    public Label maxInclineLabel;
    public Slider maxInclineSlider;
    public Label avgSpeedLabel;
    public Slider avgSpeedSlider;
    public Label topSpeedLabel;
    public Slider topSpeedSlider;
    public TextField weather;
    public Label breaksLabel;
    public Slider breaksSlider;
    public Button saveButton;
    public ComboBox<Integer> timestampStunden;
    public ComboBox<Integer> timestampMinuten;
    public ComboBox<Integer> dauerStunden;
    public ComboBox<Integer> dauerMinuten;
    public ProgressIndicator progress;
    private TourLogViewModel viewModel;

    public TourLogController(TourLogViewModel tourLogViewModel) {
        this.viewModel = tourLogViewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> stunden = FXCollections.observableArrayList();
        ObservableList<Integer> minuten = FXCollections.observableArrayList();
        ObservableList<Integer> stundenDauer = FXCollections.observableArrayList();
        ObservableList<Integer> minutenDauer = FXCollections.observableArrayList();
        for (int i = 0; i < 100; i++) {
            if (i < 24) {
                stunden.add(i);
            }
            if (i < 60) {
                minuten.add(i);
                minutenDauer.add(i);
            }
            stundenDauer.add(i);
        }

        timestampStunden.setItems(stunden);
        timestampMinuten.setItems(minuten);
        dauerStunden.setItems(stundenDauer);
        dauerMinuten.setItems(minutenDauer);

        var stundenFactory = new Callback<ListView<Integer>, ListCell<Integer>>() {
            @Override public ListCell<Integer> call(ListView<Integer> p) {
                return new ListCell<Integer>() {
                    @Override protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("");
                        } else {
                            setText(item < 10 ?
                                    "0" + item + " h" :
                                    item + " h");
                        }
                    }
                };
            }
        };
        var minutenFactory = new Callback<ListView<Integer>, ListCell<Integer>>() {
            @Override public ListCell<Integer> call(ListView<Integer> p) {
                return new ListCell<Integer>() {
                    @Override protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("");
                        } else {
                            setText(item < 10 ?
                                    "0" + item + " min" :
                                    item + " min");
                        }
                    }
                };
            }
        };

        timestampStunden.setCellFactory(stundenFactory);
        timestampMinuten.setCellFactory(minutenFactory);
        dauerStunden.setCellFactory(stundenFactory);
        dauerMinuten.setCellFactory(minutenFactory);
        timestampStunden.valueProperty().bindBidirectional(viewModel.getTimestampStunden());
        timestampMinuten.valueProperty().bindBidirectional(viewModel.getTimestampMinuten());
        dauerStunden.valueProperty().bindBidirectional(viewModel.getDauerStunden());
        dauerMinuten.valueProperty().bindBidirectional(viewModel.getDauerMinuten());

        date.valueProperty().bindBidirectional(viewModel.getDate());
        report.textProperty().bindBidirectional(viewModel.getReport());
        distance.textProperty().bindBidirectional(viewModel.getDistance());
        ratingSlider.valueProperty().bindBidirectional(viewModel.getRating());
        ratingSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                ratingLabel.setText(newValue.intValue() + "/10");
            }
        });
        maxInclineSlider.valueProperty().bindBidirectional(viewModel.getMaxIncline());
        maxInclineSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                maxInclineLabel.setText(newValue.intValue() + "%");
            }
        });
        avgSpeedSlider.valueProperty().bindBidirectional(viewModel.getAvgSpeed());
        avgSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                avgSpeedLabel.setText(newValue.intValue() + " km/h");
            }
        });
        topSpeedSlider.valueProperty().bindBidirectional(viewModel.getTopSpeed());
        topSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                topSpeedLabel.setText(newValue.intValue() + " km/h");
            }
        });
        weather.textProperty().bindBidirectional(viewModel.getWeather());
        breaksSlider.valueProperty().bindBidirectional(viewModel.getBreaks());
        breaksSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                breaksLabel.setText(newValue.intValue() + "");
            }
        });
        scrollPane.setFitToWidth(true);
    }

    public void save (ActionEvent actionEvent) {
        System.out.println(viewModel.getTimestampStunden().toString());
        /*viewModel.save();*/
    }

    public void cancel(ActionEvent actionEvent) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }
        });
    }

    public TourLogViewModel getViewModel() {
        return viewModel;
    }

    public void setSelectedTour(TourLog tourLog) {

    }
}
