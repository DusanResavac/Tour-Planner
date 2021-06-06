package TourProject.Model.editTour;

import TourProject.Model.Tour.Tour;
import TourProject.Model.Tour.TourViewModel;

import java.util.concurrent.CompletableFuture;

public class EditTourViewModel extends TourViewModel {

    @Override
    public CompletableFuture<Boolean> saveChanges() {
        return updateOrInsertTour(false, true);
    }

    @Override
    public void checkFormValidity() {
        if (getTourName().get() == null ||
                getTourDescription().get() == null) {
            setInvalidForm(true);
            return;
        }
        String tourName = getTourName().get().trim();
        String tourDescription = getTourDescription().get().trim();
        setInvalidForm(tourName.length() <= 0 || tourDescription.length() <= 0);
    }
}
