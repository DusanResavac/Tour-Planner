package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;
import TourProject.Model.Tour.TourLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Database implements DataAccessLayer {

    private List<Tour> tourList = new ArrayList<>();
    private Connection connection = null;

    public Database() {

    }

    public void openConnection(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
        connection = null;
    }


    @Override
    public CompletableFuture<Long> insertTour(Tour tour) {
        // TODO: Insert tour (even if image is not given)
        return CompletableFuture.supplyAsync(() -> {

            try (PreparedStatement stmt = connection.prepareStatement("insert into tour (name, description, distance, start, \"end\", imagePath) values (?, ?, ?, ?, ?, null)", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, tour.getName());
                stmt.setString(2, tour.getDescription());
                stmt.setDouble(3, tour.getDistance());
                stmt.setString(4, tour.getStart());
                stmt.setString(5, tour.getEnd());
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating tour failed, no rows affected.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        // TODO: Logging
                        throw new SQLException("Creating tour failed, no ID obtained.");
                    }
                }
            } catch (SQLException throwables) {
                // TODO: Logging
                throwables.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> updateTour(Tour tour) {
        // TODO: Update tour (all values that are not null)
        return CompletableFuture.supplyAsync(() -> {
            try (var stmt = connection.prepareStatement("""
                    update tour set  name = COALESCE(?, name),
                      description = COALESCE(?, description),
                      distance = COALESCE(?, distance),
                      start = COALESCE(?, start),
                      "end" = COALESCE(?, "end"),
                      imagePath = COALESCE(?, imagePath) where id = ?""")) {
                stmt.setString(1, tour.getName());
                stmt.setString(2, tour.getDescription());
                if (tour.getDistance() == null) {
                    stmt.setNull(3, Types.DOUBLE);
                } else {
                    stmt.setDouble(3, tour.getDistance());
                }
                stmt.setString(4, tour.getStart());
                stmt.setString(5, tour.getEnd());
                stmt.setString(6, tour.getImagePath());
                if (tour.getTourId() == null) {
                    stmt.setNull(7, Types.INTEGER);
                } else {
                    stmt.setInt(7, tour.getTourId());
                }

                int affectedRows = stmt.executeUpdate();
                return affectedRows != 0;

            } catch (SQLException throwables) {
                System.err.println(throwables.getMessage());
            }
            return false;
        });
    }

    public void setTourList(List<Tour> tourList) {
        this.tourList = tourList;
    }

    public List<Tour> getTourList() {
        return tourList;
    }

    /**
     * Retrieves Tours from the database and returns them in a list.
     * If tours happen to contain tour-logs, such logs will be appended to the tours.
     * Parameter allows to choose whether the tourlist should be set in the database after
     * receiving all tours.
     * @param alsoSetData Controls if retrieved tours should be set in the database
     * @return CompletableFuture<List<Tour>> with all tours and corresponding logs if applicable
     */
    public CompletableFuture<List<Tour>> retrieveData (boolean alsoSetData) {

        return CompletableFuture.supplyAsync(() -> {
            try (var stmt = connection.prepareStatement("""
                    select id, name, description, distance, start, "end", imagepath from tour""")) {
                var rsTour = stmt.executeQuery();
                List<Tour> tourList = new ArrayList<>();
                while (rsTour.next()) {
                    Tour tour = new Tour().builder()
                            .setTourId(rsTour.getInt(1))
                            .setName(rsTour.getString(2))
                            .setDescription(rsTour.getString(3))
                            .setDistance(rsTour.getDouble(4))
                            .setStart(rsTour.getString(5))
                            .setEnd(rsTour.getString(6))
                            .setImagePath(rsTour.getString(7)).build();
                    List<TourLog> tourLogs = new ArrayList<>();
                    try (var stmt2 = connection.prepareStatement("""
                            select id, datetime, report, distance, duration, rating, max_incline, average_speed, top_speed, weather, number_of_breaks 
                            from tourlog where tour = ?""")) {
                        stmt2.setInt(1, tour.getTourId());
                        var rs = stmt2.executeQuery();
                        while (rs.next()) {
                            TourLog tourLog = new TourLog(
                                    rs.getInt(1),
                                    tour.getTourId(),
                                    new Date(rs.getTimestamp(2).getTime()),
                                    rs.getString(3),
                                    rs.getDouble(4),
                                    rs.getInt(5),
                                    rs.getInt(6),
                                    rs.getDouble(7),
                                    rs.getDouble(8),
                                    rs.getDouble(9),
                                    rs.getString(10),
                                    rs.getInt(11));
                            tourLogs.add(tourLog);
                        }
                    }
                    tour.setTourLogs(tourLogs);
                    tourList.add(tour);
                }
                if (alsoSetData) {
                    setTourList(tourList);
                }
                return tourList;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        });


            /*Tour tour1 = new Tour("Tour 1 - France", "Die Tour de France [ˌtuʀdəˈfʀɑ̃ːs], auch Grande Boucle [gʀɑ̃dˈbukl] (französisch für Große Schleife) genannt, ist das bekannteste und wohl bedeutendste Straßenradrennen der Welt. Sie zählt neben dem Giro d’Italia und der Vuelta a España zu den Grand Tours. ", 150.0);
            Tour tour2 = new Tour("Tour 2 - Germany", "Eine tolle Fahrradtour durch Deutschland", 29.5);
            Tour tour3 = new Tour("Tour 3 - Brazil", "Ausgezeichnetes Sightseeing", 35.5);

            List<TourLog> tourLogs = new ArrayList<>(){{
                add(new TourLog(new Date(2021, Calendar.JANUARY, 15), 540, 135.0, 15.0));
                add(new TourLog(new Date(2021, Calendar.FEBRUARY, 18), 643, 150.0, 14.0));
            }};

            tour1.setTourLogs(tourLogs);

            return new ArrayList<>() {{
                add(tour1);
                add(tour2);
                add(tour3);
            }};*/
    }
}
