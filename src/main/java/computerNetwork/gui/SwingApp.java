package computerNetwork.gui;

import computerNetwork.http.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SwingApp {

    private static SwingApp instance;
    private String method = "GET";
    private String url;

    private String response;

    private SwingApp() throws IOException {
        HttpClient httpClient = new HttpClient();

        JFrame frame = new JFrame("Rest Client");
        frame.setSize(1080, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting FlowLayout to the frame
        frame.setLayout(new FlowLayout());

        // Panel for ComboBox and Text field
        JPanel requestPanel = new JPanel(new FlowLayout());

        // ComboBox
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        JComboBox<String> comboBox = new JComboBox<>(methods);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                method = (String) comboBox.getSelectedItem();
                System.out.println(method);
            }
        });
        requestPanel.add(comboBox);

        // Text field
        JTextField urlField = new JTextField(35);
        urlField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                url = urlField.getText();
                System.out.println(url);
            }
        });
        requestPanel.add(urlField);

        frame.add(requestPanel);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                response = httpClient.sendRequest(url, method);
            }
        });

        frame.add(sendButton);


        // PARAMS table
        createLabelAndTable("Params", 3, frame);
        // HEADER table
        createLabelAndTable("Header", 3, frame);
        // BODY table
        createLabelAndTable("Body", 4, frame);

        JTextArea requestField = new JTextArea(5, 80);
        requestField.setEditable(false);
        JScrollPane requestScrollPane = new JScrollPane(requestField);
        frame.add(requestScrollPane);

        // Response field
        JTextArea responseField = new JTextArea(13, 80);
        responseField.setEditable(false);
        JScrollPane responseScrollPane = new JScrollPane(responseField);
        frame.add(responseScrollPane);

        frame.setVisible(true);
    }

    private void createLabelAndTable(String labelText, int rows, JFrame frame) {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        JLabel label = new JLabel(labelText);
        tablePanel.add(label, BorderLayout.NORTH);

        String[] columnNames = {"Key", "Value"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        JTable table = new JTable(model);
        for (int i = 0; i < rows; i++) {
            model.addRow(new Object[]{"", ""});
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1060, rows * 23 + 25));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Set the preferred size of the tablePanel based on the preferred size of the JScrollPane.
        tablePanel.setPreferredSize(new Dimension(1060, rows * 23 + 25 + label.getPreferredSize().height));

        frame.add(tablePanel);
    }


    public static SwingApp getInstance() throws IOException {
        if (instance == null) {
            instance = new SwingApp();
        }
        return instance;
    }
}
