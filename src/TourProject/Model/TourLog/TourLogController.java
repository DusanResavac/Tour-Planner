package TourProject.Model.TourLog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public abstract class TourLogController {

    public Button cancelButton;
    public DatePicker date;
    public TextField report;
    public TextField distance;
    public TextField duration;
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
    public ProgressIndicator progress;

    public void save() {

    }

    public abstract void save(ActionEvent actionEvent);

    public void cancel(ActionEvent actionEvent) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }
        });
    }
}
