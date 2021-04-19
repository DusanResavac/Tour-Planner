package TourProject.DataAccessLayer;

import TourProject.model.Tour;
import TourProject.model.TourLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Database implements DataAccessLayer {

    private List<Tour> tourList = new ArrayList<>();

    public Database() {
        setTourList(getData());
    }

    public void setTourList(List<Tour> tourList) {
        this.tourList = tourList;
    }

    public List<Tour> getTourList() {
        return tourList;
    }

    private List<Tour> getData() {
        Tour tour1 = new Tour("Tour 1 - France", "Die Tour de France [ˌtuʀdəˈfʀɑ̃ːs], auch Grande Boucle [gʀɑ̃dˈbukl] (französisch für Große Schleife) genannt, ist das bekannteste und wohl bedeutendste Straßenradrennen der Welt. Sie zählt neben dem Giro d’Italia und der Vuelta a España zu den Grand Tours. ", 150.0);
        Tour tour2 = new Tour("Tour 2 - Germany", "Eine tolle Fahrradtour durch Deutschland", 29.5);
        Tour tour3 = new Tour("Tour 3 - Brazil", "Ausgezeichnetes Sightseeing", 35.5);

        List<TourLog> tourLogs = new ArrayList<>(){{
            add(new TourLog(new Date(2021, Calendar.JANUARY, 15), 540, 135.0, 15.0));
            add(new TourLog(new Date(2021, Calendar.FEBRUARY, 18), 643, 150.0, 14.0));
        }};

        tour1.setTourLogs(tourLogs);

        return new ArrayList<>() {{
            add(tour1);
            add(tour2);
            add(tour3);
        }};
    }
}
