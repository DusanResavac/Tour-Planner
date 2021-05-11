package TourProject.BusinessLayer;

import TourProject.Model.Tour.Tour;

import java.util.concurrent.CompletableFuture;

public interface ITourBusiness {
    public CompletableFuture<Tour> insertTour (Tour tour);
    public CompletableFuture<Boolean> updateTour (Tour tour);
}
