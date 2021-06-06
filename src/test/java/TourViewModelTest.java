
import TourProject.BusinessLayer.TourBusiness;
import TourProject.DataAccessLayer.Config;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;
import TourProject.Model.Tour.TourViewModel;
import TourProject.Model.addTour.AddTourViewModel;
import TourProject.Model.editTour.EditTourViewModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TourViewModelTest {

    static Config c;
    TourViewModel addTourVM;
    TourViewModel editTourVM;
    Tour t1;
    Tour t2;
    Tour t3;

    @BeforeAll
    public static void setup() {
        c = Config.getInstance("src/test/config.json");
    }

    @BeforeEach
    public void setupBeforeEach() {
        DatabaseLoader.getInstance().getDataAccessLayer().retrieveData(true).join();
        addTourVM = new AddTourViewModel();
        addTourVM.setTourBusiness(new TourBusiness());
        editTourVM = new EditTourViewModel();
        editTourVM.setTourBusiness(new TourBusiness());
        t1 = new Tour().builder()
                .setTourId(1)
                .setName("Tour 1: Österreich")
                .setDescription("Eine interessante Strecke")
                .setDistance(20.0)
                .setStart("Wien")
                .setEnd("Salzburg")
                .setImagePath("error").build();

        t2 = (Tour) t1.clone();
        t2.setTourId(2);

        t3 = (Tour) t1.clone();
        t3.setTourId(3);
    }

    @Test
    @DisplayName("Test update or insert tour | update")
    public void testUpdateOrInsertTour() {
        // ARRANGE
        editTourVM.setSelectedTour((Tour) t1.clone());
        editTourVM.tourName.set("Veränderter Name");
        editTourVM.tourDescription.set("Veränderte Beschreibung");

        var editTourVM2 = new EditTourViewModel();
        editTourVM2.setTourBusiness(new TourBusiness());
        t1.setTourId(100);
        editTourVM2.setSelectedTour(t1);
        editTourVM2.tourName.set("Veränderter Name");
        editTourVM2.tourDescription.set("Veränderte Beschreibung");

        // ACT
        Boolean success = editTourVM.updateOrInsertTour(false, false).join();
        Boolean success2 = editTourVM2.updateOrInsertTour(false, false).join();


        // ASSERT
        assertTrue(success);
        assertFalse(success2);
    }

    @Test
    @DisplayName("Test update or insert tour | update with image retrieval")
    public void testUpdateOrInsertTour3() {
        // ARRANGE
        t1.setTourId(1);
        editTourVM.setSelectedTour(t1);
        editTourVM.tourName.set("Veränderter Name");
        editTourVM.tourDescription.set("Veränderte Beschreibung");
        editTourVM.tourStart.set("Graz");
        editTourVM.tourEnd.set("Salzburg");

        var editTourVM2 = new EditTourViewModel();
        editTourVM2.setTourBusiness(new TourBusiness());
        editTourVM2.setSelectedTour(t1);
        editTourVM2.tourName.set("Veränderter Name");
        editTourVM2.tourStart.set("a");
        editTourVM2.tourEnd.set("b");

        // ACT
        Boolean success = editTourVM.updateOrInsertTour(false, false).join();
        Boolean success2 = editTourVM2.updateOrInsertTour(false, false).join();

        // ASSERT
        assertTrue(success);
        // API Exception - (Simulated with too short strings for start/endpoints)
        assertFalse(success2);
    }

    @Test
    @DisplayName("Test update or insert tour | insert")
    public void testUpdateOrInsertTour4() {
        // ARRANGE
        addTourVM.setSelectedTour(new Tour());
        addTourVM.tourName.set("Tour Name");
        addTourVM.tourDescription.set("Tour Beschreibung");
        addTourVM.tourStart.set("Groß-Enzersdorf");
        addTourVM.tourEnd.set("Nickelsdorf");

        var addTourVM2 = new AddTourViewModel();
        addTourVM2.setTourBusiness(new TourBusiness());
        addTourVM2.setSelectedTour(new Tour());
        addTourVM2.tourName.set("Tour Name");
        addTourVM2.tourDescription.set("Tour Beschreibung");

        // ACT
        Boolean success = addTourVM.updateOrInsertTour(true, false).join();
        Boolean success2 = addTourVM2.updateOrInsertTour(true, false).join();

        // ASSERT
        assertTrue(success);
        assertFalse(success2);
    }


    @Test
    @DisplayName("Test insert tour with generated description | should fail - wrong OpenAI api key")
    public void testUpdateOrInsertTour5() {
        // ARRANGE
        addTourVM.setSelectedTour(new Tour());
        addTourVM.tourName.set("Tour Name");
        addTourVM.tourDescription.set("Tour Beschreibung");
        addTourVM.tourStart.set("Groß-Enzersdorf");
        addTourVM.tourEnd.set("Nickelsdorf");
        addTourVM.openAICheckbox.set(true);

        // ACT
        Boolean success = addTourVM.updateOrInsertTour(true, false).join();


        // ASSERT
        assertFalse(success);
    }

}
