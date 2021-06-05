package TourProject.Model.editTourLog;

import TourProject.BusinessLayer.TourLogBusiness;
import TourProject.Model.CustomDialog.CustomDialogController;
import TourProject.Model.TourLog.TourLog;
import TourProject.Model.TourLog.TourLogSubscriber;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class TourLogDurationVM {
    @Getter
    private final SimpleObjectProperty<Integer> duration = new SimpleObjectProperty<>(10);
    @Getter
    private final SimpleBooleanProperty busy = new SimpleBooleanProperty(false);
    @Getter @Setter
    private TourLog tourLog;
    @Getter @Setter
    private TourLogBusiness tourLogBusiness;
    private final List<TourLogSubscriber> subscribers = new ArrayList<>();

    public void addSubscriber(TourLogSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public CompletionStage<Boolean> saveChanges() {
        if (duration.get() == null) {
            CustomDialogController dialog = new CustomDialogController("Duration error", "Please select or enter a valid duration.", true);
            dialog.showAndWait();
            return CompletableFuture.failedFuture(new NullPointerException("Duration must be valid"));
        }

        busy.set(true);
        int seconds = duration.get() * 60;

        TourLog t = new TourLog();
        t.setId(tourLog.getId());
        t.setTourId(tourLog.getTourId());
        t.setDuration(seconds);

        return tourLogBusiness.updateTourLog(t)
                .handle((tourLogReceived, error) -> {
                    busy.set(false);

                    if (tourLogReceived == null || error != null) {
                        CustomDialogController dialog = new CustomDialogController("Duration update error", "The duration of the tourlog could not be updated.", true);
                        dialog.showAndWait();
                        return false;
                    }

                    for (var subscriber: subscribers) {
                        subscriber.updateEditedTourLogDuration(tourLogReceived);
                    }

                    return true;
                });
    }
}
