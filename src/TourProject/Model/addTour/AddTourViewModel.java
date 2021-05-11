package TourProject.Model.addTour;

import TourProject.Model.Tour.TourViewModel;

import java.util.concurrent.CompletableFuture;

public class AddTourViewModel extends TourViewModel {


    @Override
    public CompletableFuture<Boolean> saveChanges() {
        routeError.set(false);
        invalidForm.set(true);
        isBusy.set(true);
        return super.saveChanges();
    }

    @Override
    public void checkFormValidity() {
        // If  any field is empty, the form is invalid
        String tourName = getTourName().getValue().trim();
        String tourDescription = getTourDescription().getValue().trim();
        String tourStart = getTourStart().getValue().trim();
        String tourEnd = getTourEnd().getValue().trim();
        setInvalidForm(tourName.length() <= 0 || tourDescription.length() <= 0 || tourStart.length() <= 0 || tourEnd.length() <= 0);
    }
}
