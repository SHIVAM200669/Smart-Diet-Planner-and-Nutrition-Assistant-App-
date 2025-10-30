import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DietPlanDAO {

    public DietPlan addPlan(DietPlan plan) throws SQLException {
        String sql = "INSERT INTO diet_plans (user_id, date_plan, meal_time, items, calories, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, plan.getUserId());
            ps.setDate(2, plan.getDatePlan());
            ps.setString(3, plan.getMealTime());
            ps.setString(4, plan.getItems());
            if (plan.getCalories() != null) ps.setInt(5, plan.getCalories()); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, plan.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) plan.setId(rs.getInt(1));
            }
        }
        return plan;
    }

    public List<DietPlan> getAllPlans(int userId) throws SQLException {
        List<DietPlan> list = new ArrayList<>();
        String sql = "SELECT * FROM diet_plans WHERE user_id = ? ORDER BY date_plan DESC, id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DietPlan d = new DietPlan();
                    d.setId(rs.getInt("id"));
                    d.setUserId(rs.getInt("user_id"));
                    d.setDatePlan(rs.getDate("date_plan"));
                    d.setMealTime(rs.getString("meal_time"));
                    d.setItems(rs.getString("items"));
                    int c = rs.getInt("calories");
                    if (!rs.wasNull()) d.setCalories(c);
                    d.setNotes(rs.getString("notes"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    public boolean deletePlan(int id) throws SQLException {
        String sql = "DELETE FROM diet_plans WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public DietPlan findById(int id) throws SQLException {
        String sql = "SELECT * FROM diet_plans WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DietPlan d = new DietPlan();
                    d.setId(rs.getInt("id"));
                    d.setUserId(rs.getInt("user_id"));
                    d.setDatePlan(rs.getDate("date_plan"));
                    d.setMealTime(rs.getString("meal_time"));
                    d.setItems(rs.getString("items"));
                    int c = rs.getInt("calories");
                    if (!rs.wasNull()) d.setCalories(c);
                    d.setNotes(rs.getString("notes"));
                    return d;
                }
            }
        }
        return null;
    }

    public DietPlan updatePlan(DietPlan plan) throws SQLException {
        String sql = "UPDATE diet_plans SET date_plan=?, meal_time=?, items=?, calories=?, notes=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, plan.getDatePlan());
            ps.setString(2, plan.getMealTime());
            ps.setString(3, plan.getItems());
            if (plan.getCalories() != null) ps.setInt(4, plan.getCalories()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, plan.getNotes());
            ps.setInt(6, plan.getId());
            ps.executeUpdate();
        }
        return plan;
    }
}
