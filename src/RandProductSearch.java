import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandProductSearch extends JFrame {

    // ===== ENTRY FIELD STRING LENGTHS ===== //
    private final int NAME_FIELD_LENGTH = 35;
    private final int DESC_FIELD_LENGTH = 75;
    private final int ID_FIELD_LENGTH = 6;
    private final int COST_FIELD_LENGTH = 8;
    private final int RECORD_SIZE = NAME_FIELD_LENGTH + DESC_FIELD_LENGTH + ID_FIELD_LENGTH + COST_FIELD_LENGTH;
    //Record Length should add up to 124 chars, therefore 124 bytes

    //Note: File format is ProductID, ProductName, Description, Cost. Name is just displayed first for the user entry form.

    // ===== PRODUCT ARRAY LIST DECLARATIONS ===== //

    private ArrayList<Product> allProductRecs;
    private ArrayList<Product> searchedProductRecs;
    private boolean firstFileLoaded = false; //later feature: autoload first selected file, others need to be clicked

    // ===== GUI DECLARATIONS ===== //

    private JTextField nameField;
    private JLabel headingLabel, nameLabel, returnedEntriesLabel;
    private int returnedEntriesNum = 0;

    FilePicker loadFilePicker;

    private JTable productTable;
    private ProductTableModel tableModel;

    private JPanel mainPanel, topPanel, upperTopPanel, nestedPanel1, nestedPanel2, formPanel;

    public RandProductSearch() {
        setTitle("Product Search");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //Main Panel, created for slightly easier border padding
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        // ===== TOP PANEL  ===== // contains Heading, FilePicker, Search Form, and Buttons

        topPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== UPPER TOP PANEL ===== // North subpanel of TopPanel. Contains Heading and FilePicker
        upperTopPanel = new JPanel(new BorderLayout(10, 10));
        headingLabel = new JLabel("Product Search");
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 10));

        loadFilePicker = new FilePicker("Loaded Product File:", "Browse...");
        loadFilePicker.setBorder(BorderFactory.createEmptyBorder(0, 74, 0, 0));
        loadFilePicker.setMode(FilePicker.MODE_OPEN);
        loadFilePicker.addFileTypeFilter(".dat", "dat binary files");
        loadFilePicker.addFileTypeFilter(".bin", "binary files");
        nestedPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nestedPanel1.add(loadFilePicker); // nested panels used for better alignment with form fields
        JPanel pickerPanel = new JPanel();
        pickerPanel.setLayout(new BoxLayout(pickerPanel, BoxLayout.Y_AXIS));
        pickerPanel.add(nestedPanel1);
        upperTopPanel.add(headingLabel, BorderLayout.NORTH);
        upperTopPanel.add(pickerPanel, BorderLayout.CENTER);
        topPanel.add(upperTopPanel, BorderLayout.NORTH);

        // ======= FORM PANEL ========= Top of Main Panel
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(25);

        nameLabel = new JLabel("Search by Name:");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        topPanel.add(formPanel, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear Search");
        JButton loadButton = new JButton("Load");
        JButton quitButton = new JButton("Quit");

        quitButton.addActionListener(e -> onQuitButtonClicked());
        clearButton.addActionListener(e -> onClearButtonClicked());
        loadButton.addActionListener(e -> onLoadButtonClicked());
        searchButton.addActionListener(e -> onSearchButtonClicked());

        buttonPanel.add(quitButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 60, 0, 0));

        returnedEntriesLabel = new JLabel("Returned Entries: " + returnedEntriesNum);
        returnedEntriesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        returnedEntriesLabel.setFont(returnedEntriesLabel.getFont().deriveFont(14f));

        JPanel lowerTopPanel = new JPanel();
        lowerTopPanel.setLayout(new BoxLayout(lowerTopPanel, BoxLayout.Y_AXIS));
        lowerTopPanel.add(buttonPanel);
        lowerTopPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        lowerTopPanel.add(returnedEntriesLabel);

        topPanel.add(lowerTopPanel, BorderLayout.SOUTH);

        // ===== TABLE ====== Displays current entries, and highlights new entries
        tableModel = new ProductTableModel(new ArrayList<>());
        productTable = new JTable(tableModel);
        centerTableCells(productTable);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(700, 200));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);


        // ===== END GUI CONSTRUCTION ===== //

        this.allProductRecs = new ArrayList<>();
        this.searchedProductRecs = new ArrayList<>();

        System.out.println(RECORD_SIZE);
    }

    /**
     * Called when the Quit Button is clicked. Confirms whether the user wants to exit,
     * and closes the program if they answer yes.
     */
    private void onQuitButtonClicked() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit?\nSearch data will not be saved.",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Called when Load button is clicked, and a file is selected. Attempts
     * to load the selected file. Next function down the stack handles error catching.
     */
    private void onLoadButtonClicked() {
        if (loadFilePicker.getIsFileSelected() == true) {
            loadDataFromFile(loadFilePicker.getSelectedFilePath());
        }
    }

    /**
     * Called when the clear button is clicked. Confirms that the user wants to clear
     * fields and reset the added entries to the table, which aren't saved until the user
     * clicks save.
     */
    private void onClearButtonClicked() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Clear search? This will still leave the original file contents.",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (response == JOptionPane.YES_OPTION) {
            searchedProductRecs.clear();
            this.clearFormFields();
            tableModel.setProducts(allProductRecs);
            productTable.repaint();
        }
    }

    private void onSearchButtonClicked() {
        if (nameField.getText().trim().equals("")) {
            return; //silently nothing if no search value is entered
        }
        String searchQuery = nameField.getText();
        List<Product> results = searchByName(allProductRecs, searchQuery);

        //check if any results were returned, display message if no
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No Products found matching: " + searchQuery);
            return;
        }
        this.searchedProductRecs.clear();
        this.searchedProductRecs.addAll(results);
        tableModel.setProducts(searchedProductRecs);
        productTable.repaint();
        returnedEntriesNum = searchedProductRecs.size();
        returnedEntriesLabel.setText("Matching Products: " + returnedEntriesNum);
    }

    /**
     * Simple function that returns products whose name contains a given Search String. Result can easily be plugged
     * into table model for easy viewing.
     * @param allProducts List of all products that will be searched.
     * @param searchTerm SubString that will be used searched for within Product names
     * @return a List of matching Products
     */
    private List<Product> searchByName(List<Product> allProducts, String searchTerm) {
        return allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    private String pad(String s, int length) {
        if (s.length() > length) {
            return s.substring(0, length); // truncate
        }
        return String.format("%-" + length + "s", s); // pad with spaces
    }

    /**
     * Top level load function called when the load button is clicked, and the user
     * has a file selected. Sets the displayed JTable to display the product information.
     * @param filePath Path returned by the FilePicker
     */
    private void loadDataFromFile(Path filePath) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            List<Product> products = loadAllProducts(raf);
            tableModel.setProducts(products);
            allProductRecs.clear();
            allProductRecs.addAll(products);
            searchedProductRecs.clear();;
            firstFileLoaded = true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to load data from file: " + e.getMessage());
            return;

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public List<Product> loadAllProducts(RandomAccessFile raf) throws IOException {
        List<Product> productList = new ArrayList<>();

        long fileLength = raf.length();
        long numRecords = fileLength / RECORD_SIZE;

        raf.seek(0);

        for (int i = 0; i < numRecords; i++) {
            productList.add(readProductAtIndex(raf, i));
        }
        return productList;
    }

    public Product readProductAtIndex(RandomAccessFile raf, int index) throws IOException {
        raf.seek(index * RECORD_SIZE);

        byte[] idBytes = new byte[ID_FIELD_LENGTH];
        byte[] nameBytes = new byte[NAME_FIELD_LENGTH];
        byte[] descBytes = new byte[DESC_FIELD_LENGTH];

        raf.readFully(idBytes);
        raf.readFully(nameBytes);
        raf.readFully(descBytes);
        double cost = raf.readDouble(); //already reads 8 bytes, so no need to set byte variable

        return new Product(
                new String(idBytes).trim(),
                new String(nameBytes).trim(),
                new String(descBytes).trim(),
                cost
        );

    }

    private void clearFormFields() {
        nameField.setText("");
        returnedEntriesNum = 0;
        returnedEntriesLabel.setText("returned entries count: " + returnedEntriesNum);
    }

    private void centerTableCells(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        //center cell contents
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        //Center column headers
        JTableHeader header = table.getTableHeader();
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void testProductList() {
        for (Product product : allProductRecs) {
            System.out.println(product);
        }
    }

    public static void main(String[] args) {
        new RandProductSearch();
    }


}
