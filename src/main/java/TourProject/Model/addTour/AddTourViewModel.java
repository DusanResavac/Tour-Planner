package TourProject.Model.addTour;

import TourProject.Model.Tour.Tour;
import TourProject.Model.Tour.TourViewModel;

import java.util.concurrent.CompletableFuture;

public class AddTourViewModel extends TourViewModel {


    @Override
    public CompletableFuture<Boolean> saveChanges() {
        return updateOrInsertTour(true);
    }

    @Override
    public void checkFormValidity() {
        // If  any field is empty, the form is invalid
        if (getTourName().get() == null ||
                getTourDescription().get() == null ||
                getTourStart().get() == null ||
                getTourEnd().get() == null) {
            setInvalidForm(true);
            return;
        }
        String tourName = getTourName().get().trim();
        String tourDescription = getTourDescription().get().trim();
        String tourStart = getTourStart().get().trim();
        String tourEnd = getTourEnd().get().trim();
        setInvalidForm(tourName.length() <= 0 || tourDescription.length() <= 0 || tourStart.length() <= 0 || tourEnd.length() <= 0);
    }
}
