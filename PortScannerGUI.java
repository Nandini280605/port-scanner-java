import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortScannerGUI {

    static volatile boolean isScanning = false;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Advanced Port Scanner");
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.DARK_GRAY);

        JTextField hostField = new JTextField("google.com", 12);
        JTextField startField = new JTextField("1", 5);
        JTextField endField = new JTextField("1000", 5);

        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");

        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);
        topPanel.add(new JLabel("Start:"));
        topPanel.add(startField);
        topPanel.add(new JLabel("End:"));
        topPanel.add(endField);
        topPanel.add(startBtn);
        topPanel.add(stopBtn);

        // Center Output
        JTextArea output = new JTextArea();
        output.setBackground(Color.BLACK);
        output.setForeground(Color.GREEN);
        output.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(output);

        // Bottom Progress Bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(progressBar, BorderLayout.SOUTH);

        // Thread Pool (IMPORTANT)
        ExecutorService executor = Executors.newFixedThreadPool(100);

        // START BUTTON
        startBtn.addActionListener(e -> {

            isScanning = true;
            output.setText("");

            String host = hostField.getText();
            int startPort = Integer.parseInt(startField.getText());
            int endPort = Integer.parseInt(endField.getText());

            int totalPorts = endPort - startPort + 1;
            progressBar.setMaximum(totalPorts);

            new Thread(() -> {
                int count = 0;

                for (int port = startPort; port <= endPort; port++) {

                    if (!isScanning) break;

                    int currentPort = port;

                    executor.execute(() -> {
                        try {
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(host, currentPort), 100);

                            SwingUtilities.invokeLater(() ->
                                    output.append("Port " + currentPort + " is OPEN\n"));

                            socket.close();
                        } catch (Exception ex) {
                            // ignore
                        }
                    });

                    count++;

                    int finalCount = count;
                    SwingUtilities.invokeLater(() ->
                            progressBar.setValue(finalCount));
                }
            }).start();
        });

        // STOP BUTTON
        stopBtn.addActionListener(e -> {
            isScanning = false;
            output.append("\nScanning Stopped.\n");
        });

        frame.setVisible(true);
    }
}