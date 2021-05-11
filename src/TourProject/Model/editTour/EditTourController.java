package TourProject.Model.editTour;


import TourProject.Model.Tour.TourController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTourController extends TourController implements Initializable {

    private EditTourViewModel viewModel;

    public EditTourController(EditTourViewModel viewModel) {
        System.out.println("TourProject.EditTourController created");
        this.viewModel = viewModel;
        super.setViewModel(viewModel);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.setImplicitExit(false);
        System.out.println("TourProject.EditTourController init");
        super.setup();
        super.setRouteMessage("Strecke - ACHTUNG: Beides leer lassen, wenn es nicht geändert werden soll");
    }

    @Override
    public EditTourViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(EditTourViewModel viewModel) {
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
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }
        });
    }
}
