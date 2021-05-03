package TourProject.editTour;


import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class EditTourController implements Initializable, CallbackController {


    public Button cancelButton;
    public Button saveButton;
    public Label errorMessage;
    public TextField tourName;
    public TextArea tourDescription;
    public TextField startpunkt;
    public TextField endpunkt;
    public ProgressIndicator progress;
    private EditTourViewModel viewModel;

    public EditTourController()
    {
        System.out.println("TourProject.EditTourController created");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.setImplicitExit(false);
        System.out.println("TourProject.EditTourController init");
        viewModel = new EditTourViewModel();

        startpunkt.textProperty().bindBidirectional(viewModel.getTourStart());
        endpunkt.textProperty().bindBidirectional(viewModel.getTourEnd());

        saveButton.setDefaultButton(true);
        saveButton.setDisable(true);
        progress.setVisible(false);

        tourName.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(viewModel.isInvalidForm());
        });
        tourDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(viewModel.isInvalidForm());
        });

        tourName.textProperty().bindBidirectional(viewModel.getTourName());
        tourDescription.textProperty().bindBidirectional(viewModel.getTourDescription());
    }

    public void save(ActionEvent actionEvent) throws InterruptedException {
        saveButton.setDisable(true);
        errorMessage.setVisible(false);
        progress.setVisible(true);
        viewModel.saveChanges(this);
        /*if () {
            ((Stage) cancelButton.getScene().getWindow()).close();
        } else {
            errorMessage.setVisible(true);
        }

        progress.setVisible(false);
        saveButton.setDisable(false);*/

    }

    public void apiCallDone() {
        System.out.println("done");
    }

    public void cancel(ActionEvent actionEvent) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }
        });
    }

    public EditTourViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void callback(boolean saved) {
        if (saved) {
            cancel(null);
        } else {
            errorMessage.setVisible(true);
        }
        progress.setVisible(false);
        saveButton.setDisable(false);
    }
}
