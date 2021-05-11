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

    public CompletableFuture<Boolean> saveChanges() {
        Tour tour = new Tour().builder()
                .setName(tourName.get())
                .setDescription(tourDescription.get())
                .setStart(tourStart.get())
                .setEnd(tourEnd.get()).build();

        return tourBusiness.insertTour(tour)
            .handle((insertedTour, error) -> {
                if (error != null) {
                    System.err.println(error.getMessage());
                    routeError.set(true);
                    isBusy.set(false);
                    checkFormValidity();
                    return false;
                }

                selectedTour.setTourId(insertedTour.getTourId());
                selectedTour.setName(insertedTour.getName());
                selectedTour.setDescription(insertedTour.getDescription());
                selectedTour.setDistance(insertedTour.getDistance());
                selectedTour.setImagePath(insertedTour.getImagePath());
                getSubscribers().forEach(sub -> sub.update(selectedTour));
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

    public Property<String> getTourStart() {
        return tourStart;
    }

    public Property<String> getTourEnd() {
        return tourEnd;
    }

    public Property<String> getTourName() { return tourName; }

    public Property<String> getTourDescription() {
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
