import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class SignupDialog extends JDialog {
    private JTextField tfUser, tfFull;
    private JPasswordField pfPass, pfPass2;
    private UserDAO userDao;

    public SignupDialog(JFrame parent, UserDAO userDao) {
        super(parent, "Sign up", true);
        this.userDao = userDao;
        setSize(420, 360);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setBackground(new Color(30, 136, 229));
        top.setPreferredSize(new Dimension(0, 60));
        JLabel lbl = new JLabel("Create an account", JLabel.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        top.add(lbl);
        add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        tfFull = new JTextField(18);
        tfUser = new JTextField(18);
        pfPass = new JPasswordField(18);
        pfPass2 = new JPasswordField(18);

        c.gridx=0; c.gridy=0; form.add(new JLabel("Full name:"), c);
        c.gridx=1; form.add(tfFull, c);
        c.gridx=0; c.gridy=1; form.add(new JLabel("Username:"), c);
        c.gridx=1; form.add(tfUser, c);
        c.gridx=0; c.gridy=2; form.add(new JLabel("Password:"), c);
        c.gridx=1; form.add(pfPass, c);
        c.gridx=0; c.gridy=3; form.add(new JLabel("Confirm pass:"), c);
        c.gridx=1; form.add(pfPass2, c);

        JButton btnCreate = new JButton("Create account");
        c.gridx=0; c.gridy=4; c.gridwidth=2; form.add(btnCreate, c);

        add(form, BorderLayout.CENTER);

        btnCreate.addActionListener(e -> {
            try {
                String full = tfFull.getText().trim();
                String username = tfUser.getText().trim();
                String p1 = String.valueOf(pfPass.getPassword()).trim();
                String p2 = String.valueOf(pfPass2.getPassword()).trim();
                if (full.isEmpty() || username.isEmpty() || p1.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fill all fields");
                    return;
                }
                if (!p1.equals(p2)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match");
                    return;
                }
                if (userDao.findByUsername(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already taken");
                    return;
                }
                User u = new User();
                u.setFullName(full);
                u.setUsername(username);
                u.setPassword(p1);
                userDao.create(u);
                JOptionPane.showMessageDialog(this, "Account created. You can login now.");
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            }
        });
    }
}
