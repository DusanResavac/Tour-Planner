package TourProject.Model.Tour;

import TourProject.Model.Tour.Tour;

public interface TourSubscriber {
    public void updateEditedTour(Tour tour);
    public void updateAddedTour(Tour tour);
}
