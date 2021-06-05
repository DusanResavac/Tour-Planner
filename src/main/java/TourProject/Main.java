package TourProject;

import TourProject.DataAccessLayer.Config;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Config config = Config.getInstance();

        // fxml created with SceneBuilder
        Parent root = FXMLLoader.load(getClass().getResource("Model/MainWindow/mainWindow.fxml"));
        System.out.println("fxml loaded");

        // bootstrap "window" named stage
        primaryStage.setTitle("Tour Planner");
        System.out.println("set title");

        // set scene into stage in defined size
        primaryStage.setScene(new Scene(root, 600, 500));
        System.out.println("set scene");

        // let's go
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


        System.out.println("show stage");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
