package TourProject.Model.TourLog;

import TourProject.BusinessLayer.ITourLogBusiness;
import TourProject.BusinessLayer.TourLogBusiness;
import TourProject.Model.MainWindow.MainViewModel;
import TourProject.Model.Tour.Tour;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class TourLogViewModel {
    ITourLogBusiness business;
    private final ArrayList<TourLogSubscriber> subscribers = new ArrayList<>();
    TourLog selectedTourLog;
    @Getter @Setter
    private SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>(LocalDate.now());
    @Getter @Setter
    private SimpleObjectProperty<Integer> timestampStunden = new SimpleObjectProperty<>();
    @Getter @Setter
    private SimpleObjectProperty<Integer> timestampMinuten = new SimpleObjectProperty<>();
    @Getter @Setter
    private SimpleObjectProperty<Integer> dauerStunden = new SimpleObjectProperty<>();
    @Getter @Setter
    private SimpleObjectProperty<Integer> dauerMinuten = new SimpleObjectProperty<>();
    @Getter @Setter
    private SimpleStringProperty report = new SimpleStringProperty("");
    @Getter @Setter
    private SimpleStringProperty distance = new SimpleStringProperty("");
    @Getter @Setter
    private SimpleDoubleProperty rating = new SimpleDoubleProperty(5);
    @Getter @Setter
    private SimpleDoubleProperty maxIncline = new SimpleDoubleProperty(5);
    @Getter @Setter
    private SimpleDoubleProperty avgSpeed = new SimpleDoubleProperty(10);
    @Getter @Setter
    private SimpleDoubleProperty topSpeed = new SimpleDoubleProperty(20);
    @Getter @Setter
    private SimpleStringProperty weather = new SimpleStringProperty();
    @Getter @Setter
    private SimpleDoubleProperty breaks = new SimpleDoubleProperty(0);

    public void setTourLogBusiness(ITourLogBusiness tourLogBusiness) {
        business = tourLogBusiness;
    }

    public CompletableFuture<Boolean> save() {
        return null;
    }

    public void setSelectedTour(TourLog tourLog) {
        selectedTourLog = tourLog;
    }

    public void addSubscriber(TourLogSubscriber tourLogSubscriber) {
        subscribers.add(tourLogSubscriber);
    }
}
