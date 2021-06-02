package TourProject.Model.addTour;

import TourProject.Model.Tour.TourController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTourController extends TourController implements Initializable {
    private AddTourViewModel viewModel;

    public AddTourController(AddTourViewModel viewModel) {
        System.out.println("TourProject.AddTourController created");
        this.viewModel = viewModel;
        super.setViewModel(viewModel);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.setImplicitExit(false);
        System.out.println("TourProject.AddTourController init");
        super.setup();
        viewModel.checkFormValidity();
        startpunkt.textProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.checkFormValidity();
        });
        endpunkt.textProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.checkFormValidity();
        });
        super.setRouteMessage("Strecke");
    }

    @Override
    public AddTourViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(AddTourViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    public void save(ActionEvent actionEvent) {
        viewModel.saveChanges()
                .whenComplete((success, error) -> {
                    if (success) {
                        cancel(null);
                    }
                });
    }


    public void cancel(ActionEvent actionEvent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }
        });
    }
}
