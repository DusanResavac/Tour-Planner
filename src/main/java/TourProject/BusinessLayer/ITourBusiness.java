package TourProject.BusinessLayer;

import TourProject.Model.Tour.Tour;

import java.util.concurrent.CompletableFuture;

public interface ITourBusiness {
    public CompletableFuture<Tour> insertTour (Tour tour);
    public CompletableFuture<Tour> updateTour (Tour tour, boolean retrieveImage);

    public CompletableFuture<String> generateDescription(Tour tour);
}
