package TourProject.Model.TourLog;


public interface TourLogSubscriber {
    public void updateEditedTourLogDatetime(TourLog tourLog);
    public void updateAddedTourLog(TourLog tourLog);
    void updateEditedTourLogDuration(TourLog tourLogReceived);
}
