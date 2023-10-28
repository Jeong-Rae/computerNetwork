package computerNetwork.gui;

import computerNetwork.http.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwingApp {
    private int width = 720;
    private int height = 1080;

    private static SwingApp instance;
    private String method = "GET";
    private String url = "https://api.interfacesejong.xyz/login.html";
    private String contentType;


    private final JTextArea requestField = new JTextArea(8, 70);
    private final JTextArea responseField = new JTextArea(12, 70);

    private JTable paramsTable;
    private JTable headersTable;
    private JTable bodyTable;

    private JRadioButton noneRadioButton;
    private JRadioButton urlEncodedRadioButton;
    private JRadioButton jsonRadioButton;
    private JRadioButton formDataRadioButton;

    private SwingApp(){

        JFrame frame = new JFrame("Http Client");
        frame.setSize(width + 100, height);
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
        urlField.setText(url);
        urlField.addActionListener(e -> {
            url = urlField.getText();
            System.out.println("[gui]url : " + url);
        });
        requestPanel.add(urlField);

        // request send 버튼
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            Map<String, String> params = tableToMap(paramsTable);
            Map<String, String> headers = tableToMap(headersTable);
            Map<String, String> body = tableToMap(bodyTable);

            HttpClient httpClient = new HttpClient(params, headers, body, contentType);

            Map<String, String> ret = httpClient.sendRequest(url, method);
            responseField.setText(ret.get("response"));
            requestField.setText(ret.get("request"));
        });
        requestPanel.add(sendButton);

        frame.add(requestPanel);

        // params table
        paramsTable = createTable("Params", 3, frame);
        // headers table
        headersTable = createTable("Headers", 3, frame);

        // contentType 라디오 버튼
        frame.add(createContentRadioButton());

        // Body table
        bodyTable = createTable("Body", 4, frame);

        // Request field
        requestField.setMargin(new Insets(10, 10, 10, 10));
        requestField.setEditable(false);
        JScrollPane requestScrollPane = new JScrollPane(requestField);

        frame.add(requestScrollPane);


        // Response field
        responseField.setMargin(new Insets(10, 10, 10, 10));
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
        scrollPane.setPreferredSize(new Dimension(width, rows * 23 + 25));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        tablePanel.setPreferredSize(new Dimension(width, rows * 23 + 25 + label.getPreferredSize().height));

        frame.add(tablePanel);

        return table;
    }

    private JPanel createContentRadioButton() {
        // Radio buttons for body content type
        noneRadioButton = new JRadioButton("none");
        urlEncodedRadioButton = new JRadioButton("x-www-form-urlencoded");
        jsonRadioButton = new JRadioButton("json");
        formDataRadioButton = new JRadioButton("form-data");

        noneRadioButton.addActionListener(e -> updateContentType());
        urlEncodedRadioButton.addActionListener(e -> updateContentType());
        jsonRadioButton.addActionListener(e -> updateContentType());
        formDataRadioButton.addActionListener(e -> updateContentType());

        ButtonGroup bodyContentTypeGroup = new ButtonGroup();
        bodyContentTypeGroup.add(noneRadioButton);
        bodyContentTypeGroup.add(urlEncodedRadioButton);
        bodyContentTypeGroup.add(jsonRadioButton);
        bodyContentTypeGroup.add(formDataRadioButton);

        noneRadioButton.setSelected(true);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(noneRadioButton);
        radioPanel.add(urlEncodedRadioButton);
        radioPanel.add(jsonRadioButton);
        radioPanel.add(formDataRadioButton);

        return radioPanel;
    }

    private void updateContentType() {
        if (noneRadioButton.isSelected()) {
            contentType = "";
        } else if (urlEncodedRadioButton.isSelected()) {
            contentType = "application/x-www-form-urlencoded";
        } else if (jsonRadioButton.isSelected()) {
            contentType = "application/json";
        } else if (formDataRadioButton.isSelected()) {
            contentType = "multipart/form-data";
        }
    }

    private Map<String, String> tableToMap(JTable table){
        Map<String, String> map = new HashMap<>();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String key = (String) tableModel.getValueAt(i, 0);
            String value = (String) tableModel.getValueAt(i, 1);
            if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                map.put(key, value);
            }
        }
        return map;
    }

    public static SwingApp getInstance() throws IOException {
        if (instance == null) {
            instance = new SwingApp();
        }
        return instance;
    }
}
