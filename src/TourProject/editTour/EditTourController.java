package TourProject.editTour;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTourController implements Initializable {


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
            viewModel.checkFormValidity();
        });
        tourDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.checkFormValidity();
        });

        errorMessage.visibleProperty().bindBidirectional(viewModel.getRouteErrorProperty());
        saveButton.disableProperty().bindBidirectional(viewModel.getInvalidFormProperty());
        progress.visibleProperty().bindBidirectional(viewModel.getIsBusyProperty());
        tourName.textProperty().bindBidirectional(viewModel.getTourName());
        tourDescription.textProperty().bindBidirectional(viewModel.getTourDescription());
    }

    public void save(ActionEvent actionEvent) throws InterruptedException {
        viewModel.saveChanges()
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

    public EditTourViewModel getViewModel() {
        return viewModel;
    }
}
