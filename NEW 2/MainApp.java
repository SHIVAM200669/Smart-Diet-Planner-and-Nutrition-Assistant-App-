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
    private int currentUserId = 1; // demo user 'test' created by SQL script
    private int selectedPlanId = -1;

    public MainApp() {
        setTitle("Diet Planner");
        setSize(1000, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel lbl = new JLabel("Diet Planner", JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(70, 130, 180));
        lbl.setForeground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(100, 60));
        add(lbl, BorderLayout.NORTH);

        // Input Panel
        JPanel input = new JPanel(new GridBagLayout());
        input.setBorder(BorderFactory.createTitledBorder("Add / Edit Diet Plan"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        tfDate = new JTextField(15);
        tfMealTime = new JTextField(15);
        tfCalories = new JTextField(8);
        taItems = new JTextArea(4, 20);
        taNotes = new JTextArea(3, 20);

        c.gridx=0; c.gridy=0; input.add(new JLabel("Date (YYYY-MM-DD):"), c);
        c.gridx=1; input.add(tfDate, c);

        c.gridx=0; c.gridy=1; input.add(new JLabel("Meal Time:"), c);
        c.gridx=1; input.add(tfMealTime, c);

        c.gridx=0; c.gridy=2; input.add(new JLabel("Calories:"), c);
        c.gridx=1; input.add(tfCalories, c);

        c.gridx=0; c.gridy=3; input.add(new JLabel("Items:"), c);
        c.gridx=1; input.add(new JScrollPane(taItems), c);

        c.gridx=0; c.gridy=4; input.add(new JLabel("Notes:"), c);
        c.gridx=1; input.add(new JScrollPane(taNotes), c);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        c.gridx=0; c.gridy=5; c.gridwidth=2; input.add(btnPanel, c);

        add(input, BorderLayout.WEST);

        // Table
        model = new DefaultTableModel(new String[]{"ID","Date","Meal Time","Items","Calories","Notes"},0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button actions
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());

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
            p.setUserId(currentUserId);
            p.setDatePlan(Date.valueOf(tfDate.getText().trim()));
            p.setMealTime(tfMealTime.getText().trim());
            String items = taItems.getText().trim();
            p.setItems(items);
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
        tfDate.setText("");
        tfMealTime.setText("");
        tfCalories.setText("");
        taItems.setText("");
        taNotes.setText("");
        table.clearSelection();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<DietPlan> list = dao.getAllPlans(currentUserId);
            for (DietPlan d : list) {
                model.addRow(new Object[]{d.getId(), d.getDatePlan(), d.getMealTime(), d.getItems(), d.getCalories(), d.getNotes()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
