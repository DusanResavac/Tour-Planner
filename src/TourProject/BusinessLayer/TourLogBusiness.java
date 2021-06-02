package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;

import java.util.concurrent.CompletableFuture;

public class TourLogBusiness implements ITourLogBusiness {

    DataAccessLayer database;

    public TourLogBusiness () {
        database = DatabaseLoader.getInstance().getDataAccessLayer();
    }

    @Override
    public CompletableFuture<TourLog> insertTourLog(TourLog tourLog, Tour tour) {

        return database.insertTourLog(tourLog, tour.getTourId())
                .thenApply(tourLogId -> {
                    if (tourLogId != null) {
                        tourLog.setId(Math.toIntExact(tourLogId));
                        return tourLog;
                    }

                    return null;
                });
    }
}
