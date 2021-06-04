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

public class TourLogDuration implements Initializable {
    public ComboBox<Integer> tourLogDuration;
    @Getter @Setter
    public TourLogDurationVM viewmodel;
    public Button saveButton;
    public Button cancelButton;
    public ProgressIndicator progress;

    public TourLogDuration(TourLogDurationVM viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> duration = FXCollections.observableArrayList();

        // Dauer in Minuten bis 99 Stunden im Zehnertakt hinzufügen
        for (int i = 0; i < 99*60; i+=10) {
            duration.add(i);
        }

        // Für die Anzeige der Items in der Auswahlliste
        var durationFactory = new Callback<ListView<Integer>, ListCell<Integer>>() {
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

        tourLogDuration.valueProperty().bindBidirectional(viewmodel.getDuration());
        tourLogDuration.setCellFactory(durationFactory);
        tourLogDuration.setItems(duration);
        // Für die Darstellung des ausgewählten Items
        tourLogDuration.setConverter(new StringConverter<Integer>() {
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
