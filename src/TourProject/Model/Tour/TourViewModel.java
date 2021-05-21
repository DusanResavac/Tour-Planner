package TourProject.Model.Tour;

import TourProject.BusinessLayer.ITourBusiness;
import TourProject.DataAccessLayer.API.TourAPILoader;
import TourProject.Model.editTour.EditTourSubscriber;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public abstract class TourViewModel {
    private final ArrayList<EditTourSubscriber> subscribers = new ArrayList<>();
    public StringProperty tourName = new SimpleStringProperty("");
    public StringProperty tourDescription = new SimpleStringProperty("");
    public StringProperty tourStart = new SimpleStringProperty("");
    public StringProperty tourEnd = new SimpleStringProperty("");
    public BooleanProperty isBusy = new SimpleBooleanProperty(false);
    public BooleanProperty invalidForm = new SimpleBooleanProperty(false);
    public BooleanProperty routeError = new SimpleBooleanProperty(false);
    private ITourBusiness tourBusiness = null;
    private Tour selectedTour;

    public void addSubscriber(EditTourSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void setSelectedTour (Tour tour) {
        this.selectedTour = tour;
        tourName.setValue(selectedTour.getName());
        tourDescription.setValue(selectedTour.getDescription());
    }

    public abstract CompletableFuture<Boolean> saveChanges();

    protected CompletableFuture<Boolean> updateOrInsertTour(boolean insert) {
        routeError.set(false);
        invalidForm.set(true);
        isBusy.set(true);
        
        Tour tour = new Tour().builder()
                .setTourId(selectedTour.getTourId())
                .setName(tourName.get())
                .setDescription(tourDescription.get())
                .setStart(tourStart.get().equals("") ? null : tourStart.get())
                .setEnd(tourEnd.get().equals("") ? null : tourEnd.get())
                .setTourLogs(selectedTour.getTourLogs()).build();

        var tourAction = insert ?
                tourBusiness.insertTour(tour) :
                tourBusiness.updateTour(tour);

        return tourAction
                .handle((insertedOrUpdatedTour, error) -> {
                    if (error != null || insertedOrUpdatedTour == null) {
                        if (error != null) {
                            error.printStackTrace();
                        }
                        if (insertedOrUpdatedTour == null) {
                            System.err.println(insert ?
                                    "Fehler beim Hinzufügen der Tour aufgetreten." :
                                    "Fehler beim Verändern der Tour aufgetreten.");
                        }
                        routeError.set(true);
                        isBusy.set(false);
                        checkFormValidity();
                        return false;
                    }
                    Tour selectedTour = getSelectedTour();

                    selectedTour.setTourId(insertedOrUpdatedTour.getTourId());
                    selectedTour.setName(insertedOrUpdatedTour.getName());
                    selectedTour.setDescription(insertedOrUpdatedTour.getDescription());
                    selectedTour.setDistance(insertedOrUpdatedTour.getDistance());
                    selectedTour.setImagePath(insertedOrUpdatedTour.getImagePath());
                    getSubscribers().forEach(sub -> {
                        if (insert) {
                            sub.updateAddedTour(selectedTour);
                        } else {
                            sub.updateEditedTour(selectedTour);
                        }
                    });
                    isBusy.set(false);
                    checkFormValidity();
                    return true;
                });
    }


    public abstract void checkFormValidity();

    public ArrayList<EditTourSubscriber> getSubscribers() {
        return subscribers;
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

    public StringProperty getTourStart() {
        return tourStart;
    }

    public StringProperty getTourEnd() {
        return tourEnd;
    }

    public StringProperty getTourName() { return tourName; }

    public StringProperty getTourDescription() {
        return tourDescription;
    }

    public ITourBusiness getTourBusiness() {
        return tourBusiness;
    }

    public void setTourBusiness(ITourBusiness tourBusiness) {
        this.tourBusiness = tourBusiness;
    }

    protected Tour getSelectedTour() {
        return selectedTour;
    }
}
