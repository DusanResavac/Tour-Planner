package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.Config;
import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Comparison;
import TourProject.Model.CustomDialog.CustomDialogController;
import TourProject.Model.FilterCondition;
import TourProject.Model.Tour.Tour;
import TourProject.Model.TourLog.TourLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                    tour.getDescription().toLowerCase().contains(text) ||
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

    public void exportToursPDF(List<Tour> tours) {
        var thread = new Thread(() -> {
            try {
                String filepath = Config.getInstance().getAttribute("reports_folder") + "tours-summary.pdf";
                Files.deleteIfExists(Path.of(filepath));

                int toursTotalTime = 0;
                double toursTotalDistance = 0;
                double toursMaxIncline = 0;

                for (Tour t : tours) {
                    for (TourLog tl : t.getTourLogs()) {
                        if (tl.getDuration() != null) {
                            toursTotalTime += tl.getDuration();
                        }
                        if (tl.getDistance() != null) {
                            toursTotalDistance += tl.getDistance();
                        }
                        if (tl.getMaxIncline() != null && tl.getMaxIncline() > toursMaxIncline) {
                            toursMaxIncline = tl.getMaxIncline();
                        }
                    }
                }

                int hours = toursTotalTime / (60 * 60);
                toursTotalTime -= hours * 60 * 60;
                int minutes = toursTotalTime / 60;

                PdfWriter writer = new PdfWriter(filepath);


                //Initialize PDF document
                PdfDocument pdf = new PdfDocument(writer);

                // Initialize document
                Document document = new Document(pdf);

                PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                Text title = new Text("Summary of Tours").setFont(bold).setFontSize(20);
                Text totalTime = new Text("Total time: " + hours + " hours and " + minutes + " minutes").setFont(normal);
                Text totalDistance = new Text("Total distance: " + toursTotalDistance + " km").setFont(normal);
                Text maxInclineOverall = new Text("Max incline overall: " + toursMaxIncline + "%").setFont(normal);

                //Add paragraph to the document
                document.add(new Paragraph(title))
                        .add(new Paragraph(totalTime))
                        .add(new Paragraph(totalDistance))
                        .add(new Paragraph(maxInclineOverall));

                //Close document
                document.close();
            } catch (Exception e) {
                Log4J.logger.error("PDF Tours summary error");
                e.printStackTrace();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        CustomDialogController dialog = new CustomDialogController("Pdf creation error", "An error occurred while the pdf was being generated.", true);
                        dialog.showAndWait();
                    }
                });
            }
        });
        thread.start();
    }

    public void exportTourPDF(Tour tour) {
        var thread = new Thread(() -> {
            try {
                String filepath = Config.getInstance().getAttribute("reports_folder") + "tour_" + tour.getTourId() + "-report.pdf";
                Files.deleteIfExists(Path.of(filepath));

                PdfWriter writer = new PdfWriter(filepath);

                //Initialize PDF document
                PdfDocument pdf = new PdfDocument(writer);

                // Initialize document
                Document document = new Document(pdf);

                PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                Text title = new Text(tour.getName()).setFont(bold).setFontSize(20);


                Image img = new Image(ImageDataFactory.create(tour.getImagePath()))
                        .setAutoScale(false)
                        .setWidth(220);

                Paragraph startToEnd = new Paragraph(new Text(tour.getStart()).setFont(normal))
                        .add(new Text(" to ").setFont(bold))
                        .add(new Text(tour.getEnd()).setFont(normal))
                        .add(new Text(" (" + ((double) Math.round(tour.getDistance() * 10) / 10) + " km)").setFont(bold).setFontSize(10));

                Paragraph tourDescription = new Paragraph(tour.getDescription()).setFontSize(11).setPadding(10);

                Table tourInfoTable = new Table(new float[]{1, 1}).useAllAvailableWidth().setFont(normal)
                        .addCell(new Cell().add(img).setBorder(Border.NO_BORDER).setPaddingRight(10).setPaddingTop(5).setPaddingBottom(3))
                        .addCell(new Cell().add(tourDescription).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(1)).setMinWidth(200))
                        .addCell(new Cell().add(startToEnd).setBorder(Border.NO_BORDER));

                Table tourLogTable = new Table(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}).useAllAvailableWidth().setFont(normal).setMarginTop(10);

                tourLogTable.addHeaderCell("Datetime")
                        .addHeaderCell("Report")
                        .addHeaderCell("Distance")
                        .addHeaderCell("Duration")
                        .addHeaderCell("Rating")
                        .addHeaderCell("Max Incline")
                        .addHeaderCell("Average Speed")
                        .addHeaderCell("Top Speed")
                        .addHeaderCell("Weather")
                        .addHeaderCell("Number of Breaks");

                SimpleDateFormat format = new SimpleDateFormat("dd. MMMMM, yyyy HH:mm");
                for (TourLog tl : tour.getTourLogs()) {
                    int hours = tl.getDuration() / 3600;
                    int minutes = (tl.getDuration() - hours * 3600) / 60;

                    tourLogTable.addCell(format.format(tl.getDatetime()))
                            .addCell(tl.getReport())
                            .addCell(tl.getDistance() + " km")
                            .addCell(hours + "h and " + minutes + " min")
                            .addCell(tl.getRating() + "")
                            .addCell(tl.getMaxIncline() + "%")
                            .addCell(tl.getAverageSpeed() + " km/h")
                            .addCell(tl.getTopSpeed() + " km/h")
                            .addCell(tl.getWeather())
                            .addCell(tl.getNumberOfBreaks() + "");
                }

                document.add(new Paragraph(title))
                        .add(tourInfoTable)
                        .add(tourLogTable);
                document.close();

            } catch (Exception e) {
                Log4J.logger.error("PDF Tours summary error");
                e.printStackTrace();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialogController dialog = new CustomDialogController("Pdf creation error", "An error occurred while the pdf was being generated.", true);
                        dialog.showAndWait();
                    }
                });
            }
        });
        thread.start();
    }

    public CompletionStage<Boolean> importToursJSON(List<Tour> toursToImport, boolean deleteExistingTours) {

        if (deleteExistingTours) {
            List<CompletableFuture<Boolean>> completableFutures = new ArrayList<>();
            for (Tour t : this.tours) {
                completableFutures.add(dataAccessLayer.removeTour(t));
            }

            completableFutures.forEach(cf -> {
                cf.handle((t, ex) -> {
                    return ex == null && t != null && t;
                });
            });

            return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        var booleans = completableFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());

                        // Mindestens eine CompletableFuture war nicht erfolgreich,
                        // Trotzdem fortführen
                        if (booleans.contains(false)) {
                            Log4J.logger.warn("Error encountered while deleting tours for tour-import");
                        }

                        return true;
                    })
                    .thenCompose(b -> insertTours(toursToImport));

        } else {
            return insertTours(toursToImport)
                    .handle((success, ex) -> success != null & ex == null && success);
        }
    }

    private CompletableFuture<Boolean> insertTours(List<Tour> tours) {
        List<CompletableFuture<Boolean>> cfs = new ArrayList<>();
        for (Tour t : tours) {
            CompletableFuture<Boolean> cf = dataAccessLayer.insertTour(t)
                    .thenCompose(tourID -> {
                        if (tourID != null) {
                            return dataAccessLayer.insertTourLogs(Math.toIntExact(tourID), t.getTourLogs());
                        } else {
                            return CompletableFuture.supplyAsync(() -> false);
                        }
                    });
            cfs.add(cf);
        }

        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0]))
                    .exceptionally(ex -> null)
                    .join();

            Map<Boolean, List<CompletableFuture>> result =
                    cfs.stream().collect(Collectors.partitioningBy(CompletableFuture::isCompletedExceptionally));

            return result.get(true) == null || result.get(true).size() == 0;
        });
    }

    public boolean exportToursJSON(ActionEvent event) {
        try {
            String result = new Gson().toJson(tours);
            String dir = (String) Config.getInstance().getAttribute("json_export_folder");
            String filePath = dir + "tours_export.json";

            Files.createDirectories(Paths.get(dir));
            Files.deleteIfExists(Path.of(filePath));
            Files.writeString(Paths.get(filePath), result);

            return true;
        } catch (Exception e) {
            Log4J.logger.warn("Could not export tours to JSON");
            e.printStackTrace();
        }

        return false;
    }
}
