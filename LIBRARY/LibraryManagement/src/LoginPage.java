import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn, signupBtn;

    public LoginPage() {
        setTitle("Library Login");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Gradient background panel
        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(63, 94, 251), 800, 550, new Color(252, 70, 107));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(null);
        bgPanel.setBounds(0, 0, 850, 550);
        add(bgPanel);

        // Card-style Login box
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(250, 100, 350, 330);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        JLabel title = new JLabel("Welcome Back 👋", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(40, 20, 270, 40);
        loginPanel.add(title);

        JLabel userLbl = new JLabel("Username");
        userLbl.setBounds(50, 90, 100, 20);
        loginPanel.add(userLbl);

        userField = new JTextField();
        userField.setBounds(50, 110, 250, 30);
        userField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        loginPanel.add(userField);

        JLabel passLbl = new JLabel("Password");
        passLbl.setBounds(50, 160, 100, 20);
        loginPanel.add(passLbl);

        passField = new JPasswordField();
        passField.setBounds(50, 180, 250, 30);
        passField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        loginPanel.add(passField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(50, 230, 250, 35);
        loginBtn.setBackground(new Color(63, 94, 251));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginPanel.add(loginBtn);

        signupBtn = new JButton("Create Account");
        signupBtn.setBounds(50, 275, 250, 30);
        signupBtn.setBackground(Color.WHITE);
        signupBtn.setForeground(new Color(63, 94, 251));
        signupBtn.setBorder(BorderFactory.createEmptyBorder());
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginPanel.add(signupBtn);

        bgPanel.add(loginPanel);

        loginBtn.addActionListener(this);
        signupBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill username and password.");
                return;
            }
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();
                    new Dashboard(username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == signupBtn) {
            dispose();
            new SignupPage().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
