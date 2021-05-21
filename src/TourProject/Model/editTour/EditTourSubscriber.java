package TourProject.Model.editTour;

import TourProject.Model.Tour.Tour;

public interface EditTourSubscriber {
    public void updateEditedTour(Tour tour);
    public void updateAddedTour(Tour tour);
}
