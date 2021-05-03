package TourProject.editTour;

import TourProject.DataAccessLayer.API.CallbackViewModel;
import TourProject.DataAccessLayer.API.TourAPILoader;
import TourProject.model.Tour;
import TourProject.model.api.TourInformation;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class EditTourViewModel implements CallbackViewModel {
    private final ArrayList<EditTourSubscriber> subscribers = new ArrayList<>();
    StringProperty tourName = new SimpleStringProperty();
    StringProperty tourDescription = new SimpleStringProperty();
    StringProperty tourStart = new SimpleStringProperty();
    StringProperty tourEnd = new SimpleStringProperty();
    private CallbackController c;
    private Tour selectedTour;

    public void addSubscriber(EditTourSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void setSelectedTour(Tour tour) {
        this.selectedTour = tour;
        tourName.setValue(selectedTour.getName());
        tourDescription.setValue(selectedTour.getDescription());
    }

    public void saveChanges(CallbackController controller) {
        this.c = controller;
        selectedTour.setName(tourName.get());
        selectedTour.setDescription(tourDescription.get());
        if (tourStart.get() != null && tourStart.get().length() > 0 && tourEnd.get() != null && tourEnd.get().length() > 0) {
            TourAPILoader.getInstance().getTourAPI().getRouteInformation(tourStart.get(), tourEnd.get(), 1, this);
            /*if (tourInformation == null) {
                controller.apiCallDone();
                return false;
            }
            selectedTour.setImagePath(tourInformation.getImagePath());
            selectedTour.setDistance(tourInformation.getDistance());*/
        } else {
            subscribers.forEach(sub -> sub.update(selectedTour));
        }


    }

    public void apiCallDone() {
        System.out.println("done in viewmodel");
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

    public boolean isInvalidForm() {
        return (tourName.get() == null || tourName.get().length() <= 0) ||
                (tourDescription.get() == null || tourDescription.get().length() <= 0);
    }

    @Override
    public void callback(TourInformation tourInformation) {
        selectedTour.setName(tourName.get());
        selectedTour.setDescription(tourDescription.get());

        if (tourInformation == null) {
            c.callback(false);
            return;
        }
        selectedTour.setImagePath(tourInformation.getImagePath());
        selectedTour.setDistance(tourInformation.getDistance());


        c.callback(true);
    }
}
