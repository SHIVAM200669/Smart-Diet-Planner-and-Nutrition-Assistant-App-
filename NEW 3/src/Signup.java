
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Signup extends JFrame {
    JTextField usernameField, ageField, weightField, heightField;
    JPasswordField passwordField;
    JButton signupButton;

    public Signup() {
        setTitle("Signup - Food Diet Planner");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setBounds(100, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 70, 100, 25);
        add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setBounds(150, 70, 180, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 110, 100, 25);
        add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 180, 25);
        add(passwordField);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(50, 150, 100, 25);
        add(ageLabel);
        ageField = new JTextField();
        ageField.setBounds(150, 150, 180, 25);
        add(ageField);

        JLabel weightLabel = new JLabel("Weight(kg):");
        weightLabel.setBounds(50, 190, 100, 25);
        add(weightLabel);
        weightField = new JTextField();
        weightField.setBounds(150, 190, 180, 25);
        add(weightField);

        JLabel heightLabel = new JLabel("Height(cm):");
        heightLabel.setBounds(50, 230, 100, 25);
        add(heightLabel);
        heightField = new JTextField();
        heightField.setBounds(150, 230, 180, 25);
        add(heightField);

        signupButton = new JButton("Signup");
        signupButton.setBounds(150, 280, 100, 30);
        signupButton.setBackground(new Color(70, 130, 180));
        signupButton.setForeground(Color.white);
        signupButton.setFocusPainted(false);
        add(signupButton);

        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setBounds(100, 330, 250, 25);
        loginLabel.setForeground(Color.BLUE.darker());
        add(loginLabel);

        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Login();
                dispose();
            }
        });

        signupButton.addActionListener(e -> registerUser());

        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String age = ageField.getText();
        String weight = weightField.getText();
        String height = heightField.getText();

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO users(username,password,age,weight,height) VALUES(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, Integer.parseInt(age));
            ps.setFloat(4, Float.parseFloat(weight));
            ps.setFloat(5, Float.parseFloat(height));

            int rows = ps.executeUpdate();
            if(rows > 0){
                JOptionPane.showMessageDialog(this, "Signup Successful! Please login.");
                new Login();
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
