package TourProject.model;

import javafx.beans.property.*;

import java.util.Date;

public class TourLog {

    private Date date = null;
    private int duration;
    private double distance;
    private double averageSpeed;

    public TourLog (Date date, Integer duration, Double distance, Double averageSpeed) {
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    @Override
    public String toString() {
        return "TourLog{" +
                "date=" + date +
                ", duration=" + duration +
                ", distance=" + distance +
                ", averageSpeed=" + averageSpeed +
                '}';
    }
}
