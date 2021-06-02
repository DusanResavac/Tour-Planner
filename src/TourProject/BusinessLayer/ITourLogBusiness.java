package TourProject.BusinessLayer;

import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;

import java.util.concurrent.CompletableFuture;

public interface ITourLogBusiness {
    public CompletableFuture<TourLog> insertTourLog(TourLog tourlog, Tour tour);
}
