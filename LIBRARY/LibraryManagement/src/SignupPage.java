import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupPage extends JFrame implements ActionListener {
    JTextField nameField, emailField, userField;
    JPasswordField passField;
    JButton signupBtn, loginBtn;

    public SignupPage() {
        setTitle("Library Signup");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(252, 70, 107), 850, 550, new Color(63, 94, 251));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(null);
        bgPanel.setBounds(0, 0, 850, 550);
        add(bgPanel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(250, 80, 350, 380);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        JLabel title = new JLabel("Create New Account ✨", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(40, 15, 270, 40);
        formPanel.add(title);

        JLabel nameLbl = new JLabel("Full Name");
        nameLbl.setBounds(50, 70, 200, 20);
        formPanel.add(nameLbl);
        nameField = new JTextField();
        nameField.setBounds(50, 90, 250, 30);
        formPanel.add(nameField);

        JLabel emailLbl = new JLabel("Email");
        emailLbl.setBounds(50, 130, 200, 20);
        formPanel.add(emailLbl);
        emailField = new JTextField();
        emailField.setBounds(50, 150, 250, 30);
        formPanel.add(emailField);

        JLabel userLbl = new JLabel("Username");
        userLbl.setBounds(50, 190, 200, 20);
        formPanel.add(userLbl);
        userField = new JTextField();
        userField.setBounds(50, 210, 250, 30);
        formPanel.add(userField);

        JLabel passLbl = new JLabel("Password");
        passLbl.setBounds(50, 250, 200, 20);
        formPanel.add(passLbl);
        passField = new JPasswordField();
        passField.setBounds(50, 270, 250, 30);
        formPanel.add(passField);

        signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(50, 320, 250, 35);
        signupBtn.setBackground(new Color(252, 70, 107));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signupBtn.setFocusPainted(false);
        signupBtn.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(signupBtn);

        loginBtn = new JButton("Already have an account?");
        loginBtn.setBounds(50, 360, 250, 25);
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setForeground(new Color(63, 94, 251));
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(loginBtn);

        bgPanel.add(formPanel);

        signupBtn.addActionListener(this);
        loginBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupBtn) {
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || userField.getText().isEmpty() || passField.getPassword().length==0) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO users (name, email, username, password) VALUES (?, ?, ?, ?)"
                );
                pst.setString(1, nameField.getText());
                pst.setString(2, emailField.getText());
                pst.setString(3, userField.getText());
                pst.setString(4, new String(passField.getPassword()));
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Signup Successful!");
                dispose();
                new LoginPage().setVisible(true);
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Username or Email already exists!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == loginBtn) {
            dispose();
            new LoginPage().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignupPage().setVisible(true));
    }
}
