package TourProject.Model.editTourLog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class TourLogDatetime implements Initializable {
    public DatePicker tourLogDatum;
    public ComboBox<Integer> tourLogUhrzeit;
    @Getter @Setter
    public TourLogDatetimeVM viewmodel;
    public Button saveButton;
    public Button cancelButton;
    public ProgressIndicator progress;

    public TourLogDatetime(TourLogDatetimeVM viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> uhrzeit = FXCollections.observableArrayList();

        // alle Uhrzeiten im 5er Takt hinzufügen
        for (int i = 0; i < 24*60; i+= 5) {
            uhrzeit.add(i);
        }

        // Für die Anzeige der Items in der Auswahlliste
        var uhrzeitFactory = new Callback<ListView<Integer>, ListCell<Integer>>() {
            @Override
            public ListCell<Integer> call(ListView<Integer> integerListView) {
                return new ListCell<Integer>() {
                    @Override protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("");
                        } else {
                            setText(minutesAsTimeString(item));
                        }
                    }
                };
            }
        };

        tourLogDatum.valueProperty().bindBidirectional(viewmodel.getDatum());

        tourLogUhrzeit.valueProperty().bindBidirectional(viewmodel.getUhrzeit());
        tourLogUhrzeit.setCellFactory(uhrzeitFactory);
        tourLogUhrzeit.setItems(uhrzeit);
        // Für die Darstellung des ausgewählten Items
        tourLogUhrzeit.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer integer) {
                return minutesAsTimeString(integer);
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });

        progress.visibleProperty().bindBidirectional(viewmodel.getBusy());
        saveButton.disableProperty().bindBidirectional(viewmodel.getBusy());
    }

    public void save(ActionEvent actionEvent) {
        viewmodel.saveChanges()
                .whenComplete((success, error) -> {
                    if (success) {
                        cancel(null);
                    }
                });
    }

    public void cancel(ActionEvent actionEvent) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }
        });
    }

    private String minutesAsTimeString(Integer integer) {
        int hours = integer / 60;
        int minutes = integer - hours * 60;

        String temp = hours < 10 ? "0" + hours + ":" : hours + ":";
        temp += minutes < 10 ? "0" + minutes : minutes + "";
        return temp;
    }
}
