package TourProject.Model.Tour;

import TourProject.Model.Prototype;

import java.util.Date;

public class TourLog implements Prototype {

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

    public TourLog(TourLog tourLog) {
        date = tourLog.date;
        duration = tourLog.duration;
        distance = tourLog.distance;
        averageSpeed = tourLog.averageSpeed;
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

    @Override
    public Prototype clone() {
        return new TourLog(this);
    }
}
