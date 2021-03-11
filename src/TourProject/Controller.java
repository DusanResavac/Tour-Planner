package TourProject;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // create custom viewmodel
    public MainViewModel viewModel = new MainViewModel();

    // add fx:id and use intelliJ to create field in controller
    public TextField inputSearch;
    public TableView toursListing;
    public TableColumn tournameColumn;

    public Controller()
    {
        System.out.println("Controller created");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Controller init");

        tournameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        toursListing.setItems(viewModel.getToursListing());

        inputSearch.textProperty().bindBidirectional(viewModel.inputProperty());
    }

    public void printText(MouseEvent mouseEvent) {
        System.out.println("Clicked Label");
        System.out.println(mouseEvent);
        System.out.println(mouseEvent.getButton());
    }

    public void search (ActionEvent actionEvent) {
        viewModel.searchButtonPressed();
        toursListing.setItems(viewModel.getToursListing());
    }
}
