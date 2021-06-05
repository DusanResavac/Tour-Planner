package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.API.TourAPI;
import TourProject.DataAccessLayer.API.TourAPILoader;
import TourProject.DataAccessLayer.Config;
import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;
import TourProject.Model.api.TourInformation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class TourBusiness implements ITourBusiness {

    private TourAPI tourAPI;
    private DataAccessLayer dataAccessLayer;

    public TourBusiness() {
        tourAPI = TourAPILoader.getInstance().getTourAPI();
        dataAccessLayer = DatabaseLoader.getInstance().getDataAccessLayer();
    }

    @Override
    public CompletableFuture<Tour> insertTour (Tour tourParam) {

        Tour tour = (Tour) tourParam.clone();

        // get Route information from API
        return tourAPI.getRouteInformation(tour.getStart(), tour.getEnd())
                // insert tour and return tourInformation including tourId
                .thenCompose(tourInformation -> {
                    tour.setDistance(tourInformation.getDistance());
                    return dataAccessLayer.insertTour(tour)
                            .thenApply(tourId -> {
                                tourInformation.setTourId(Math.toIntExact(tourId));
                                tour.setTourId(Math.toIntExact(tourId));
                                return tourInformation;
                            });
                })
                // get and save route image on disk
                .thenCompose(tourInformation -> {
                    return retrieveAndSaveRouteImage(tourInformation, tour);
                })
                // update tour in database with route image
                .thenCompose(t -> {
                    return updateTour(t, false);
                });
    }

    public String randomStringGenerator() {
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            result.append(characterSet.charAt((int) (Math.random() * characterSet.length())));
        }

        return result.toString();
    }

    @Override
    public CompletableFuture<Tour> updateTour(Tour tour, boolean retrieveImage) {
        if (retrieveImage && tour.getStart() != null && tour.getEnd() != null && !tour.getStart().equals("") && !tour.getEnd().equals("")) {
            // Falls Start- oder Endpunkt verändert wurden, muss die API verwendet werden
            return tourAPI.getRouteInformation(tour.getStart(), tour.getEnd())
                    .thenCompose(tourInformation -> {
                        tour.setDistance(tourInformation.getDistance());
                        return retrieveAndSaveRouteImage(tourInformation, tour);
                    })
                    .thenCompose((Tour t) -> {
                        // Liefert Tour zurück, wenn sie erfolgreich aktualisiert werden konnte
                        return dataAccessLayer.updateTour(tour)
                                .thenApply(successful -> {
                                    return successful ? tour : null;
                                });
                    });
        }
        return dataAccessLayer.updateTour(tour)
                .thenApply(successful -> {
                    return successful ? tour : null;
                });
    }

    public CompletableFuture<Tour> retrieveAndSaveRouteImage (TourInformation tourInformation, Tour tour) {
        Integer tourId = tourInformation.getTourId() == null ? tour.getTourId() : tourInformation.getTourId();
        return tourAPI.getRouteImage(tourInformation.getSessionId(), tourId)
                /*.exceptionally(error -> {
                    throw new NullPointerException("Error while retrieving route-image occurred");
                })*/
                .thenApply(byteArray -> {
                    String imagePath =
                            String.format("%s/Tour-%d-%s.jpg",
                                    Config.getInstance().getAttribute("route_image_folder"),
                                    tour.getTourId(),
                                    randomStringGenerator());
                    if (byteArray != null) {
                        try {
                            Files.deleteIfExists(Path.of(imagePath));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                        fos.write(byteArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tourInformation.setTourId(tourId);
                    tour.setTourId(tourId);
                    tour.setImagePath(imagePath);
                    return tour;
                });
    }
}

