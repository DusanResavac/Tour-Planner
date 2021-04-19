package TourProject.DataAccessLayer;

import TourProject.model.Tour;

import java.util.List;

public interface DataAccessLayer {
    void setTourList(List<Tour> tourList);
    List<Tour> getTourList();
}
