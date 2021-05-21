package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
}
