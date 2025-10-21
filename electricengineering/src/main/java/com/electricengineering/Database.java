package com.electricengineering;

import com.electricengineering.models.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:electric_engineering.db";
    private static Database instance;
    private Connection connection;

    private Database() {
        try {
            // Явно регистрируем драйвер SQLite
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void initializeDatabase() throws SQLException {
        // Create users table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "role TEXT DEFAULT 'USER'" +
                ")";

        // Create ohm_results table
        String createOhmResultsTable = "CREATE TABLE IF NOT EXISTS ohm_results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "voltage REAL," +
                "current REAL," +
                "resistance REAL," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users (id)" +
                ")";

        // Create divider_results table
        String createDividerResultsTable = "CREATE TABLE IF NOT EXISTS divider_results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "vin REAL NOT NULL," +
                "vout_required REAL NOT NULL," +
                "tolerance REAL NOT NULL," +
                "resistor_series TEXT NOT NULL," +
                "min_resistance REAL NOT NULL," +
                "max_resistance REAL NOT NULL," +
                "resistor_count INTEGER NOT NULL," +
                "configuration TEXT NOT NULL," +
                "resistors TEXT NOT NULL," +
                "calculated_vout REAL NOT NULL," +
                "error_percentage REAL NOT NULL," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users (id)" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createOhmResultsTable);
            stmt.execute(createDividerResultsTable);

            // Create default admin user if not exists
            if (!userExists("admin")) {
                registerUser("admin", "admin123", "ADMIN");
            }
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }

    public boolean registerUser(String username, String password, String role) {
        if (connection == null) {
            System.err.println("Database connection is null");
            return false;
        }

        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        if (connection == null) {
            System.err.println("Database connection is null");
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (checkPassword(password, storedHash)) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            storedHash,
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userExists(String username) {
        if (connection == null) {
            System.err.println("Database connection is null");
            return false;
        }

        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveOhmResult(int userId, double voltage, double current, double resistance) {
        if (connection == null) {
            System.err.println("Database connection is null");
            return;
        }

        String sql = "INSERT INTO ohm_results (user_id, voltage, current, resistance) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, voltage);
            pstmt.setDouble(3, current);
            pstmt.setDouble(4, resistance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveDividerResult(int userId, double vin, double voutRequired, double tolerance,
                                  String resistorSeries, double minResistance, double maxResistance,
                                  int resistorCount, String configuration, String resistors,
                                  double calculatedVout, double errorPercentage) {
        if (connection == null) {
            System.err.println("Database connection is null");
            return;
        }

        String sql = "INSERT INTO divider_results (user_id, vin, vout_required, tolerance, " +
                "resistor_series, min_resistance, max_resistance, resistor_count, " +
                "configuration, resistors, calculated_vout, error_percentage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, vin);
            pstmt.setDouble(3, voutRequired);
            pstmt.setDouble(4, tolerance);
            pstmt.setString(5, resistorSeries);
            pstmt.setDouble(6, minResistance);
            pstmt.setDouble(7, maxResistance);
            pstmt.setInt(8, resistorCount);
            pstmt.setString(9, configuration);
            pstmt.setString(10, resistors);
            pstmt.setDouble(11, calculatedVout);
            pstmt.setDouble(12, errorPercentage);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OhmResult> getOhmResults(int userId, boolean isAdmin) {
        List<OhmResult> results = new ArrayList<>();
        if (connection == null) {
            System.err.println("Database connection is null");
            return results;
        }

        String sql = isAdmin ?
                "SELECT * FROM ohm_results ORDER BY created_at DESC" :
                "SELECT * FROM ohm_results WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (!isAdmin) {
                pstmt.setInt(1, userId);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new OhmResult(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("voltage"),
                        rs.getDouble("current"),
                        rs.getDouble("resistance"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<DividerResult> getDividerResults(int userId, boolean isAdmin) {
        List<DividerResult> results = new ArrayList<>();
        if (connection == null) {
            System.err.println("Database connection is null");
            return results;
        }

        String sql = isAdmin ?
                "SELECT * FROM divider_results ORDER BY created_at DESC" :
                "SELECT * FROM divider_results WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (!isAdmin) {
                pstmt.setInt(1, userId);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new DividerResult(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("vin"),
                        rs.getDouble("vout_required"),
                        rs.getDouble("tolerance"),
                        rs.getString("resistor_series"),
                        rs.getDouble("min_resistance"),
                        rs.getDouble("max_resistance"),
                        rs.getInt("resistor_count"),
                        rs.getString("configuration"),
                        rs.getString("resistors"),
                        rs.getDouble("calculated_vout"),
                        rs.getDouble("error_percentage"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}