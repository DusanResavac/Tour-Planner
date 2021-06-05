package TourProject.Model.Tour;


import TourProject.Model.Prototype;
import TourProject.Model.TourLog.TourLog;

import java.util.ArrayList;
import java.util.List;

public class Tour implements Prototype {

    private Integer tourId;
    private String name;
    private String description;
    private Double distance;
    private String start;
    private String end;
    private String imagePath;
    private List<TourLog> tourLogs = new ArrayList<>();

    public Tour() {}

    public Tour(String tourName, String description, Double distance, String imagePath, String start, String end) {
        this.name = tourName;
        this.description = description;
        this.distance = distance;
        this.imagePath = imagePath;
        this.start = start;
        this.end = end;
    }

    public Tour(String tourName, String description, Double distance, String imagePath) {
        this.name = tourName;
        this.description = description;
        this.distance = distance;
        this.imagePath = imagePath;
    }

    public Tour(String tourName, String description, Double distance) {
        this.name = tourName;
        this.description = description;
        this.distance = distance;
    }

    public Tour(Tour tour) {
        this.tourId = tour.getTourId();
        this.name = tour.name;
        this.description = tour.description;
        this.distance = tour.distance;
        this.imagePath = tour.imagePath;
        this.start = tour.start;
        this.end = tour.end;
        this.tourLogs = new ArrayList<>();
        tour.tourLogs.forEach(log -> this.tourLogs.add((TourLog) log.clone()));
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TourLog> getTourLogs() {
        return tourLogs;
    }

    public void setTourLogs(List<TourLog> tourLogs) {
        this.tourLogs = tourLogs;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", distance=" + distance +
                ", imagePath='" + imagePath + '\'' +
                ", tourLogs=" + tourLogs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tour tour = (Tour) o;

        if (name != null ? !name.equals(tour.name) : tour.name != null) return false;
        return tourLogs != null ? tourLogs.equals(tour.tourLogs) : tour.tourLogs == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tourLogs != null ? tourLogs.hashCode() : 0);
        return result;
    }

    @Override
    public Prototype clone() {
        return new Tour(this);
    }

    public TourBuilder builder() {
        return new TourBuilder();
    }

    public class TourBuilder {
        private final Tour tour;

        public TourBuilder() {
            this.tour = new Tour();
        }
        public TourBuilder setTourId(Integer tourId) {
            tour.setTourId(tourId);
            return this;
        }

        public TourBuilder setImagePath(String imagePath) {
            tour.setImagePath(imagePath);
            return this;
        }

        public TourBuilder setName(String name) {
            tour.setName(name);
            return this;
        }

        public TourBuilder setDescription(String description) {
            tour.setDescription(description);
            return this;
        }

        public TourBuilder setStart(String start) {
            tour.setStart(start);
            return this;
        }

        public TourBuilder setEnd(String end) {
            tour.setEnd(end);
            return this;
        }

        public TourBuilder setTourLogs(List<TourLog> tourLogs) {
            tour.setTourLogs(tourLogs);
            return this;
        }

        public TourBuilder setDistance(Double distance) {
            tour.setDistance(distance);
            return this;
        }

        public Tour build() {
            return tour;
        }

    }
}
