package TourProject.Model.editTour;

import TourProject.Model.Tour.TourViewModel;

import java.util.concurrent.CompletableFuture;

public class EditTourViewModel extends TourViewModel {

    @Override
    public CompletableFuture<Boolean> saveChanges() {
        routeError.set(false);
        invalidForm.set(true);
        isBusy.set(true);
        if (tourStart.get() != null && tourStart.get().length() > 0 && tourEnd.get() != null && tourEnd.get().length() > 0) {
            return super.saveChanges();
        } else {
            var selectedTour = super.getSelectedTour();
            selectedTour.setName(tourName.get());
            selectedTour.setDescription(tourDescription.get());
            super.getSubscribers().forEach(sub -> sub.update(selectedTour));
            checkFormValidity();
            return CompletableFuture.supplyAsync(() -> {
                return true;
            });
        }

    }

    @Override
    public void checkFormValidity() {
        String tourName = getTourName().getValue().trim();
        String tourDescription = getTourDescription().getValue().trim();
        setInvalidForm(tourName.length() <= 0 || tourDescription.length() <= 0);
    }
}
