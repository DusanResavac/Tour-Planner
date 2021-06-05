import TourProject.BusinessLayer.MainBusiness;
import TourProject.DataAccessLayer.Config;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchTest {

    static Config c;
    MainBusiness mainBusiness;
    Tour t1;

    Tour t2;

    Tour t3;

    @BeforeAll
    public static void setup() {
        c = Config.getInstance("src/test/config.json");
    }

    @BeforeEach
    public void setupBeforeEach() {
        mainBusiness = new MainBusiness();
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
    @DisplayName("Search by Tour-Name | expect 3")
    public void testSearchByTourName() {
        // ARRANGE
        t1.setName("Tour 1: Österreich");
        t2.setName("Tour 2: Deutschland");
        t3.setName("Tour 3: Schweiz");

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("Tour", tours);


        // ASSERT
        assertEquals(3, result.size());
        assertEquals(1, (int) result.get(0).getTourId());
        assertEquals(2, result.get(1).getTourId());
        assertEquals(3, result.get(2).getTourId());
    }

    @Test
    @DisplayName("Search by Tour-Name 2 | expect 1")
    public void testSearchByTourName2() {
        // ARRANGE
        t1.setName("Tour 1: Österreich");
        t2.setName("Tour 2: Deutschland");
        t3.setName("Tour 3: Schweiz");

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("Schweiz", tours);


        // ASSERT
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Search by Tour-Description | expect 1")
    public void testSearchByTourDescription() {
        // ARRANGE
        t1.setDescription("Eine interessante Strecke");
        t2.setDescription("Eine spannende Strecke");
        t3.setDescription("Eine Beschreibung");

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("spannend", tours);


        // ASSERT
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Search by Tour-Description 2 | expect 2")
    public void testSearchByTourDescription2() {
        // ARRANGE
        t1.setDescription("Eine interessante Strecke");
        t2.setDescription("Eine spannende Strecke");
        t3.setDescription("Eine Beschreibung");

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("Strecke", tours);


        // ASSERT
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTourId());
        assertEquals(2, result.get(1).getTourId());
    }

    @Test
    @DisplayName("Search by Tour-Description or Name | expect 2")
    public void testSearchByTourNameDescription() {
        // ARRANGE
        t1.setName("Spannende Tour");
        t2.setDescription("Eine Strecke");
        t3.setDescription("Eine spannende Beschreibung");

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("spannend", tours);


        // ASSERT
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTourId());
        assertEquals(3, result.get(1).getTourId());
    }

    @Test
    @DisplayName("Search by Filters (min-Distance) | expect 1")
    public void testSearchByTourFilters() {
        // ARRANGE
        TourLog tl1 = new TourLog(1, 1, new Date(), "", 10.0, 3600, 6, 5.0, 10.0, 15.0, "", 2);
        TourLog tl2 = (TourLog) tl1.clone();
        TourLog tl3 = (TourLog) tl1.clone();
        TourLog tl4 = (TourLog) tl1.clone();
        tl1.setDistance(10.0);
        tl2.setDistance(7.0);
        tl3.setDistance(15.0);
        tl4.setDistance(20.0);

        t1.getTourLogs().add(tl1);

        t2.getTourLogs().add(tl2);

        t3.getTourLogs().add(tl3);
        t3.getTourLogs().add(tl4);

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("[min-distance]: 11", tours);


        // ASSERT
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getTourId());
    }

    @Test
    @DisplayName("Search by Filters (max-Distance) | expect 2")
    public void testSearchByTourFilters2() {
        // ARRANGE
        TourLog tl1 = new TourLog(1, 1, new Date(), "", 10.0, 3600, 6, 5.0, 10.0, 15.0, "", 2);
        TourLog tl2 = (TourLog) tl1.clone();
        TourLog tl3 = (TourLog) tl1.clone();
        TourLog tl4 = (TourLog) tl1.clone();
        tl1.setDistance(10.0);
        tl2.setDistance(7.0);
        tl3.setDistance(15.0);
        tl4.setDistance(20.0);

        t1.getTourLogs().add(tl1);

        t2.getTourLogs().add(tl2);

        t3.getTourLogs().add(tl3);
        t3.getTourLogs().add(tl4);

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("[max-distance]: 11", tours);


        // ASSERT
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTourId());
        assertEquals(2, result.get(1).getTourId());
    }

    @Test
    @DisplayName("Search by combined Filters (max-Distance & min-rating) | expect 0")
    public void testSearchByTourFilters3() {
        // ARRANGE
        TourLog tl1 = new TourLog(1, 1, new Date(), "", 10.0, 3600, 6, 5.0, 10.0, 15.0, "", 2);
        TourLog tl2 = (TourLog) tl1.clone();
        TourLog tl3 = (TourLog) tl1.clone();
        TourLog tl4 = (TourLog) tl1.clone();

        tl1.setRating(6);
        tl2.setRating(7);

        tl1.setDistance(10.0);
        tl2.setDistance(7.0);
        tl3.setDistance(15.0);
        tl4.setDistance(20.0);

        t1.getTourLogs().add(tl1);

        t2.getTourLogs().add(tl2);

        t3.getTourLogs().add(tl3);
        t3.getTourLogs().add(tl4);

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("[max-distance]: 11 [min-rating]: 8", tours);


        // ASSERT
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Search by combined Filters (max-Distance & min-rating) | expect 1")
    public void testSearchByTourFilters4() {
        // ARRANGE
        TourLog tl1 = new TourLog(1, 1, new Date(), "", 10.0, 3600, 6, 5.0, 10.0, 15.0, "", 2);
        TourLog tl2 = (TourLog) tl1.clone();
        TourLog tl3 = (TourLog) tl1.clone();
        TourLog tl4 = (TourLog) tl1.clone();

        tl1.setRating(6);
        tl2.setRating(7);

        tl1.setDistance(10.0);
        tl2.setDistance(7.0);
        tl3.setDistance(15.0);
        tl4.setDistance(20.0);

        t1.getTourLogs().add(tl1);

        t2.getTourLogs().add(tl2);

        t3.getTourLogs().add(tl3);
        t3.getTourLogs().add(tl4);

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("[max-distance]: 11 [min-rating]: 7", tours);


        // ASSERT
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getTourId());
    }

    @Test
    @DisplayName("Search by combined Filters (min-Distance & min-rating & exact breaks) | expect 1")
    public void testSearchByTourFilters5() {
        // ARRANGE
        TourLog tl1 = new TourLog(1, 1, new Date(), "", 10.0, 3600, 6, 5.0, 10.0, 15.0, "", 2);
        TourLog tl2 = (TourLog) tl1.clone();
        TourLog tl3 = (TourLog) tl1.clone();
        TourLog tl4 = (TourLog) tl1.clone();

        tl1.setNumberOfBreaks(2);
        tl2.setNumberOfBreaks(7);
        tl3.setNumberOfBreaks(5);
        tl4.setNumberOfBreaks(1);

        tl1.setRating(6);
        tl2.setRating(7);

        tl1.setDistance(10.0);
        tl2.setDistance(7.0);
        tl3.setDistance(15.0);
        tl4.setDistance(20.0);

        t1.getTourLogs().add(tl1);

        t2.getTourLogs().add(tl2);

        t3.getTourLogs().add(tl3);
        t3.getTourLogs().add(tl4);

        List<Tour> tours = new ArrayList<>() {{
            add(t1);
            add(t2);
            add(t3);
        }};

        // ACT
        List<Tour> result = mainBusiness.search("[min-distance]: 5 [min-rating]: 5 [breaks]: 1", tours);


        // ASSERT
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getTourId());
    }

}
