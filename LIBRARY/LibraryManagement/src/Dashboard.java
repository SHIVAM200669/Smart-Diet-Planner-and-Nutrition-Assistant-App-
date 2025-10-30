import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Dashboard extends JFrame implements ActionListener {
    String username;
    JPanel sidebar, mainPanel;
    JButton addBookBtn, addStudentBtn, issueBtn, returnBtn, statsBtn, logoutBtn;

    public Dashboard(String username) {
        this.username = username;
        setTitle("Library Dashboard");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 0, 5));
        sidebar.setBounds(0, 0, 200, 600);
        sidebar.setBackground(new Color(25, 32, 45));

        addBookBtn = makeBtn("📘 Add Book");
        addStudentBtn = makeBtn("👩‍🎓 Add Student");
        issueBtn = makeBtn("📖 Issue Book");
        returnBtn = makeBtn("↩️ Return Book");
        statsBtn = makeBtn("📊 View Records");
        logoutBtn = makeBtn("🚪 Logout");

        sidebar.add(addBookBtn);
        sidebar.add(addStudentBtn);
        sidebar.add(issueBtn);
        sidebar.add(returnBtn);
        sidebar.add(statsBtn);
        sidebar.add(logoutBtn);
        add(sidebar);

        mainPanel = new JPanel();
        mainPanel.setBounds(200, 0, 750, 600);
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setLayout(null);

        JLabel welcome = new JLabel("Welcome, " + username + " 👋");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setBounds(40, 30, 500, 30);
        mainPanel.add(welcome);

        // small stats
        JPanel cards = new JPanel(new GridLayout(1,4,10,10));
        cards.setBounds(40, 80, 660, 120);
        cards.setBackground(new Color(245,247,250));
        try {
            cards.add(makeCard("Total Books", getCount("books")));
            cards.add(makeCard("Total Students", getCount("students")));
            cards.add(makeCard("Issued Books", getIssuedCount("Issued")));
            cards.add(makeCard("Returned Books", getIssuedCount("Returned")));
        } catch (Exception ex) {
            cards.add(makeCard("Total Books", 0));
            cards.add(makeCard("Total Students", 0));
            cards.add(makeCard("Issued Books", 0));
            cards.add(makeCard("Returned Books", 0));
        }
        mainPanel.add(cards);

        add(mainPanel);
    }

    private JPanel makeCard(String title, int value) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        p.setBackground(Color.WHITE);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel v = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        b.setBackground(new Color(25, 32, 45));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(50, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(25, 32, 45));
            }
        });
        b.addActionListener(this);
        return b;
    }

    private int getCount(String table) throws Exception {
        int c = 0;
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM " + table);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) c = rs.getInt(1);
        return c;
    }

    private int getIssuedCount(String status) throws Exception {
        int c = 0;
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM issue_books WHERE status=?");
        pst.setString(1, status);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) c = rs.getInt(1);
        return c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutBtn) {
            dispose();
            new LoginPage().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Feature coming soon!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Admin").setVisible(true));
    }
}
