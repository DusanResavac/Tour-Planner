package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainBusiness {
    private final List<Tour> tours = FXCollections.observableArrayList();
    private Tour selectedTour;
    private final DataAccessLayer dataAccessLayer;

    public MainBusiness() {
        this.dataAccessLayer = DatabaseLoader.getInstance().getDataAccessLayer();
    }


    public List<Tour> getTours() {
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


        String pattern = "\\[(min-|max-)?([a-zA-Z]*)]: *([^\\[]*)";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);

        // TODO: Vielleicht Automatisieren?
        // Variablen zum Filtern vorbereiten

        int maxRating = -1;
        int minRating = -1;
        int exactRating = -1;

        int maxBreaks = -1;
        int minBreaks = -1;
        int exactBreaks = -1;

        double maxDistance = -1;
        double minDistance = -1;
        double exactDistance = -1;

        double maxAvgSpeed = -1;
        double minAvgSpeed = -1;
        double exactAvgSpeed = -1;
        boolean atLeastOneFilter = false;


        while (matcher.find()) {
            String minOrMaxOrExact = matcher.group(1) == null ? null : matcher.group(1).toLowerCase();
            String attribute = matcher.group(2).toLowerCase();
            String value = matcher.group(3).toLowerCase();
            atLeastOneFilter = true;

            try {
                switch (attribute) {
                    case "rating" -> {
                        int rating = Integer.parseInt(value);
                        if (minOrMaxOrExact == null) {
                            exactRating = rating;
                        } else if (minOrMaxOrExact.equals("min-")) {
                            minRating = rating;
                        } else if (minOrMaxOrExact.equals("max-")) {
                            maxRating = rating;
                        }
                    }
                    case "breaks" -> {
                        int breaks = Integer.parseInt(value);
                        if (minOrMaxOrExact == null) {
                            exactBreaks = breaks;
                        } else if (minOrMaxOrExact.equals("min-")) {
                            minBreaks = breaks;
                        } else if (minOrMaxOrExact.equals("max-")) {
                            maxBreaks = breaks;
                        }
                    }
                    case "distance" -> {
                        double distance = Double.parseDouble(value);
                        if (minOrMaxOrExact == null) {
                            exactDistance = distance;
                        } else if (minOrMaxOrExact.equals("min-")) {
                            minDistance = distance;
                        } else if (minOrMaxOrExact.equals("max-")) {
                            maxDistance = distance;
                        }
                    }
                    case "avgspeed" -> {
                        double avgSpeed = Double.parseDouble(value);
                        if (minOrMaxOrExact == null) {
                            exactAvgSpeed = avgSpeed;
                        } else if (minOrMaxOrExact.equals("min-")) {
                            minAvgSpeed = avgSpeed;
                        } else if (minOrMaxOrExact.equals("max-")) {
                            maxAvgSpeed = avgSpeed;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // TODO: Logging?
                e.printStackTrace();
            }
        }

        for (Tour tour : tours) {
            boolean addedTour = false;
            for (TourLog tl : tour.getTourLogs()) {
                // Wenn Report oder Weather den Text enthält ODER ALLE Filter erfüllt werden
                if (tl.getReport().toLowerCase().contains(text) || tl.getWeather().toLowerCase().contains(text)) {
                    tempTours.add(tour);
                    addedTour = true;
                    break;
                    // Wenn eine der Bedingungen erfüllt wird, wird gegen einen Filter verstoßen
                    // und der Tourlog scheidet aus. Das wird dann negiert -> Wenn gegen keinen Filter verstoßen wird
                    // Dann muss noch geprüft werden, ob überhaupt ein Filter aktiv ist, sonst würden immer alle Touren
                    // genommen werden.
                } else if (!(maxRating > -1 && tl.getRating() > maxRating ||
                        minRating > -1 && tl.getRating() < minRating ||
                        exactRating > -1 && !tl.getRating().equals(exactRating) ||
                        maxBreaks > -1 && tl.getNumberOfBreaks() > maxBreaks ||
                        minBreaks > -1 && tl.getNumberOfBreaks() < minBreaks ||
                        exactBreaks > -1 && tl.getNumberOfBreaks() != exactBreaks ||
                        maxDistance > -1 && tl.getDistance() > maxDistance ||
                        minDistance > -1 && tl.getDistance() < minDistance ||
                        exactDistance > -1 && tl.getDistance() != exactDistance ||
                        maxAvgSpeed > -1 && tl.getAverageSpeed() > maxAvgSpeed ||
                        minAvgSpeed > -1 && tl.getAverageSpeed() < minAvgSpeed ||
                        exactAvgSpeed > -1 && tl.getAverageSpeed() != exactAvgSpeed) && atLeastOneFilter) {
                    tempTours.add(tour);
                    addedTour = true;
                    break;
                }
            }
            if (!addedTour && (isEmpty || tour.getName().toLowerCase().contains(text))) {
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
