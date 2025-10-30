import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginApp extends JFrame {
    private JTextField tfUser;
    private JPasswordField pfPass;
    private UserDAO userDao = new UserDAO();

    public LoginApp() {
        setTitle("Diet Planner - Login");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // top banner
        JPanel top = new JPanel();
        top.setBackground(new Color(30, 136, 229));
        top.setPreferredSize(new Dimension(0, 70));
        JLabel title = new JLabel("Diet Planner", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        top.setLayout(new BorderLayout());
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        tfUser = new JTextField(18);
        pfPass = new JPasswordField(18);

        c.gridx=0; c.gridy=0; form.add(new JLabel("Username:"), c);
        c.gridx=1; form.add(tfUser, c);
        c.gridx=0; c.gridy=1; form.add(new JLabel("Password:"), c);
        c.gridx=1; form.add(pfPass, c);

        JButton btnLogin = new JButton("Login");
        JButton btnSignup = new JButton("Sign up");

        JPanel btns = new JPanel();
        btns.add(btnLogin);
        btns.add(btnSignup);

        c.gridx=0; c.gridy=2; c.gridwidth=2; form.add(btns, c);

        add(form, BorderLayout.CENTER);

        // actions
        btnLogin.addActionListener(e -> onLogin());
        btnSignup.addActionListener(e -> onSignup());

        setVisible(true);
    }

    private void onLogin() {
        String user = tfUser.getText().trim();
        String pass = String.valueOf(pfPass.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }
        try {
            User u = userDao.findByUsername(user);
            if (u != null && pass.equals(u.getPassword())) {
                // success
                SwingUtilities.invokeLater(() -> {
                    new MainApp(u); // open dashboard with user
                });
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void onSignup() {
        SwingUtilities.invokeLater(() -> {
            new SignupDialog(this, userDao).setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginApp::new);
    }
}
