package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface DataAccessLayer {
    CompletableFuture<Long> insertTour (Tour tour);
    CompletableFuture<Boolean> updateTour (Tour tour);
    void setTourList(List<Tour> tourList);
    CompletableFuture<List<Tour>> retrieveData (boolean alsoSetData);
    List<Tour> getTourList();
    CompletableFuture<Boolean> removeTour(Tour tour);

    CompletableFuture<Long> insertTourLog(TourLog tourLog, Integer tourId);

    CompletableFuture<Boolean> updateTourLog(TourLog tourLog);

    CompletionStage<Boolean> removeTourLog(TourLog selectedTourLog);

    CompletionStage<Boolean> insertTourLogs(int tourID, List<TourLog> tourLogs);
}
