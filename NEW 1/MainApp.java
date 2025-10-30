import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;

public class MainApp extends JFrame {
    private JTextField tfDate, tfMealTime, tfCalories;
    private JTextArea taItems, taNotes;
    private JTable table;
    private DefaultTableModel model;
    private DietPlanDAO dao = new DietPlanDAO();
    private User currentUser;
    private int selectedPlanId = -1;

    public MainApp(User user) {
        this.currentUser = user;
        setTitle("Diet Planner - " + (user.getFullName() != null ? user.getFullName() : user.getUsername()));
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Header with logout
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 128, 255));
        header.setPreferredSize(new Dimension(0,70));
        JLabel title = new JLabel("Diet Planner", JLabel.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(10,16,10,10));
        header.add(title, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginApp());
        });
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(new JLabel("Hello, " + (user.getFullName() != null ? user.getFullName() : user.getUsername())));
        right.add(btnLogout);
        header.add(right, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Left: form
        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder("Add / Edit Diet Plan"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        tfDate = new JTextField(12);
        tfMealTime = new JTextField(12);
        tfCalories = new JTextField(8);
        taItems = new JTextArea(4, 18);
        taNotes = new JTextArea(3, 18);

        c.gridx=0; c.gridy=0; left.add(new JLabel("Date (YYYY-MM-DD):"), c);
        c.gridx=1; left.add(tfDate, c);
        c.gridx=0; c.gridy=1; left.add(new JLabel("Meal Time:"), c);
        c.gridx=1; left.add(tfMealTime, c);
        c.gridx=0; c.gridy=2; left.add(new JLabel("Calories:"), c);
        c.gridx=1; left.add(tfCalories, c);
        c.gridx=0; c.gridy=3; left.add(new JLabel("Items:"), c);
        c.gridx=1; left.add(new JScrollPane(taItems), c);
        c.gridx=0; c.gridy=4; left.add(new JLabel("Notes:"), c);
        c.gridx=1; left.add(new JScrollPane(taNotes), c);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);

        c.gridx=0; c.gridy=5; c.gridwidth=2; left.add(btnPanel, c);

        add(left, BorderLayout.WEST);

        // Center: table and search
        JPanel center = new JPanel(new BorderLayout(6,6));
        center.setBorder(BorderFactory.createTitledBorder("Your Plans"));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfSearchDate = new JTextField(10);
        JButton btnSearch = new JButton("Search by date");
        JButton btnRefresh = new JButton("Refresh");
        searchRow.add(new JLabel("Date:")); searchRow.add(tfSearchDate); searchRow.add(btnSearch); searchRow.add(btnRefresh);

        model = new DefaultTableModel(new String[]{"ID","Date","Meal Time","Items","Calories","Notes"},0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        center.add(searchRow, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> {
            String d = tfSearchDate.getText().trim();
            if (!d.isEmpty()) searchByDate(d);
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedPlanId = (int) model.getValueAt(row, 0);
                tfDate.setText(String.valueOf(model.getValueAt(row,1)));
                tfMealTime.setText(String.valueOf(model.getValueAt(row,2)));
                taItems.setText(String.valueOf(model.getValueAt(row,3)));
                tfCalories.setText(String.valueOf(model.getValueAt(row,4)));
                taNotes.setText(String.valueOf(model.getValueAt(row,5)));
            }
        });

        loadData();
        setVisible(true);
    }

    private void onAdd() {
        try {
            DietPlan p = new DietPlan();
            p.setUserId(currentUser.getId());
            p.setDatePlan(Date.valueOf(tfDate.getText().trim()));
            p.setMealTime(tfMealTime.getText().trim());
            p.setItems(taItems.getText().trim());
            String cals = tfCalories.getText().trim();
            if (!cals.isEmpty()) p.setCalories(Integer.parseInt(cals));
            p.setNotes(taNotes.getText().trim());
            dao.addPlan(p);
            JOptionPane.showMessageDialog(this, "Plan added");
            clearForm();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedPlanId == -1) { JOptionPane.showMessageDialog(this, "Select a plan first"); return; }
        try {
            DietPlan p = dao.findById(selectedPlanId);
            if (p == null) { JOptionPane.showMessageDialog(this, "Selected plan not found"); return; }
            p.setDatePlan(Date.valueOf(tfDate.getText().trim()));
            p.setMealTime(tfMealTime.getText().trim());
            p.setItems(taItems.getText().trim());
            String cals = tfCalories.getText().trim();
            if (!cals.isEmpty()) p.setCalories(Integer.parseInt(cals)); else p.setCalories(null);
            p.setNotes(taNotes.getText().trim());
            dao.updatePlan(p);
            JOptionPane.showMessageDialog(this, "Plan updated");
            clearForm();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onDelete() {
        if (selectedPlanId == -1) { JOptionPane.showMessageDialog(this, "Select a plan first"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected plan?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.deletePlan(selectedPlanId);
            JOptionPane.showMessageDialog(this, "Deleted");
            clearForm();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        selectedPlanId = -1;
        tfDate.setText(""); tfMealTime.setText(""); tfCalories.setText(""); taItems.setText(""); taNotes.setText(""); table.clearSelection();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<DietPlan> list = dao.getAllPlans(currentUser.getId());
            for (DietPlan d : list) {
                model.addRow(new Object[]{d.getId(), d.getDatePlan(), d.getMealTime(), d.getItems(), d.getCalories(), d.getNotes()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + ex.getMessage());
        }
    }

    private void searchByDate(String date) {
        try {
            model.setRowCount(0);
            List<DietPlan> list = dao.getAllPlans(currentUser.getId());
            for (DietPlan d : list) {
                if (String.valueOf(d.getDatePlan()).equals(date)) {
                    model.addRow(new Object[]{d.getId(), d.getDatePlan(), d.getMealTime(), d.getItems(), d.getCalories(), d.getNotes()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }
}
