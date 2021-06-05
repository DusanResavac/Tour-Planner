package TourProject.Model.editTourLog;

import TourProject.BusinessLayer.TourLogBusiness;
import TourProject.Model.CustomDialog.CustomDialogController;
import TourProject.Model.MainWindow.MainViewModel;
import TourProject.Model.TourLog.TourLog;
import TourProject.Model.TourLog.TourLogSubscriber;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class TourLogDatetimeVM {
    @Getter
    private final SimpleObjectProperty<Integer> uhrzeit = new SimpleObjectProperty<>(0);
    @Getter
    private final SimpleObjectProperty<LocalDate> datum = new SimpleObjectProperty<>(LocalDate.now());
    @Getter @Setter
    private TourLogBusiness tourLogBusiness;
    @Getter @Setter
    private TourLog tourLog;
    @Getter @Setter
    private SimpleBooleanProperty busy = new SimpleBooleanProperty(false);
    private final List<TourLogSubscriber> subscribers = new ArrayList<>();

    public void addSubscriber(TourLogSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public CompletionStage<Boolean> saveChanges() {
        if (datum.get() == null || uhrzeit.get() == null) {
            CustomDialogController dialog = new CustomDialogController("Date or Time error", "Please select or enter a valid date, as well as a valid time", true);
            dialog.showAndWait();
            return CompletableFuture.failedFuture(new NullPointerException("Date must be valid"));
        }
        busy.set(true);

        int hours = uhrzeit.get() / 60;
        int minutes = uhrzeit.get() - hours*60;
        LocalDateTime datetime = datum.get().atTime(hours, minutes);

        // TourLog nur mit den veränderten Daten erstellen
        // Außerdem Identifikation hinzufügen (ID & tourID)
        TourLog t = new TourLog();
        t.setId(tourLog.getId());
        t.setTourId(tourLog.getTourId());
        t.setDatetime(Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant()));

        return tourLogBusiness.updateTourLog(t)
                .handle((tourLogReceived, error) -> {
                    busy.set(false);

                    if (tourLogReceived == null || error != null) {
                        CustomDialogController dialog = new CustomDialogController("Date and time update error", "The date and time of the tourlog could not be updated.", true);
                        dialog.showAndWait();
                        return false;
                    }

                    for (var subscriber: subscribers) {
                        subscriber.updateEditedTourLogDatetime(tourLogReceived);
                    }

                    return true;
                });
    }
}
