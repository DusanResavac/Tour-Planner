package TourProject.editTour;

import TourProject.DataAccessLayer.API.TourAPILoader;
import TourProject.model.Tour;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class EditTourViewModel {
    private final ArrayList<EditTourSubscriber> subscribers = new ArrayList<>();
    StringProperty tourName = new SimpleStringProperty();
    StringProperty tourDescription = new SimpleStringProperty();
    StringProperty tourStart = new SimpleStringProperty();
    StringProperty tourEnd = new SimpleStringProperty();
    BooleanProperty isBusy = new SimpleBooleanProperty(false);
    BooleanProperty invalidForm = new SimpleBooleanProperty(false);
    BooleanProperty routeError = new SimpleBooleanProperty(false);
    private Tour selectedTour;

    public void addSubscriber(EditTourSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void setSelectedTour(Tour tour) {
        this.selectedTour = tour;
        tourName.setValue(selectedTour.getName());
        tourDescription.setValue(selectedTour.getDescription());
    }

    public CompletableFuture<Boolean> saveChanges() {
        routeError.set(false);
        invalidForm.set(true);
        isBusy.set(true);
        if (tourStart.get() != null && tourStart.get().length() > 0 && tourEnd.get() != null && tourEnd.get().length() > 0) {
            var tourInformationFuture = TourAPILoader.getInstance().getTourAPI().getRouteInformation(tourStart.get(), tourEnd.get(), 1);
            return tourInformationFuture
                    .handle((tourInformation, error) -> {
                        if (error != null || tourInformation.getImagePath() == null) {
                            if (error != null) {
                                System.err.println(error.getMessage());
                            }
                            routeError.set(true);
                            isBusy.set(false);
                            checkFormValidity();
                            return false;
                        }
                        selectedTour.setName(tourName.get());
                        selectedTour.setDescription(tourDescription.get());
                        selectedTour.setDistance(tourInformation.getDistance());
                        selectedTour.setImagePath(tourInformation.getImagePath());
                        subscribers.forEach(sub -> sub.update(selectedTour));
                        isBusy.set(false);
                        checkFormValidity();
                        return true;
                    });

        } else {
            selectedTour.setName(tourName.get());
            selectedTour.setDescription(tourDescription.get());
            subscribers.forEach(sub -> sub.update(selectedTour));
            checkFormValidity();
            return CompletableFuture.supplyAsync(() -> {
                return true;
            });
        }

    }

    public BooleanProperty getInvalidFormProperty() {
        return invalidForm;
    }

    public void setInvalidForm(boolean invalidForm) {
        this.invalidForm.set(invalidForm);
    }

    public BooleanProperty getIsBusyProperty() {
        return isBusy;
    }

    public BooleanProperty getRouteErrorProperty() {
        return routeError;
    }

    public void setRouteError(boolean routeError) {
        this.routeError.set(routeError);
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy.set(isBusy);
    }



    public Property<String> getTourStart() {
        return tourStart;
    }

    public Property<String> getTourEnd() {
        return tourEnd;
    }

    public Property<String> getTourName() {
        return tourName;
    }

    public Property<String> getTourDescription() {
        return tourDescription;
    }

    public void checkFormValidity() {
        setInvalidForm(
                tourName.get() == null || tourName.get().length() <= 0 ||
                tourDescription.get() == null || tourDescription.get().length() <= 0
        );
    }
}
