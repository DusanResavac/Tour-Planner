package TourProject.BusinessLayer;

import TourProject.DataAccessLayer.API.TourAPI;
import TourProject.DataAccessLayer.API.TourAPILoader;
import TourProject.DataAccessLayer.Config;
import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.DataAccessLayer.DatabaseLoader;
import TourProject.Model.Tour.Tour;

import java.io.FileOutputStream;
import java.io.IOException;
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
                .exceptionally(error -> {
                    //System.err.println(error.getMessage());
                    throw new IllegalArgumentException("No route with specified starting point and endpoint found");
                })
                // insert tour and return tourInformation including tourId
                .thenCompose(tourInformation -> {
                    tour.setDistance(tourInformation.getDistance());
                    return dataAccessLayer.insertTour(tour)
                            .exceptionally( e -> {
                                throw new IllegalArgumentException("Could not insert tour into database");
                            })
                            .thenApply(tourId -> {
                                tourInformation.setTourId(tourId);
                                return tourInformation;
                            });
                })
                // get and save route image on disk
                .thenCompose(tourInformation -> {
                    return tourAPI.getRouteImage(tourInformation.getSessionId(), tourInformation.getTourId())
                            .exceptionally(error -> {
                                throw new NullPointerException("Error while retrieving route-image occurred");
                            })
                            .thenApply(byteArray -> {
                                String imagePath =
                                        String.format("%s/Tour-%d-%s.jpg",
                                                Config.getInstance().getAttribute("route_image_folder"),
                                                tourInformation.getTourId(),
                                                randomStringGenerator());
                                try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                                    fos.write(byteArray);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                tour.setTourId(tourInformation.getTourId());
                                tour.setImagePath(imagePath);

                                return new Tour().builder()
                                        .setTourId(tourInformation.getTourId())
                                        .setImagePath(imagePath)
                                        .build();
                            });
                })
                // update tour in database with route image
                .thenCompose(this::updateTour)
                // return final tour which was inserted into the database
                .thenApply(success -> {
                    return success ? tour : null;
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
    public CompletableFuture<Boolean> updateTour(Tour tour) {
        return dataAccessLayer.updateTour(tour);
    }
}
