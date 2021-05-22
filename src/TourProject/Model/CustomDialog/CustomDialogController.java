package TourProject.Model.CustomDialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class CustomDialogController extends Dialog<Boolean> {

    private DialogController controller;

    public CustomDialogController(String title, String message, boolean closeWithX) {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dialog.fxml"));
            Parent root = loader.load();
            controller = loader.<DialogController>getController();
            controller.errorMessage.setText(message);
            setTitle(title);
            getDialogPane().getStylesheets().add(getClass().getResource("CustomDialogStyle.css").toExternalForm());
            getDialogPane().setContent(root);
            controller.setup(closeWithX);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showProcess(boolean show) {
        controller.showProcess(show);
    }
}
