package TourProject.model;

import javafx.beans.property.*;

import java.util.Date;

public class TourLog {

    private Date date = null;
    private IntegerProperty duration;
    private DoubleProperty distance;
    private DoubleProperty averageSpeed;

    public TourLog(Date date, Integer duration, Double distance, Double averageSpeed) {
        this.date = date;
        this.duration = new SimpleIntegerProperty(duration);
        this.distance = new SimpleDoubleProperty(distance);
        this.averageSpeed = new SimpleDoubleProperty(averageSpeed);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration.get();
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration.set(duration);
    }

    public double getDistance() {
        return distance.get();
    }

    public DoubleProperty distanceProperty() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance.set(distance);
    }

    public double getAverageSpeed() {
        return averageSpeed.get();
    }

    public DoubleProperty averageSpeedProperty() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed.set(averageSpeed);
    }
}
