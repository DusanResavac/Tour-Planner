package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Comparison;
import TourProject.Model.FilterCondition;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import javafx.collections.FXCollections;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainBusiness {
    private final List<Tour> tours = new ArrayList<>();
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

    /**
     * Search based on a String text <br>
     * Supports the following filters: <br>
     * <br>
     * <p>
     * [(min- | max-)?attribut]: Wert
     * <p><blockquote><pre>
     *  Beispiele:
     *   [rating]: 5
     *      -> es wird nach Touren mit exakt 5/10 Bewertung gesucht
     *   [min-distance]: 20
     *      -> die Touren müssen mindestens einen TourLog
     *         mit einer Distanz >= 20 haben
     *   [max-breaks]: 5 [min-rating]: 8 ->
     *      -> die Touren müssen mindestens einen TourLog haben,
     *         der maximal 5 Pausen verzeichnet hat und
     *         mit mindestens 8/10 bewertet wurde
     *   [max-distance]: 50 [min-rating]: 7 [min-breaks]: 4
     *      -> TourLog hat mindestens 50km als Distanz,
     *         eine Mindestwertung von 7/10 und mindestens 4 Pausen
     * </pre></blockquote></p>
     *
     * @param text  the search term which is used to filter the tours
     * @param tours the tours which are chosen according to the filter criteria
     * @return a List of Tours that match the criteria
     */
    public List<Tour> search(String text, List<Tour> tours) {
        text = text.toLowerCase().trim();
        List<Tour> tempTours = new ArrayList<>();
        boolean isEmpty = text.equals("");


        // Erlaubte Patterns:
        /*
        [(min- | max-)?attribut]: Wert
        Beispiele:
            [rating]: 5 -> es wird nach Touren mit exakt 5/10 Bewertung gesucht
            [min-distance]: 20 -> die Touren müssen mindestens einen TourLog mit einer Distanz >= 20 haben
            [max-breaks]: 5 [min-rating]: 8 -> die Touren müssen mindestens einen TourLog haben,
                                               der maximal 5 Pausen verzeichnet hat und mit mindestens 8/10 bewertet wurde
            [max-distance]: 50 [min-rating]: 7 [min-breaks]: 4  -> TourLog hat mindestens 50km als Distanz, eine Mindestwertung
                                                                   von 7/10 und mindestens 4 Pausen
         */
        String pattern = "\\[(min-|max-)?([a-zA-Z]*)]: *([^\\[ ]*)";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);

        List<FilterCondition> conditions = new ArrayList<FilterCondition>();


        while (matcher.find()) {
            String minOrMax = matcher.group(1) == null ? null : matcher.group(1).toLowerCase();
            String attribute = matcher.group(2).toLowerCase();
            String value = matcher.group(3).toLowerCase();
            Comparison c = null;

            if (minOrMax == null) {
                c = Comparison.EQUALS;
            } else if (minOrMax.equals("min-")) {
                c = Comparison.GREATER_OR_EQUALS;
            } else if (minOrMax.equals("max-")) {
                c = Comparison.SMALLER_OR_EQUALS;
            }

            try {
                conditions.add(new FilterCondition(attribute, value, c));
            } catch (IllegalArgumentException ignored) {
            }
        }

        for (Tour tour : tours) {
            boolean addedTour = false;
            for (TourLog tl : tour.getTourLogs()) {
                // Wenn Report oder Weather den Text enthält ODER Bedingungen besitzt und auch erfüllt
                if (tl.getReport().toLowerCase().contains(text) || tl.getWeather().toLowerCase().contains(text)) {
                    tempTours.add(tour);
                    addedTour = true;
                    break;
                } else if (conditions.size() > 0) {

                    // Es müssen alle Filter passen, damit ein TourLog die Kriterien erfüllt
                    boolean passesAllFilters = true;

                    for (FilterCondition fc : conditions) {
                        if (!fc.isFilterMet(tl)) {
                            passesAllFilters = false;
                            break;
                        }
                    }

                    if (passesAllFilters) {
                        tempTours.add(tour);
                        addedTour = true;
                        break;
                    }
                }
            }
            if (!addedTour && (isEmpty ||
                    tour.getName().toLowerCase().contains(text) ||
                    tour.getStart().toLowerCase().contains(text) ||
                    tour.getEnd().toLowerCase().contains(text))) {
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
