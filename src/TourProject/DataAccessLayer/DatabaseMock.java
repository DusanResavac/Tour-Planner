package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DatabaseMock implements DataAccessLayer {
    @Override
    public CompletableFuture<Long> insertTour(Tour tour) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> updateTour(Tour tour) {
        return null;
    }

    @Override
    public void setTourList(List<Tour> tourList) {

    }

    @Override
    public CompletableFuture<List<Tour>> retrieveData(boolean alsoSetData) {
        return null;
    }

    @Override
    public List<Tour> getTourList() {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> removeTour(Tour tour) {
        return null;
    }

    @Override
    public CompletableFuture<Long> insertTourLog(TourLog tourLog, Integer tourId) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> updateTourLog(TourLog tourLog) {
        return null;
    }

    @Override
    public CompletionStage<Boolean> removeTourLog(TourLog selectedTourLog) {
        return null;
    }
}
