import TourProject.BusinessLayer.TourBusiness;
import TourProject.DataAccessLayer.Config;
import TourProject.Model.Tour.Tour;
import TourProject.Model.api.TourInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TourBusinessTest {
    static Config c;
    static TourBusiness business;


    @BeforeAll
    public static void setup() {
        c = Config.getInstance("src/test/config.json");

        business = new TourBusiness();

    }

    @BeforeEach
    public void setupBeforeEach() {
        try {
            File folder = new File("./testImages");

            File[] files = folder.listFiles();
            if(files != null) { //some JVMs return null for empty dirs
                for(File f: files) {
                    if(!f.isDirectory()) {
                        f.delete();
                    }
                }
            }
            folder.delete();

            Files.createDirectories(Path.of("./testImages/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test insert tour")
    public void testInsertTour() {
        // ARRANGE
        Tour t = new Tour().builder()
                .setTourId(1)
                .setDescription("description")
                .setStart("Wien")
                .setEnd("Graz").build();
        Tour t2 = new Tour().builder()
                .setTourId(2)
                .setDescription("")
                .setStart("Tirol")
                .setEnd("Bregenz").build();

        // ACT
        Tour tRes = business.insertTour(t).join();
        Tour t2Res = business.insertTour(t2).join();


        // ASSERT
        assertNotNull(tRes);
        assertNotNull(t2Res);
        assertEquals(25, tRes.getDistance());
        assertEquals(25, t2Res.getDistance());
        // Image Files were created
        assertEquals(2, Objects.requireNonNull(new File("./testImages").list()).length);
    }

    @Test
    @DisplayName("Test retrieve and save route image")
    public void testRetrieveAndSaveRouteImage() {
        // ARRANGE
        Tour t = new Tour().builder()
                .setTourId(1)
                .setDescription("description")
                .setStart("Wiener Wald")
                .setEnd("Wiener Prater").build();
        Tour t2 = new Tour().builder()
                .setTourId(2)
                .setDescription("")
                .setStart("Wien")
                .setEnd("Schwechat").build();

        TourInformation tourInformation = new TourInformation();
        tourInformation.setTourId(1);
        tourInformation.setDistance(15.0);
        tourInformation.setSessionId((long) (Math.random() * 1_000_000_000) + "");
        tourInformation.setImagePath("Wiener Wald_to_Wiener Prater.jpg");

        TourInformation tourInformation2 = new TourInformation();
        tourInformation2.setTourId(2);
        tourInformation2.setDistance(10.0);
        tourInformation2.setSessionId((long) (Math.random() * 1_000_000_000) + "");
        tourInformation2.setImagePath("Wien_to_Schwechat.jpg");

        // ACT
        Tour tRes = business.retrieveAndSaveRouteImage(tourInformation, t).join();
        Tour t2Res = business.retrieveAndSaveRouteImage(tourInformation2, t2).join();


        // ASSERT
        // Bildpfade müssen gesetzt sein
        assertNotNull(tRes.getImagePath());
        assertNotNull(t2Res.getImagePath());
        assertEquals(2, Objects.requireNonNull(new File("./testImages").list()).length);
    }

    @Test
    @DisplayName("Test update tour")
    public void testUpdateTour() {
        // ARRANGE
        Tour t = new Tour().builder()
                .setTourId(1)
                .setDescription("description")
                .setStart("Wien")
                .setEnd("Graz").build();
        Tour t2 = new Tour().builder()
                .setTourId(2)
                .setDescription("")
                .setStart("Tirol")
                .setEnd("Bregenz").build();

        // ACT
        Tour tRes = business.updateTour(t, true).join();
        Tour t2Res = business.updateTour(t2, false).join();


        // ASSERT
        assertNotNull(tRes);
        assertNotNull(t2Res);
        assertEquals(t, tRes);
        assertEquals(t2, t2Res);
    }
}
