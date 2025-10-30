 import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dashboard extends JFrame {
    JLabel locationLabel;
    JLabel statusLabel;
    JButton detectButton;
    JButton mapButton;
    double lastLat = Double.NaN;
    double lastLon = Double.NaN;

    public Dashboard(String username) {
        setTitle("Food Diet Planner - Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setBounds(20, 20, 400, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel);

        JLabel gpsTitle = new JLabel("Location (Approximate)");
        gpsTitle.setBounds(20, 70, 300, 24);
        gpsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        add(gpsTitle);

        locationLabel = new JLabel("Lat: -, Lon: -");
        locationLabel.setBounds(20, 100, 300, 22);
        add(locationLabel);

        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 130, 440, 22);
        statusLabel.setForeground(new Color(90, 90, 90));
        add(statusLabel);

        detectButton = new JButton("Detect Location");
        detectButton.setBounds(20, 165, 150, 30);
        add(detectButton);

        mapButton = new JButton("Open in Maps");
        mapButton.setBounds(180, 165, 140, 30);
        mapButton.setEnabled(false);
        add(mapButton);

        detectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detectLocation();
            }
        });

        mapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMap();
            }
        });

        setVisible(true);
    }

    private void detectLocation() {
        statusLabel.setText("Detecting location...");
        new Thread(() -> {
            try {
                double[] latlon = getLocationByIP();
                if (latlon != null) {
                    lastLat = latlon[0];
                    lastLon = latlon[1];
                    SwingUtilities.invokeLater(() -> {
                        locationLabel.setText(String.format("Lat: %.6f, Lon: %.6f", lastLat, lastLon));
                        statusLabel.setText("Detected via IP geolocation. Accuracy may vary.");
                        mapButton.setEnabled(true);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Unable to detect location.");
                        mapButton.setEnabled(false);
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error detecting location: " + ex.getMessage());
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
}
