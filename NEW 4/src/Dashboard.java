import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.Timer;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Dashboard extends JFrame {
    private JTextField ageField, weightField, heightField;
    private JTextArea preferencesArea, dietArea;
    private String username;
    private JLabel locationLabel;
    private JLabel statusLabel;
    private JButton detectButton;
    private JButton mapButton;
    private double lastLat = Double.NaN;
    private double lastLon = Double.NaN;

    public Dashboard(String username) {
        this.username = username;
        setTitle("Food Diet Planner - Dashboard");
        setSize(950, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // 🔵 Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setPreferredSize(new Dimension(850, 80));
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        topPanel.add(welcomeLabel);
        add(topPanel, BorderLayout.NORTH);

        // 🔵 Center Panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(new Color(245, 245, 245));

        // 🔵 Left Panel - Info
        JPanel infoPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        infoPanel.setBackground(new Color(224, 255, 255));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Your Details"));

        infoPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        infoPanel.add(ageField);

        infoPanel.add(new JLabel("Weight (kg):"));
        weightField = new JTextField();
        infoPanel.add(weightField);

        infoPanel.add(new JLabel("Height (cm):"));
        heightField = new JTextField();
        infoPanel.add(heightField);

        infoPanel.add(new JLabel("Preferences / Goals:"));
        preferencesArea = new JTextArea(3, 20);
        preferencesArea.setLineWrap(true);
        preferencesArea.setWrapStyleWord(true);
        JScrollPane scrollPref = new JScrollPane(preferencesArea);
        infoPanel.add(scrollPref);

        JButton saveButton = new JButton("Save / Update Info");
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(saveButton);

        loadUserDetails();
        saveButton.addActionListener(e -> saveUserDetails());

        // 🔵 Right Panel - Diet + GPS
        JPanel dietPanel = new JPanel(new BorderLayout());
        dietPanel.setBackground(new Color(255, 228, 225));
        dietPanel.setBorder(BorderFactory.createTitledBorder("Suggested Diet Plan"));

        JPanel analyzerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        analyzerPanel.add(new JLabel("Ingredients:"));
        JTextField analyzerIngredientsField = new JTextField("Oats, Eggs, Chicken, Rice", 28);
        JButton analyzeBtn = new JButton("Analyze (AI)");
        analyzerPanel.add(analyzerIngredientsField);
        analyzerPanel.add(analyzeBtn);
        dietPanel.add(analyzerPanel, BorderLayout.NORTH);

        dietArea = new JTextArea();
        dietArea.setFont(new Font("Arial", Font.PLAIN, 16));
        dietArea.setEditable(false);
        dietArea.setLineWrap(true);
        dietArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(dietArea);
        dietPanel.add(scrollPane, BorderLayout.CENTER);

        // 🔵 Buttons under Diet
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton generateButton = new JButton("Generate Diet Plan");
        JButton downloadButton = new JButton("Download Plan");
        JButton ingredientPlannerBtn = new JButton("Ingredient Planner");
        JButton ingredientAnalyzerBtn = new JButton("Ingredient Analyzer");
        JButton gpsButton = new JButton("GPS Map");
        JLabel filler2 = new JLabel("");

        generateButton.setBackground(new Color(70, 130, 180));
        generateButton.setForeground(Color.WHITE);
        downloadButton.setBackground(new Color(255, 140, 0));
        downloadButton.setForeground(Color.WHITE);
        ingredientPlannerBtn.setBackground(new Color(34, 139, 34));
        ingredientPlannerBtn.setForeground(Color.WHITE);
        ingredientAnalyzerBtn.setBackground(new Color(123, 104, 238));
        ingredientAnalyzerBtn.setForeground(Color.WHITE);
        gpsButton.setBackground(new Color(46, 139, 87));
        gpsButton.setForeground(Color.WHITE);

        generateButton.setFont(new Font("Arial", Font.BOLD, 16));
        downloadButton.setFont(new Font("Arial", Font.BOLD, 16));
        ingredientPlannerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        ingredientAnalyzerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        gpsButton.setFont(new Font("Arial", Font.BOLD, 16));

        buttonPanel.add(generateButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(ingredientPlannerBtn);
        buttonPanel.add(ingredientAnalyzerBtn);
        buttonPanel.add(gpsButton);
        buttonPanel.add(filler2);

        dietPanel.add(buttonPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateDietPlan());
        downloadButton.addActionListener(e -> downloadPlan());
        ingredientPlannerBtn.addActionListener(e -> openIngredientPlanner());
        ingredientAnalyzerBtn.addActionListener(e -> openIngredientAnalyzer(analyzerIngredientsField.getText()));
        analyzeBtn.addActionListener(e -> openIngredientAnalyzer(analyzerIngredientsField.getText()));
        gpsButton.addActionListener(e -> openChennaiRouteDialog());

        centerPanel.add(infoPanel);
        centerPanel.add(dietPanel);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openIngredientPlanner() {
        JDialog dlg = new JDialog(this, "Ingredient Planner", true);
        dlg.setSize(520, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField ingredientsField = new JTextField("Oats, Eggs, Chicken, Rice, Vegetables, Fruits, Yogurt, Nuts");
        JTextField fromDateField = new JTextField("2025-10-29");
        JTextField toDateField = new JTextField("2025-11-04");
        JButton generateBtn = new JButton("Generate Chart");

        dlg.add(new JLabel("Ingredients (comma-separated):"));
        dlg.add(ingredientsField);
        dlg.add(new JLabel("From (YYYY-MM-DD):"));
        dlg.add(fromDateField);
        dlg.add(new JLabel("To (YYYY-MM-DD):"));
        dlg.add(toDateField);
        dlg.add(new JLabel(""));
        dlg.add(generateBtn);

        generateBtn.addActionListener(e -> {
            try {
                String ing = ingredientsField.getText().trim();
                LocalDate from = LocalDate.parse(fromDateField.getText().trim());
                LocalDate to = LocalDate.parse(toDateField.getText().trim());
                if (to.isBefore(from)) {
                    JOptionPane.showMessageDialog(dlg, "To date must be on/after From date");
                    return;
                }
                java.util.List<String> items = new ArrayList<>();
                for (String s : ing.split(",")) {
                    String v = s.trim();
                    if (!v.isEmpty()) items.add(v);
                }
                if (items.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Enter at least one ingredient");
                    return;
                }
                String chart = generateDietChart(items, from, to);
                dietArea.setText(chart);
                dlg.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid date format. Use YYYY-MM-DD");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        dlg.setVisible(true);
    }

    private String generateDietChart(java.util.List<String> items, LocalDate from, LocalDate to) {
        double weight = 70.0;
        double height = 170.0;
        try { weight = Double.parseDouble(weightField.getText().trim()); } catch (Exception ignored) {}
        try { height = Double.parseDouble(heightField.getText().trim()); } catch (Exception ignored) {}
        double bmi = 0.0;
        if (height > 0) bmi = weight / Math.pow(height / 100.0, 2);
        double base = 1800.0;
        if (bmi < 18.5) base += 200;
        else if (bmi > 25) base -= 200;
        StringBuilder sb = new StringBuilder();
        sb.append("Diet Chart\n");
        sb.append(String.format("Weight: %.1f kg, Height: %.0f cm, BMI: %.1f\n", weight, height, bmi));
        sb.append(String.format("Target Calories: %.0f kcal/day\n\n", base));

        int idx = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            sb.append(d.toString()).append("\n");
            String breakfast = items.get(idx % items.size()); idx++;
            String lunch = items.get(idx % items.size()); idx++;
            String snack = items.get(idx % items.size()); idx++;
            String dinner = items.get(idx % items.size()); idx++;
            sb.append("  Breakfast: ").append(breakfast).append("\n");
            sb.append("  Lunch: ").append(lunch).append("\n");
            sb.append("  Snack: ").append(snack).append("\n");
            sb.append("  Dinner: ").append(dinner).append("\n\n");
        }
        return sb.toString();
    }

    private void openIngredientAnalyzer(String ingredientsCsv) {
        java.util.List<String> items = new ArrayList<>();
        for (String s : ingredientsCsv.split(",")) {
            String v = s.trim();
            if (!v.isEmpty()) items.add(v);
        }
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ingredients to analyze");
            return;
        }
        double weight = 70.0;
        double height = 170.0;
        try { weight = Double.parseDouble(weightField.getText().trim()); } catch (Exception ignored) {}
        try { height = Double.parseDouble(heightField.getText().trim()); } catch (Exception ignored) {}
        double bmi = 0.0;
        if (height > 0) bmi = weight / Math.pow(height / 100.0, 2);
        java.util.List<Double> scores = new ArrayList<>();
        for (String it : items) {
            double base = Math.max(1, it.length());
            int v = 0; for (char c : it.toLowerCase().toCharArray()) if ("aeiou".indexOf(c) >= 0) v++;
            double ai = base * 1.2 + v * 2.0;
            if (bmi < 18.5) ai *= 1.05;
            else if (bmi > 25) ai *= 0.95;
            scores.add(ai);
        }
        JDialog dlg = new JDialog(this, "Ingredient Analyzer", true);
        dlg.setSize(640, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        JPanel chart = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int left = 60, right = 20, top = 20, bottom = 60;
                double max = 1.0; for (double sc : scores) max = Math.max(max, sc);
                int n = items.size();
                int bw = Math.max(18, (w - left - right) / Math.max(1, n) - 10);
                int x = left + 10;
                g2.setColor(new Color(245,245,255));
                g2.fillRect(left, top, w - left - right, h - top - bottom);
                g2.setColor(new Color(180,180,200));
                g2.drawRect(left, top, w - left - right, h - top - bottom);
                for (int i = 0; i < n; i++) {
                    double sc = scores.get(i);
                    int bh = (int) Math.round((h - top - bottom) * (sc / max));
                    int y = h - bottom - bh;
                    g2.setColor(new Color(123,104,238));
                    g2.fillRoundRect(x, y, bw, bh, 8, 8);
                    g2.setColor(new Color(70,70,120));
                    g2.drawRoundRect(x, y, bw, bh, 8, 8);
                    g2.setFont(new Font("Arial", Font.PLAIN, 11));
                    String label = items.get(i);
                    int tx = x + Math.max(0, (bw - g2.getFontMetrics().stringWidth(label)) / 2);
                    g2.drawString(label, tx, h - bottom + 18);
                    String sv = String.format("%.1f", sc);
                    int svx = x + Math.max(0, (bw - g2.getFontMetrics().stringWidth(sv)) / 2);
                    g2.drawString(sv, svx, y - 6);
                    x += bw + 10;
                }
                g2.setColor(new Color(60,60,90));
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("Ingredient Efficiency (AI)", left, 16);
                g2.dispose();
            }
        };
        dlg.add(chart, BorderLayout.CENTER);
        JButton close = new JButton("Close");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(close);
        dlg.add(south, BorderLayout.SOUTH);
        close.addActionListener(e -> dlg.dispose());
        StringBuilder sb = new StringBuilder();
        sb.append("Ingredient Analyzer\n");
        sb.append(String.format("BMI: %.1f\n", bmi));
        for (int i = 0; i < items.size(); i++) {
            sb.append(String.format("%s: %.1f\n", items.get(i), scores.get(i)));
        }
        dietArea.setText(sb.toString());
        dlg.setVisible(true);
    }

    private void showChennaiSummaryMap(double distanceKm, String type, double calories, String origin, String dest, java.util.List<String> waypoints) {
        JFrame mapFrame = new JFrame("Chennai Route Summary");
        mapFrame.setSize(640, 720);
        mapFrame.setLocationRelativeTo(this);
        mapFrame.setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(3,1));
        JLabel l1 = new JLabel("Origin: " + origin + ", Chennai");
        JLabel l2 = new JLabel("Destination: " + dest + ", Chennai");
        JLabel l3 = new JLabel(String.format("%s | Distance: %.2f km | Calories: %.0f kcal", type, distanceKm, calories));
        l1.setForeground(new Color(20, 120, 20));
        l2.setForeground(new Color(20, 120, 20));
        l3.setForeground(new Color(0, 100, 0));
        top.setBackground(new Color(220, 245, 220));
        top.add(l1);
        top.add(l2);
        top.add(l3);
        mapFrame.add(top, BorderLayout.NORTH);

        StaticChennaiMapPanel mapPanel = new StaticChennaiMapPanel(origin, dest, type, distanceKm, calories, waypoints);
        mapFrame.add(mapPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loadImgBtn = new JButton("Load Map Image");
        bottom.add(loadImgBtn);
        mapFrame.add(bottom, BorderLayout.SOUTH);

        loadImgBtn.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(mapFrame);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        mapPanel.setBackgroundImage(img);
                        mapPanel.repaint();
                    }
                } catch (Exception ignored) {}
            }
        });
        mapFrame.setVisible(true);
        mapPanel.startAnimation();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mapFrame,
                    "Great work! Keep up the pace and stay hydrated.\nFocus on steady breathing and form.",
                    "Fitness Motivation",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    

    private void openChennaiRouteDialog() {
        JDialog dlg = new JDialog(this, "Chennai Route", true);
        dlg.setSize(460, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField fromField = new JTextField("Chennai Central");
        JTextField toField = new JTextField("Marina Beach");
        String[] types = {"Running", "Cycling", "Walking"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        JTextField distanceKmField = new JTextField("5.0");
        JTextField waypointsField = new JTextField("Guindy, T Nagar");
        JButton openBtn = new JButton("Open Route");

        dlg.add(new JLabel("From:"));
        dlg.add(fromField);
        dlg.add(new JLabel("To:"));
        dlg.add(toField);
        dlg.add(new JLabel("Activity:"));
        dlg.add(typeBox);
        dlg.add(new JLabel("Distance (km):"));
        dlg.add(distanceKmField);
        dlg.add(new JLabel("Waypoints (comma-separated):"));
        dlg.add(waypointsField);
        dlg.add(new JLabel(""));
        dlg.add(openBtn);

        openBtn.addActionListener(ev -> {
            try {
                String origin = fromField.getText().trim();
                String dest = toField.getText().trim();
                String type = (String) typeBox.getSelectedItem();
                double distanceKm = Double.parseDouble(distanceKmField.getText().trim());

                double weight = 70.0;
                try { weight = Double.parseDouble(weightField.getText()); } catch (Exception ignored) {}
                double factor = 1.0;
                if ("Cycling".equals(type)) factor = 0.5;
                double calories = distanceKm * weight * factor;
                java.util.List<String> waypoints = new ArrayList<>();
                for (String s : waypointsField.getText().split(",")) {
                    String v = s.trim();
                    if (!v.isEmpty()) waypoints.add(v);
                }

                try {
                    Connection conn = DatabaseConnection.getConnection();
                    if (conn != null) {
                        PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO gps_tracking (username, activity_type, latitude, longitude, distance_km, calories_burned) VALUES (?, ?, ?, ?, ?, ?)"
                        );
                        ps.setString(1, username);
                        ps.setString(2, type);
                        ps.setDouble(3, 13.0827);
                        ps.setDouble(4, 80.2707);
                        ps.setDouble(5, distanceKm);
                        ps.setDouble(6, calories);
                        ps.executeUpdate();
                        conn.close();
                    }
                } catch (Exception ignored) {
                }

                SwingUtilities.invokeLater(() -> showChennaiSummaryMap(distanceKm, type, calories, origin, dest, waypoints));
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        dlg.setVisible(true);
    }

    private static class StaticChennaiMapPanel extends JPanel {
        private final String origin;
        private final String dest;
        private final String type;
        private final double distanceKm;
        private final double calories;
        private final java.util.List<String> waypoints;
        private final java.util.List<Point> routePoints = new ArrayList<>();
        private BufferedImage background;
        private double animT = 0.0;
        private long startMillis = 0L;
        private Timer timer;

        StaticChennaiMapPanel(String origin, String dest, String type, double distanceKm, double calories, java.util.List<String> waypoints) {
            this.origin = origin;
            this.dest = dest;
            this.type = type;
            this.distanceKm = distanceKm;
            this.calories = calories;
            this.waypoints = waypoints == null ? new ArrayList<>() : waypoints;
            setPreferredSize(new Dimension(600, 600));
            setBackground(new Color(230, 240, 250));
            buildRoute();
        }

        void setBackgroundImage(BufferedImage img) {
            this.background = img;
        }

        void startAnimation() {
            startMillis = System.currentTimeMillis();
            if (timer != null) timer.stop();
            timer = new Timer(120, e -> {
                animT += 0.004;
                if (animT > 1.0) animT = 0.0;
                repaint();
            });
            timer.start();
        }

        private Point seededPoint(String seed, int w, int h) {
            int hx = Math.abs(seed.hashCode());
            double nx = (hx % 10000) / 10000.0;
            int hy = Math.abs((seed + "_y").hashCode());
            double ny = (hy % 10000) / 10000.0;
            int margin = 60;
            int x = margin + (int) Math.round(nx * (w - 2 * margin));
            int y = margin + (int) Math.round(ny * (h - 2 * margin));
            return new Point(x, y);
        }

        private void buildRoute() {
            // Build placeholder positions based on names to keep positions stable
            // Actual pixel placement depends on panel size; recompute in paint using current W/H
            // We store seeds (names) and recompute points each paint
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(235, 255, 235));
            g2.fillRoundRect(10, 10, w - 20, h - 20, 20, 20);

            if (background != null) {
                int pad = 14;
                int aw = w - pad * 2;
                int ah = h - pad * 2;
                double sx = aw / (double) background.getWidth();
                double sy = ah / (double) background.getHeight();
                double s = Math.min(sx, sy);
                int dw = (int) Math.round(background.getWidth() * s);
                int dh = (int) Math.round(background.getHeight() * s);
                int dx = (w - dw) / 2;
                int dy = (h - dh) / 2;
                g2.setClip(new RoundRectangle2D.Float(10, 10, w - 20, h - 20, 20, 20));
                g2.drawImage(background, dx, dy, dw, dh, null);
                g2.setClip(null);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(230, 255, 230), 0, h, new Color(210, 240, 210));
                g2.setPaint(gp);
                g2.fillRoundRect(10, 10, w - 20, h - 20, 20, 20);
            }

            g2.setColor(new Color(200, 230, 200));
            for (int i = 60; i < w - 40; i += 60) g2.drawLine(20 + i, 40, 20 + i, h - 40);
            for (int j = 60; j < h - 40; j += 60) g2.drawLine(40, 20 + j, w - 40, 20 + j);

            g2.setStroke(new BasicStroke(18f));
            g2.setColor(new Color(170, 210, 170));
            g2.drawOval(w / 2 - 220, h / 2 - 200, 440, 400);

            g2.setStroke(new BasicStroke(6f));
            g2.setColor(new Color(120, 170, 120));
            g2.drawLine(40, h - 140, w - 60, 100);

            java.util.List<Point> pts = new ArrayList<>();
            pts.add(seededPoint("o:" + origin, w, h));
            for (String wp : waypoints) pts.add(seededPoint("w:" + wp, w, h));
            pts.add(seededPoint("d:" + dest, w, h));

            g2.setStroke(new BasicStroke(5f));
            g2.setColor(new Color(20, 150, 20));
            for (int i = 0; i < pts.size() - 1; i++) {
                Point a = pts.get(i), b = pts.get(i + 1);
                g2.drawLine(a.x, a.y, b.x, b.y);
            }

            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(0, 100, 0));
            for (int i = 0; i < pts.size() - 1; i++) {
                Point a = pts.get(i), b = pts.get(i + 1);
                int dx = b.x - a.x, dy = b.y - a.y;
                double len = Math.max(1.0, Math.hypot(dx, dy));
                for (int t = 0; t <= 10; t++) {
                    double k = t / 10.0;
                    int x = (int) Math.round(a.x + dx * k);
                    int y = (int) Math.round(a.y + dy * k);
                    double nx = dx / len, ny = dy / len;
                    int ax = (int) Math.round(x - 6 * nx - 4 * ny);
                    int ay = (int) Math.round(y - 6 * ny + 4 * nx);
                    int bx = (int) Math.round(x - 6 * nx + 4 * ny);
                    int by = (int) Math.round(y - 6 * ny - 4 * nx);
                    int[] px = {x, ax, bx};
                    int[] py = {y, ay, by};
                    g2.fillPolygon(px, py, 3);
                }
            }

            // Markers and labels
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i < pts.size(); i++) {
                Point p = pts.get(i);
                if (i == 0) {
                    g2.setColor(new Color(20, 140, 60));
                    g2.fillOval(p.x - 7, p.y - 7, 14, 14);
                    g2.setColor(new Color(0, 90, 0));
                    g2.drawString("Origin", p.x + 8, p.y - 8);
                } else if (i == pts.size() - 1) {
                    g2.setColor(new Color(200, 60, 60));
                    g2.fillOval(p.x - 7, p.y - 7, 14, 14);
                    g2.setColor(new Color(120, 0, 0));
                    g2.drawString("Destination", p.x + 8, p.y - 8);
                } else {
                    g2.setColor(new Color(0, 120, 0));
                    g2.fillOval(p.x - 5, p.y - 5, 10, 10);
                    g2.setColor(new Color(0, 80, 0));
                    String name = waypoints.get(i - 1);
                    g2.drawString(name, p.x + 8, p.y - 6);
                }
            }

            g2.setColor(new Color(0, 90, 0));
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("Chennai Fitness Map", w / 2 - 80, 35);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString("From: " + origin, 20, h - 55);
            g2.drawString("To: " + dest, 20, h - 40);
            g2.drawString(String.format("%s | %.2f km | %.0f kcal", type, distanceKm, calories), 20, h - 25);

            // Animate along total polyline
            double total = 0.0;
            for (int i = 0; i < pts.size() - 1; i++) {
                Point a = pts.get(i), b = pts.get(i + 1);
                total += Math.hypot(b.x - a.x, b.y - a.y);
            }
            double prog = Math.max(0, Math.min(1, animT));
            double target = total * prog;
            int rx = pts.get(0).x, ry = pts.get(0).y;
            double acc = 0.0;
            for (int i = 0; i < pts.size() - 1; i++) {
                Point a = pts.get(i), b = pts.get(i + 1);
                double seg = Math.hypot(b.x - a.x, b.y - a.y);
                if (acc + seg >= target) {
                    double k = (target - acc) / Math.max(1.0, seg);
                    rx = (int) Math.round(a.x + (b.x - a.x) * k);
                    ry = (int) Math.round(a.y + (b.y - a.y) * k);
                    break;
                }
                acc += seg;
            }
            g2.setColor(new Color(0, 160, 0));
            g2.fillOval(rx - 8, ry - 8, 16, 16);
            g2.setColor(new Color(0, 80, 0));
            g2.fillOval(rx - 4, ry - 14, 8, 8);
            g2.drawLine(rx, ry - 6, rx, ry + 10);
            g2.drawLine(rx, ry + 10, rx - 8, ry + 18);
            g2.drawLine(rx, ry + 10, rx + 8, ry + 18);
            g2.drawLine(rx, ry + 2, rx - 10, ry + 6);
            g2.drawLine(rx, ry + 2, rx + 10, ry + 6);

            long elapsed = (System.currentTimeMillis() - startMillis) / 1000;
            String tStr = String.format("Time: %02d:%02d", elapsed / 60, elapsed % 60);
            g2.setColor(new Color(0, 110, 0));
            g2.drawString(tStr, w - 100, h - 20);

            int legendX = w - 180, legendY = 50, lgw = 150, lgh = 80;
            g2.setColor(new Color(240, 255, 240, 220));
            g2.fillRoundRect(legendX, legendY, lgw, lgh, 12, 12);
            g2.setColor(new Color(0, 120, 0));
            g2.drawRoundRect(legendX, legendY, lgw, lgh, 12, 12);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Legend", legendX + 10, legendY + 18);
            g2.setStroke(new BasicStroke(5f));
            g2.setColor(new Color(20, 150, 20));
            g2.drawLine(legendX + 10, legendY + 35, legendX + 60, legendY + 35);
            g2.setColor(new Color(20, 140, 60));
            g2.fillOval(legendX + 10, legendY + 50, 10, 10);
            g2.setColor(new Color(200, 60, 60));
            g2.fillOval(legendX + 40, legendY + 50, 10, 10);
            g2.setColor(new Color(0, 90, 0));
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.drawString("Route", legendX + 70, legendY + 38);
            g2.drawString("Start", legendX + 25, legendY + 58);
            g2.drawString("End", legendX + 55, legendY + 58);

            g2.dispose();
        }
    }

    private void detectLocation() {
        statusLabel.setText("Detecting...");
        new Thread(() -> {
            try {
                double[] latlon = getLocationByIP();
                if (latlon != null) {
                    lastLat = latlon[0];
                    lastLon = latlon[1];
                    SwingUtilities.invokeLater(() -> {
                        locationLabel.setText(String.format("Lat: %.6f, Lon: %.6f", lastLat, lastLon));
                        statusLabel.setText("IP-based location");
                        mapButton.setEnabled(true);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Unable to detect location");
                        mapButton.setEnabled(false);
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error: " + ex.getMessage());
                    mapButton.setEnabled(false);
                });
            }
        }).start();
    }

    private double[] getLocationByIP() throws Exception {
        URL url = new URL("https://ipapi.co/json/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(8000);
        con.setReadTimeout(8000);
        int status = con.getResponseCode();
        if (status != 200) {
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        String json = content.toString();
        Double lat = extractNumber(json, "\\\"latitude\\\"\\s*:\\s*([-0-9.]+)");
        Double lon = extractNumber(json, "\\\"longitude\\\"\\s*:\\s*([-0-9.]+)");
        if (lat != null && lon != null) {
            return new double[]{lat, lon};
        }
        lat = extractNumber(json, "\\\"lat\\\"\\s*:\\s*([-0-9.]+)");
        lon = extractNumber(json, "\\\"lon\\\"\\s*:\\s*([-0-9.]+)");
        if (lat != null && lon != null) {
            return new double[]{lat, lon};
        }
        return null;
    }

    private Double extractNumber(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private void openMap() {
        if (!Double.isNaN(lastLat) && !Double.isNaN(lastLon)) {
            try {
                String q = String.format("https://www.google.com/maps?q=%f,%f", lastLat, lastLon);
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(q));
                }
            } catch (Exception ignored) {
            }
        }
    }

    // -------- Load / Save Details --------
    private void loadUserDetails() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT age, weight, height, preferences FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ageField.setText(String.valueOf(rs.getInt("age")));
                weightField.setText(String.valueOf(rs.getFloat("weight")));
                heightField.setText(String.valueOf(rs.getFloat("height")));
                preferencesArea.setText(rs.getString("preferences"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user details: " + e.getMessage());
        }
    }

    private void saveUserDetails() {
        try {
            int age = Integer.parseInt(ageField.getText());
            float weight = Float.parseFloat(weightField.getText());
            float height = Float.parseFloat(heightField.getText());
            String prefs = preferencesArea.getText();

            Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE users SET age=?, weight=?, height=?, preferences=? WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, age);
            ps.setFloat(2, weight);
            ps.setFloat(3, height);
            ps.setString(4, prefs);
            ps.setString(5, username);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Details updated successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating details: " + e.getMessage());
        }
    }

    // -------- Diet Plan Generator --------
    private void generateDietPlan() {
        try {
            int age = Integer.parseInt(ageField.getText());
            float weight = Float.parseFloat(weightField.getText());
            float height = Float.parseFloat(heightField.getText());
            String prefs = preferencesArea.getText();

            double bmi = weight / ((height / 100) * (height / 100));
            StringBuilder plan = new StringBuilder();
            plan.append("Your Personalized Diet Plan:\n\n");

            if (bmi < 18.5) {
                plan.append("- BMI: Underweight\n- Focus on calorie-rich, protein foods\n");
                dietArea.setBackground(new Color(255, 204, 153));
            } else if (bmi > 25) {
                plan.append("- BMI: Overweight\n- Reduce sugar & fats, focus on veggies & protein\n");
                dietArea.setBackground(new Color(255, 102, 102));
            } else {
                plan.append("- BMI: Normal\n- Maintain balanced diet\n");
                dietArea.setBackground(new Color(102, 204, 102));
            }

            plan.append("\nBreakfast: Oatmeal / Eggs / Fruits\n");
            plan.append("Lunch: Rice / Vegetables / Lean Protein\n");
            plan.append("Snack: Nuts / Fruits / Yogurt\n");
            plan.append("Dinner: Light meal with Protein & Veggies\n");

            if (!prefs.isEmpty()) plan.append("\nPreferences/Goals: ").append(prefs);

            dietArea.setText(plan.toString());

            JOptionPane.showMessageDialog(this,
                    "Your BMI is " + String.format("%.1f", bmi),
                    "BMI Status",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid details to generate diet plan.");
        }
    }

    private void downloadPlan() {
        if (dietArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate diet plan first!");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Diet Plan");
        fileChooser.setSelectedFile(new java.io.File("DietPlan_" + username + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(dietArea.getText());
                JOptionPane.showMessageDialog(this, "Diet plan saved!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }

    // -------- GPS Popup for Manual Activity Entry --------
    private void showActivityPopup() {
        JDialog popup = new JDialog(this, "Track New Activity", true);
        popup.setSize(400, 350);
        popup.setLayout(new GridLayout(6, 2, 10, 10));
        popup.setLocationRelativeTo(this);

        JLabel typeLabel = new JLabel("Activity Type:");
        String[] types = {"Cycling", "Running", "Walking"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JLabel distanceLabel = new JLabel("Distance (km):");
        JTextField distanceField = new JTextField();

        JLabel calorieLabel = new JLabel("Calories Burned:");
        JTextField calorieField = new JTextField();

        JButton saveBtn = new JButton("Save Activity");
        saveBtn.setBackground(new Color(46, 139, 87));
        saveBtn.setForeground(Color.WHITE);

        popup.add(typeLabel);
        popup.add(typeBox);
        popup.add(distanceLabel);
        popup.add(distanceField);
        popup.add(calorieLabel);
        popup.add(calorieField);
        popup.add(new JLabel(""));
        popup.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String type = (String) typeBox.getSelectedItem();
                double distance = Double.parseDouble(distanceField.getText());
                double calories = Double.parseDouble(calorieField.getText());

                double baseLat = 12.8235;
                double baseLng = 80.0452;
                double randomLat = baseLat + (Math.random() * 0.004 - 0.002);
                double randomLng = baseLng + (Math.random() * 0.004 - 0.002);

                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO gps_tracking (username, activity_type, latitude, longitude, distance_km, calories_burned) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, username);
                ps.setString(2, type);
                ps.setDouble(3, randomLat);
                ps.setDouble(4, randomLng);
                ps.setDouble(5, distance);
                ps.setDouble(6, calories);
                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(popup, "Activity Saved ✅");
                popup.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(popup, "Error: " + ex.getMessage());
            }
        });

        popup.setVisible(true);
    }

    // -------- GPS Summary --------
    private void viewGPSReport() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT SUM(distance_km) AS total_distance, SUM(calories_burned) AS total_calories FROM gps_tracking WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double totalDist = rs.getDouble("total_distance");
                double totalCals = rs.getDouble("total_calories");
                JOptionPane.showMessageDialog(this,
                        "📊 GPS Activity Summary:\n\n" +
                                "Total Distance: " + String.format("%.2f", totalDist) + " km\n" +
                                "Total Calories: " + String.format("%.1f", totalCals) + " kcal");
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching report: " + ex.getMessage());
        }
    }

    // -------- Goal Tracker --------
    private void openGoalTracker() {
        JFrame goalFrame = new JFrame("Goal Tracker");
        goalFrame.setSize(600, 400);
        goalFrame.setLocationRelativeTo(this);
        goalFrame.setLayout(new BorderLayout());

        JTextArea goalList = new JTextArea();
        goalList.setEditable(false);
        JScrollPane scroll = new JScrollPane(goalList);
        goalFrame.add(scroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JTextField titleField = new JTextField();
        JTextField targetField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JButton addGoalBtn = new JButton("Add Goal");

        inputPanel.add(new JLabel("Goal:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Target:"));
        inputPanel.add(targetField);
        inputPanel.add(new JLabel("Deadline:"));
        inputPanel.add(dateField);
        inputPanel.add(addGoalBtn);

        goalFrame.add(inputPanel, BorderLayout.NORTH);
        loadGoals(goalList);

        addGoalBtn.addActionListener(e -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO goals (username, goal_title, target, deadline) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, titleField.getText());
                ps.setString(3, targetField.getText());
                ps.setString(4, dateField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(goalFrame, "Goal added!");
                loadGoals(goalList);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(goalFrame, "Error: " + ex.getMessage());
            }
        });

        goalFrame.setVisible(true);
    }

    private void loadGoals(JTextArea goalList) {
        goalList.setText("");
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT goal_title, target, deadline, status FROM goals WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                goalList.append("🎯 " + rs.getString("goal_title") +
                        " | Target: " + rs.getString("target") +
                        " | Deadline: " + rs.getString("deadline") +
                        " | Status: " + rs.getString("status") + "\n");
            }
        } catch (SQLException e) {
            goalList.append("Error loading goals: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Shivam"));
    }
}
