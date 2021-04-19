package TourProject.editTour;

import TourProject.model.Tour;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class EditTourViewModel {
    private final ArrayList<EditTourSubscriber> subscribers = new ArrayList<>();
    StringProperty tourName = new SimpleStringProperty();
    StringProperty tourDescription = new SimpleStringProperty();
    StringProperty tourStart = new SimpleStringProperty();
    StringProperty tourEnd = new SimpleStringProperty();
    private Tour selectedTour;

    public void addSubscriber(EditTourSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void setSelectedTour(Tour tour) {
        this.selectedTour = tour;
        tourName.setValue(selectedTour.getName());
        tourDescription.setValue(selectedTour.getDescription());
    }

    public boolean saveChanges() {
        // TODO: Überprüfung der Eigenschaften und eventuell API Aufruf
        selectedTour.setName(tourName.get());
        selectedTour.setDescription(tourDescription.get());
        selectedTour.setImagePath("image.jpg");
        selectedTour.setDistance(0.5);
        subscribers.forEach(sub -> sub.update(selectedTour));

        return true;
    }

    public Property<String> getTourName() {
        return tourName;
    }

    public Property<String> getTourDescription() {
        return tourDescription;
    }
}
