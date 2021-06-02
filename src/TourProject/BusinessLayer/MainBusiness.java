package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MainBusiness {
    private final List<Tour> tours = FXCollections.observableArrayList();
    private Tour selectedTour;
    private final DataAccessLayer dataAccessLayer;

    public MainBusiness() {
        this.dataAccessLayer = DatabaseLoader.getInstance().getDataAccessLayer();
    }


    public List<Tour> getTours () {
        return tours;
    }

    public Tour getSelectedTour() {
        return selectedTour;
    }

    public void setSelectedTour(Tour selectedTour) {
        this.selectedTour = selectedTour;
    }

    public List<Tour> search(String text) {
        text = text.toLowerCase().trim();
        List<Tour> tempTours = new ArrayList<Tour>();
        boolean isEmpty = text.equals("");

        for (Tour tour : tours) {
            if (isEmpty || tour.getName().toLowerCase().contains(text)) {
                tempTours.add(tour);
            }
        }

        return tempTours;
    }

    public CompletableFuture<List<Tour>> retrieveTours() {
        return this.dataAccessLayer.retrieveData(true)
                .thenApply((tourList) -> {
                    if (tourList == null) {
                        System.err.println("Fehler beim Abrufen der Daten aus der Datenbank");
                    } else {
                        tours.clear();
                        tours.addAll(tourList);
                    }
                    return tours;
                });
    }

    public CompletableFuture<Boolean> removeTour(Tour tour) {
        // TODO: Remove artificial loading time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*return CompletableFuture.supplyAsync(() -> {
            return false;
        });*/
        return dataAccessLayer.removeTour(tour);
    }

    public CompletionStage<Boolean> removeTourLog(TourLog selectedTourLog) {
        return dataAccessLayer.removeTourLog(selectedTourLog);
    }
}
