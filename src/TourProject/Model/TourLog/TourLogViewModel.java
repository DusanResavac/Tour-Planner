package TourProject.Model.TourLog;

import TourProject.BusinessLayer.ITourLogBusiness;
import TourProject.BusinessLayer.TourLogBusiness;
import TourProject.Model.MainWindow.MainViewModel;
import TourProject.Model.Tour.Tour;
import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class TourLogViewModel {
    @Getter @Setter
    ITourLogBusiness business;
    @Getter @Setter
    private SimpleBooleanProperty errorVisible = new SimpleBooleanProperty(false);
    @Getter @Setter
    private SimpleStringProperty errorMessage = new SimpleStringProperty("");
    @Getter @Setter
    private SimpleBooleanProperty isBusy = new SimpleBooleanProperty(false);
    private final ArrayList<TourLogSubscriber> subscribers = new ArrayList<>();
    @Getter @Setter
    Tour selectedTour;
    @Getter
    TourLog selectedTourLog;
    @Getter
    @Setter
    private SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>(LocalDate.now());
    @Getter
    @Setter
    private SimpleObjectProperty<Integer> timestampStunden = new SimpleObjectProperty<>();
    @Getter
    @Setter
    private SimpleObjectProperty<Integer> timestampMinuten = new SimpleObjectProperty<>();
    @Getter
    @Setter
    private SimpleObjectProperty<Integer> dauerStunden = new SimpleObjectProperty<>();
    @Getter
    @Setter
    private SimpleObjectProperty<Integer> dauerMinuten = new SimpleObjectProperty<>();
    @Getter
    @Setter
    private SimpleStringProperty report = new SimpleStringProperty("");
    @Getter
    @Setter
    private SimpleStringProperty distance = new SimpleStringProperty("");
    @Getter
    @Setter
    private SimpleDoubleProperty rating = new SimpleDoubleProperty(5);
    @Getter
    @Setter
    private SimpleDoubleProperty maxIncline = new SimpleDoubleProperty(5);
    @Getter
    @Setter
    private SimpleDoubleProperty avgSpeed = new SimpleDoubleProperty(10);
    @Getter
    @Setter
    private SimpleDoubleProperty topSpeed = new SimpleDoubleProperty(20);
    @Getter
    @Setter
    private SimpleStringProperty weather = new SimpleStringProperty("");
    @Getter
    @Setter
    private SimpleDoubleProperty breaks = new SimpleDoubleProperty(0);

    public void setTourLogBusiness(ITourLogBusiness tourLogBusiness) {
        business = tourLogBusiness;
    }

    public CompletableFuture<Boolean> save() {
        if (date.get() == null ||
                timestampStunden.get() == null ||
                timestampMinuten.get() == null ||
                dauerStunden.get() == null ||
                dauerMinuten.get() == null ||
                report.get() == null ||
                distance.get() == null ||
                weather.get() == null
        ) {
            errorVisible.set(true);
            errorMessage.set("Bitte überprüfen, ob die Startzeit, die Dauer und die Distanz eingetragen wurden.");
            return CompletableFuture.failedFuture(new NullPointerException("Die Felder dürfen nicht null sein."));
        }
        try {
            Double.parseDouble(distance.get());
            int temp = (int) rating.get();
            temp = (int) breaks.get();
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
        errorVisible.set(false);
        Date newDate = new Date();

        long day = Date.from(date.get().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
        long dayWithTime = day + (timestampStunden.get() * 60 * 60 + timestampMinuten.get() * 60) * 1000;
        newDate.setTime(dayWithTime);

        TourLog tourLog = new TourLog(
                selectedTourLog == null ? null : selectedTourLog.getId(),
                selectedTour.getTourId(),
                newDate,
                report.get(),
                Math.round(Double.parseDouble(distance.get()) * 100.0) / 100.0,
                dauerStunden.get() * 60 * 60 + dauerMinuten.get() * 60,
                (int) rating.get(),
                maxIncline.get(),
                avgSpeed.get(),
                Math.round(topSpeed.get() * 10.0) / 10.0,
                weather.get(),
                (int) breaks.get());

        isBusy.set(true);

        return business.insertTourLog(tourLog, selectedTour)
                .handle((addedTourLog, error) -> {

                    if (addedTourLog != null && error == null) {
                        for (TourLogSubscriber sub: subscribers) {
                            sub.updateAddedTourLog(addedTourLog);
                        }

                        isBusy.set(false);
                        return true;
                    } else if (error != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                errorMessage.set("Es ist ein Fehler aufgetreten: " + error.getMessage());
                                errorVisible.set(true);
                            }
                        });
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                errorMessage.set("Es ist ein Fehler aufgetreten.");
                                errorVisible.set(true);
                            }
                        });
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            isBusy.set(false);
                        }
                    });
                    return false;
                });
    }

    public void setSelectedTourLog(TourLog tourLog) {
        selectedTourLog = tourLog;

        try {
            SimpleDateFormat d = new SimpleDateFormat("HH:mm");
            String[] zeit = d.format(tourLog.getDatetime()).split(":");
            Integer hours = Integer.parseInt(zeit[0]);
            Integer minutes = Integer.parseInt(zeit[1]);
            Integer duration = tourLog.getDuration();
            int durationHours = duration / 3600;
            duration -= durationHours * 3600;
            int durationMinutes = duration / 60;

            date.set(tourLog.getDatetime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
            timestampStunden.set(hours);
            timestampMinuten.set(minutes);
            dauerStunden.set(durationHours);
            dauerMinuten.set(durationMinutes);
            report.set(tourLog.getReport());
            distance.set(tourLog.getDistance().toString());
            rating.set(tourLog.getRating());
            maxIncline.set(tourLog.getMaxIncline());
            avgSpeed.set(tourLog.getAverageSpeed());
            topSpeed.set(tourLog.getTopSpeed());
            weather.set(tourLog.getWeather());
            breaks.set(tourLog.getNumberOfBreaks());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSubscriber(TourLogSubscriber tourLogSubscriber) {
        subscribers.add(tourLogSubscriber);
    }
}
