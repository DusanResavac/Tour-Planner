package TourProject.Model.Tour;

import TourProject.Model.Prototype;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class TourLog implements Prototype {

    /*
    id               serial primary key,
    tour             integer,
    datetime         timestamp,
    report           varchar(255),
    distance         double precision,
    total_time       integer,
    rating           integer,
    max_incline      integer,
    average_speed    double precision,
    top_speed        double precision,
    weather          varchar(100),
    number_of_breaks integer
    */
    @Getter @Setter
    private Integer id;
    @Getter @Setter
    private Integer tourId;
    @Getter @Setter
    private Date datetime = null;
    @Getter @Setter
    private String report;
    @Getter @Setter
    private Double distance;
    @Getter @Setter
    private Integer duration;
    @Getter
    private Integer rating;
    @Getter @Setter
    private Double maxIncline;
    @Getter @Setter
    private Double averageSpeed;
    @Getter @Setter
    private Double topSpeed;
    @Getter @Setter
    private String weather;
    @Getter @Setter
    private Integer numberOfBreaks;

    /**
     * Creates a log entry for a tour
     * @param id
     * @param tourId
     * @param datetime
     * @param report
     * @param distance
     * @param duration
     * @param rating
     * @param maxIncline
     * @param averageSpeed
     * @param topSpeed
     * @param weather
     * @param numberOfBreaks
     */
    public TourLog(Integer id, Integer tourId, Date datetime, String report, Double distance, Integer duration,
                   Integer rating, Double maxIncline, Double averageSpeed, Double topSpeed, String weather, Integer numberOfBreaks) {
        this.id = id;
        this.tourId = tourId;
        this.datetime = datetime;
        this.report = report;
        this.distance = distance;
        this.duration = duration;
        this.rating = rating;
        this.maxIncline = maxIncline;
        this.averageSpeed = averageSpeed;
        this.topSpeed = topSpeed;
        this.weather = weather;
        this.numberOfBreaks = numberOfBreaks;
    }

    public TourLog(TourLog tourLog) {
        this.id = tourLog.getId();
        this.tourId = tourLog.getTourId();
        this.datetime = tourLog.getDatetime();
        this.report = tourLog.getReport();
        this.distance = tourLog.getDistance();
        this.duration = tourLog.getDuration();
        this.rating = tourLog.getRating();
        this.maxIncline = tourLog.getMaxIncline();
        this.averageSpeed = tourLog.getAverageSpeed();
        this.topSpeed = tourLog.getTopSpeed();
        this.weather = tourLog.getWeather();
        this.numberOfBreaks = tourLog.getNumberOfBreaks();
    }

    public void setRating(Integer rating) {
        // Rating zwischen 1 und 10
        this.rating = Math.max(1, Math.min(10, rating));
    }

    @Override
    public String toString() {
        return "TourLog{" +
                "id=" + id +
                ", tourId=" + tourId +
                ", datetime=" + datetime +
                ", report='" + report + '\'' +
                ", distance=" + distance +
                ", duration=" + duration +
                ", rating=" + rating +
                ", maxIncline=" + maxIncline +
                ", averageSpeed=" + averageSpeed +
                ", topSpeed=" + topSpeed +
                ", weather='" + weather + '\'' +
                ", numberOfBreaks=" + numberOfBreaks +
                '}';
    }

    @Override
    public Prototype clone() {
        return new TourLog(this);
    }
}
