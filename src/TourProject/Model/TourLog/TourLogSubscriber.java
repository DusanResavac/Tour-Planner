package TourProject.Model.TourLog;


public interface TourLogSubscriber {
    public void updateEditedTourLog(TourLog tourLog);
    public void updateAddedTourLog(TourLog tourLog);
}
