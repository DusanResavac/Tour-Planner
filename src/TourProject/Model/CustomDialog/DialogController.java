package TourProject.Model.CustomDialog;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogController {

    public Button button;
    public ImageView warningImage;
    public Pane warningImagePane;
    public GridPane gridPane;
    public Label errorMessage;
    public ProgressIndicator progress;

    public void handleButtonPress(ActionEvent actionEvent) {
        button.getScene().getWindow().hide();
    }

    public void setup (boolean closeWithX) {
        showProcess(false);
        if (closeWithX) {
            Window window = button.getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
        }
    }

    public void showProcess(boolean show) {
        button.setDisable(show);
        progress.setVisible(show);
    }
}
