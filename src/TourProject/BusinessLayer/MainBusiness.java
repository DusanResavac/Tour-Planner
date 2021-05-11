package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;

import java.util.ArrayList;
import java.util.List;

public class MainBusiness {

    private List<Tour> tours;
    private Tour selectedTour;
    private DataAccessLayer dataAccessLayer;

    public MainBusiness() {
        this.dataAccessLayer = DatabaseLoader.getInstance().getDataAccessLayer();
    }


    public List<Tour> getTours () {
        if (tours == null) {
            tours = this.dataAccessLayer.getTourList();
        }
        return tours;
    }

    public Tour getSelectedTour() {
        return selectedTour;
    }

    public void setSelectedTour(Tour selectedTour) {
        this.selectedTour = selectedTour;
    }

    public List<Tour> search(String text) {
        text = text.toLowerCase().trim();
        List<Tour> tempTours = new ArrayList<Tour>();
        boolean isEmpty = text.equals("");

        for (Tour tour : tours) {
            if (isEmpty || tour.getName().toLowerCase().contains(text)) {
                tempTours.add(tour);
            }
        }

        return tempTours;
    }
}
