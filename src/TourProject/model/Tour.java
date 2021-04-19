package TourProject.model;


import java.util.ArrayList;
import java.util.List;

public class Tour implements Prototype {

    private String name;
    private String description;
    private Double distance;
    private String imagePath = null;
    private List<TourLog> tourLogs;


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
        this.name = tour.name;
        this.description = tour.description;
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
}
