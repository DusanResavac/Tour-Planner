package TourProject.editTour;


import TourProject.MainViewModel;
import TourProject.model.Tour;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTourController implements Initializable {


    public Button cancelButton;
    public TextField tourName;
    public TextArea tourDescription;
    public TextField startpunkt;
    public TextField endpunkt;
    private EditTourViewModel viewModel;

    public EditTourController()
    {
        System.out.println("TourProject.EditTourController created");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("TourProject.EditTourController init");
        viewModel = new EditTourViewModel();

        tourName.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });

        tourName.textProperty().bindBidirectional(viewModel.getTourName());
        tourDescription.textProperty().bindBidirectional(viewModel.getTourDescription());
    }

    public void save(ActionEvent actionEvent) {
        if (viewModel.saveChanges()) {
            ((Stage) cancelButton.getScene().getWindow()).close();
        }
    }

    public void cancel(ActionEvent actionEvent) {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }

    public EditTourViewModel getViewModel() {
        return viewModel;
    }
}
