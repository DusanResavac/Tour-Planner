package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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

    @Override
    public CompletionStage<TourLog> updateTourLog(TourLog tourLog) {
        return database.updateTourLog(tourLog)
                .thenApply(success -> {
                    return success ? tourLog : null;
                });
    }
}
