package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DatabaseMock implements DataAccessLayer {
    public Long idCounter = 1L;
    public Long tourLogIdCounter = 1L;
    public List<Tour> tourList;

    @Override
    public CompletableFuture<Long> insertTour(Tour tour) {
        if (tour.getTourId() == null) {
            return CompletableFuture.failedFuture(new SQLException("Creating tour failed, no ID obtained."));
        }
        return CompletableFuture.completedFuture(idCounter++);
    }

    @Override
    public CompletableFuture<Boolean> updateTour(Tour tour) {
        if (tour.getTourId() == null) {
            return CompletableFuture.failedFuture(new SQLException("Updating tour failed, no ID provided."));
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void setTourList(List<Tour> tourList) {
        this.tourList = tourList;
    }

    @Override
    public CompletableFuture<List<Tour>> retrieveData(boolean alsoSetData) {
        return CompletableFuture.supplyAsync(() -> {
            Tour t1 = new Tour("Tour 1: Österreich", "Schöne Tour durchs schöne Österreich", 40.0, "", "Wiener Wald", "Salzburg");
            Tour t2 = new Tour("Tour 2: Deutschland", "Kurze Tour", 15.0, "", "Hamburg", "Wetzleinsdorf");
            Tour t3 = new Tour("Tour 1: Griechenland", "Gute Tour fürs Montainbiking mit Badeoption am Ziel", 20.0, "", "λόγια", "Νερό βράχο");
            t1.setTourId(1);
            t2.setTourId(2);
            t3.setTourId(3);

            TourLog tl1 = new TourLog(1,2,new Date(), "", 41.2, 3600 * 2, 7, 5.0, 15.0, 23.5, "Sunny", 3);
            TourLog tl2 = (TourLog) tl1.clone();
            tl2.setTourId(2);
            tl2.setId(2);
            tl2.setDistance(14.8);
            tl2.setDuration(2000);
            TourLog tl3 = (TourLog) tl1.clone();

            t1.getTourLogs().add(tl1);
            t2.getTourLogs().add(tl2);
            t2.getTourLogs().add(tl3);

            List<Tour> tours = new ArrayList<Tour>(){{
                add(t1);
                add(t2);
                add(t3);
            }};

            if (alsoSetData) {
                setTourList(tours);
            }

            return tours;
        });

    }

    @Override
    public List<Tour> getTourList() {
        return tourList;
    }

    @Override
    public CompletableFuture<Boolean> removeTour(Tour tour) {
        if (tour.getTourId() == null) {
            return CompletableFuture.failedFuture(new SQLException("Deleting tour failed, no ID provided."));
        }
        for (int i = 0; i < tourList.size(); i++) {
            if (tourList.get(i).getTourId().equals(tour.getTourId())) {
                tourList.remove(i);
                return CompletableFuture.completedFuture(true);
            }
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Long> insertTourLog(TourLog tourLog, Integer tourId) {
        if (tourLog.getId() == null || tourLog.getTourId() == null || tourId == null) {
            return CompletableFuture.failedFuture(new SQLException("Inserting tourLog failed, no ID provided."));
        }

        for (Tour t: tourList) {
            if (t.getTourId().equals(tourId)) {
                t.getTourLogs().add(tourLog);
                return CompletableFuture.completedFuture(tourLogIdCounter++);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> updateTourLog(TourLog tourLog) {
        if (tourLog.getTourId() == null || tourLog.getId() == null) {
            return CompletableFuture.failedFuture(new SQLException("Updating tourLog failed, no ID provided."));
        }

        for (Tour t: tourList) {
            if (t.getTourId().equals(tourLog.getTourId())) {
                ArrayList<TourLog> tls = new ArrayList<>();
                for (TourLog tl: t.getTourLogs()) {
                    if (tl.getId().equals(tourLog.getId())) {
                        tls.add(tourLog);
                    } else {
                        tls.add(tl);
                    }
                }
                t.setTourLogs(tls);
                return CompletableFuture.completedFuture(true);
            }
        }

        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletionStage<Boolean> removeTourLog(TourLog selectedTourLog) {
        if (selectedTourLog.getTourId() == null || selectedTourLog.getId() == null) {
            return CompletableFuture.failedFuture(new SQLException("Removing tourLog failed, no ID provided."));
        }

        for (Tour t: tourList) {
            if (t.getTourId().equals(selectedTourLog.getTourId())) {
                ArrayList<TourLog> tls = new ArrayList<>();
                for (TourLog tl: t.getTourLogs()) {
                    if (!tl.getId().equals(selectedTourLog.getId())) {
                        tls.add(tl);
                    }
                }
                t.setTourLogs(tls);
                return CompletableFuture.completedFuture(true);
            }
        }

        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletionStage<Boolean> insertTourLogs(int tourID, List<TourLog> tourLogs) {
        for (int i = 0; i < tourList.size(); i++) {
            if (tourList.get(i).getTourId().equals(tourID)) {
                List<TourLog> tls = tourList.get(i).getTourLogs();
                tls.addAll(tourLogs);
                tourList.get(i).setTourLogs(tls);

                return CompletableFuture.completedFuture(true);
            }
        }

        return CompletableFuture.completedFuture(false);
    }

}
