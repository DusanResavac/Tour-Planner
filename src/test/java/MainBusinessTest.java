import TourProject.BusinessLayer.MainBusiness;
import TourProject.DataAccessLayer.Config;
import TourProject.Model.Tour.Tour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainBusinessTest {
    static Config c;
    static MainBusiness business;


    @BeforeAll
    public static void setup() {
        c = Config.getInstance("src/test/config.json");

        business = new MainBusiness();
        try {
            File folder = new File("./testPDFs");

            File[] files = folder.listFiles();
            if(files != null) { //some JVMs return null for empty dirs
                for(File f: files) {
                    if(!f.isDirectory()) {
                        f.delete();
                    }
                }
            }
            folder.delete();

            Files.createDirectories(Path.of("./testPDFs/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setupBeforeEach() {
        business.retrieveTours().join();
    }

    @Test
    @DisplayName("Test export Tours-Summary")
    public void testExportToursSummary2() {
        // ARRANGE
        List<Tour> tours = business.getTours();

        // ACT
        String filePath = business.exportToursPDF(tours, "./testPDFs/sum.pdf").join();
        String filePath2 = business.exportToursPDF(null, "./testPDFs/sum.pdf").join();
        String filePath3 = business.exportToursPDF(tours, null).join();

        // ASSERT
        assertEquals(filePath, "./testPDFs/sum.pdf");
        assertNull(filePath2);
        assertNull(filePath3);
    }


    @Test
    @DisplayName("Test export Tour-Information as PDF")
    public void testExportTourPDF() {
        // ARRANGE
        List<Tour> tours = business.getTours();
        var b = business.getDataAccessLayer();

        // ACT
        String filePath = business.exportTourPDF(null, "./testPDFs/sum.pdf").join();
        Tour temp = tours.get(0);
        temp.setImagePath("https://helpx.adobe.com/content/dam/help/en/photoshop/using/convert-color-image-black-white/jcr_content/main-pars/before_and_after/image-before/Landscape-Color.jpg");
        String filePath2 = business.exportTourPDF(temp, "./testPDFs/tour_" + temp.getTourId() + "-report.pdf").join();
        String filePath3 = business.exportTourPDF(temp, null).join();

        // ASSERT
        assertNull(filePath);
        assertEquals("./testPDFs/tour_" + temp.getTourId() + "-report.pdf", filePath2);
        assertNull(filePath3);
    }

    @Test
    @DisplayName("Test import tours from JSON")
    public void testImportToursJSON() {
        // ARRANGE
        List<Tour> tours = new ArrayList<>();
        List<Tour> tours2 = new ArrayList<>();
        List<Tour> tours3 = new ArrayList<>();
        Tour tour = new Tour().builder().build();
        Tour tour2 = new Tour().builder().setTourId(10).build();

        tours.add(business.getTours().get(0));
        tours2.add(tour);
        tours3.add(tour2);


        // ACT
        Boolean success = business.importToursJSON(tours, false).join();
        Boolean success2 = business.importToursJSON(tours2, false).join();
        Boolean success3 = business.importToursJSON(tours3, false).join();
        Boolean success4 = business.importToursJSON(tours3, true).join();
        Boolean success5 = business.importToursJSON(tours, true).join();

        // ASSERT
        assertTrue(success);
        // Tour hat keine ID
        assertFalse(success2);
        assertTrue(success3);
        assertTrue(success4);
        assertTrue(success5);
    }
}
