import java.sql.Date;

public class DietPlan {
    private int id;
    private int userId;
    private Date datePlan;
    private String mealTime;
    private String items;
    private Integer calories;
    private String notes;

    public DietPlan() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getDatePlan() { return datePlan; }
    public void setDatePlan(Date datePlan) { this.datePlan = datePlan; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
