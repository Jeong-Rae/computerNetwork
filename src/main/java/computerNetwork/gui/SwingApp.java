package computerNetwork.gui;

import computerNetwork.http.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwingApp {

    private static SwingApp instance;
    private String response;
    private String method = "GET";
    private String url;


    private final JTextArea requestField = new JTextArea(5, 80);
    private final JTextArea responseField = new JTextArea(13, 80);

    private JTable paramsTable;
    private JTable headersTable;
    private JTable bodyTable;

    private SwingApp(){
        HttpClient httpClient = new HttpClient();

        JFrame frame = new JFrame("Http Client");
        frame.setSize(1080, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new FlowLayout());

        JPanel requestPanel = new JPanel(new FlowLayout());

        // Http method 선택 상자
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        JComboBox<String> comboBox = new JComboBox<>(methods);
        comboBox.addActionListener(e -> {
            method = (String) comboBox.getSelectedItem();
        });
        requestPanel.add(comboBox);

        // URL 입력
        JTextField urlField = new JTextField(35);
        //urlField.setText(url);
        urlField.addActionListener(e -> {
            url = urlField.getText();
            System.out.println(url);
        });
        requestPanel.add(urlField);


        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            /*
            Map<String, String> params = new HashMap<>();
            DefaultTableModel tableModel;
            tableModel = (DefaultTableModel) paramsTable.getModel();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String key = (String) tableModel.getValueAt(i, 0);
                String value = (String) tableModel.getValueAt(i, 1);
                if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                    params.put(key, value);
                }
            }
            Map<String, String> headers = new HashMap<>();
            tableModel = (DefaultTableModel) headersTable.getModel();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String key = (String) tableModel.getValueAt(i, 0);
                String value = (String) tableModel.getValueAt(i, 1);
                if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                    params.put(key, value);
                }
            }
            Map<String, String> body = new HashMap<>();
            tableModel = (DefaultTableModel) bodyTable.getModel();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String key = (String) tableModel.getValueAt(i, 0);
                String value = (String) tableModel.getValueAt(i, 1);
                if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                    params.put(key, value);
                }
            }*/

            Map<String, String> ret = httpClient.sendRequest(url, method);
            System.out.println(response);
            responseField.setText(ret.get("response"));
            requestField.setText(ret.get("request"));
        });

        requestPanel.add(sendButton);

        frame.add(requestPanel);

        // PARAMS table
        paramsTable = createTable("Params", 3, frame);
        // HEADER table
        headersTable = createTable("Headers", 3, frame);
        // BODY table
        bodyTable = createTable("Body", 4, frame);

        // Request field
        requestField.setEditable(false);
        JScrollPane requestScrollPane = new JScrollPane(requestField);

        frame.add(requestScrollPane);


        // Response field
        responseField.setEditable(false);
        JScrollPane responseScrollPane = new JScrollPane(responseField);

        frame.add(responseScrollPane);

        frame.setVisible(true);
    }

    private JTable createTable(String labelText, int rows, JFrame frame) {
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

        tablePanel.setPreferredSize(new Dimension(1060, rows * 23 + 25 + label.getPreferredSize().height));

        frame.add(tablePanel);

        return table;
    }


    public static SwingApp getInstance() throws IOException {
        if (instance == null) {
            instance = new SwingApp();
        }
        return instance;
    }
}
