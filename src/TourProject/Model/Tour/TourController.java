package TourProject.Model.Tour;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public abstract class TourController {

    public Button cancelButton;
    public Button saveButton;
    public Label errorMessage;
    public TextField tourName;
    public TextArea tourDescription;
    public TextField startpunkt;
    public TextField endpunkt;
    public ProgressIndicator progress;

    public Label routeMessage;
    TourViewModel viewModel;

    public void setup() {
        Platform.setImplicitExit(false);
        System.out.println("TourProject.TourController setup");

        startpunkt.textProperty().bindBidirectional(viewModel.getTourStart());
        endpunkt.textProperty().bindBidirectional(viewModel.getTourEnd());

        saveButton.setDefaultButton(true);
        saveButton.setDisable(true);
        progress.setVisible(false);

        errorMessage.visibleProperty().bindBidirectional(viewModel.getRouteErrorProperty());
        saveButton.disableProperty().bindBidirectional(viewModel.getInvalidFormProperty());
        progress.visibleProperty().bindBidirectional(viewModel.getIsBusyProperty());
        tourName.textProperty().bindBidirectional(viewModel.getTourName());
        tourDescription.textProperty().bindBidirectional(viewModel.getTourDescription());

        tourName.textProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println("TourName new value: " + newValue);

            viewModel.checkFormValidity();
        });
        tourDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println("TourDescription new value: " + newValue);
            viewModel.checkFormValidity();
        });
    }

    public void setViewModel (TourViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public TourViewModel getViewModel() {
        return viewModel;
    }

    public Label getRouteMessage() {
        return routeMessage;
    }

    public void setRouteMessage(String routeMessage) {
        this.routeMessage.setText(routeMessage);
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

    public TextField getTourName() {
        return tourName;
    }

    public void setTourName(TextField tourName) {
        this.tourName = tourName;
    }

    public TextArea getTourDescription() {
        return tourDescription;
    }

    public void setTourDescription(TextArea tourDescription) {
        this.tourDescription = tourDescription;
    }

    public TextField getStartpunkt() {
        return startpunkt;
    }

    public void setStartpunkt(TextField startpunkt) {
        this.startpunkt = startpunkt;
    }

    public TextField getEndpunkt() {
        return endpunkt;
    }

    public void setEndpunkt(TextField endpunkt) {
        this.endpunkt = endpunkt;
    }
}
