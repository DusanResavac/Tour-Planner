
import TourProject.BusinessLayer.TourBusiness;
import TourProject.DataAccessLayer.Config;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import TourProject.Model.addTour.AddTourViewModel;
import TourProject.Model.editTour.EditTourViewModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TourViewModelTest {

    static Config c;
    AddTourViewModel addTourViewModel;
    EditTourViewModel editTourViewModel;
    Tour t1;
    Tour t2;
    Tour t3;

    @BeforeAll
    public static void setup() {
        c = Config.getInstance("src/test/config.json");
    }

    @BeforeEach
    public void setupBeforeEach() {
        addTourViewModel = new AddTourViewModel();
        addTourViewModel.setTourBusiness(new TourBusiness());
        editTourViewModel = new EditTourViewModel();
        editTourViewModel.setTourBusiness(new TourBusiness());
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
        // TODO: write tests
    }



}
