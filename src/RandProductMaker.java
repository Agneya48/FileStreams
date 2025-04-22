import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RandProductMaker extends JFrame {

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
    private ArrayList<Product> originalProductRecs;
    private Set<Product> highlightedProducts = new HashSet<>();
    private boolean firstFileLoaded = false; //will autoload first selected file, others need to be clicked
    private int newEntries; //keeps track of new Entry rows


    // ===== GUI DECLARATIONS ===== //

    private JTextField nameField, descField, idField, costField, rowSelectField;
    private JLabel headingLabel, nameLabel, descLabel, idLabel,
            costLabel, rowSelectLabel, entryNumLabel;

    FilePicker loadFilePicker;
    FilePicker saveFilePicker;

    private JTable productTable;
    private ProductTableModel tableModel;

    private JPanel mainPanel, topPanel, upperTopPanel, nestedPanel1, nestedPanel2, formPanel;


    public RandProductMaker() {
        setTitle("Product Entry");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //Main Panel, created for slightly easier border padding
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        // ===== TOP PANEL  ===== // contains Heading, FilePicker, Entry Forms, and Buttons

        topPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== UPPER TOP PANEL ===== // North subpanel of TopPanel. Contains Heading and FilePicker
        upperTopPanel = new JPanel(new BorderLayout(10, 10));
        headingLabel = new JLabel("Product Entry");
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 20));

        loadFilePicker = new FilePicker("Loaded Product File:", "Browse...");
        loadFilePicker.setBorder(BorderFactory.createEmptyBorder(0, 66, 0, 0));
        loadFilePicker.setMode(FilePicker.MODE_OPEN);
        saveFilePicker = new FilePicker("Save File Destination:", "Browse...");
        saveFilePicker.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 0));
        saveFilePicker.setMode(FilePicker.MODE_SAVE);
        saveFilePicker.addFileTypeFilter(".dat", "dat binary files");
        saveFilePicker.addFileTypeFilter(".bin", "binary files");
        loadFilePicker.addFileTypeFilter(".dat", "dat binary files");
        loadFilePicker.addFileTypeFilter(".bin", "binary files");
        nestedPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nestedPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nestedPanel1.add(loadFilePicker); // nested panels used for better alignment with form fields
        nestedPanel2.add(saveFilePicker);
        JPanel pickerPanel = new JPanel();
        pickerPanel.setLayout(new BoxLayout(pickerPanel, BoxLayout.Y_AXIS));
        pickerPanel.add(nestedPanel1);
        pickerPanel.add(nestedPanel2);
        upperTopPanel.add(headingLabel, BorderLayout.NORTH);
        upperTopPanel.add(pickerPanel, BorderLayout.CENTER);
        topPanel.add(upperTopPanel, BorderLayout.NORTH);

        // ======= FORM PANEL ========= Top of Main Panel
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(25);
        descField = new JTextField(25);
        idField = new JTextField(10);
        costField = new JTextField(10);
        rowSelectField = new JTextField(10);

        nameLabel = new JLabel("Product Name:");
        descLabel = new JLabel("Description:");
        idLabel = new JLabel("ID (6 chars):");
        costLabel = new JLabel("Cost:");
        rowSelectLabel = new JLabel("(Opt) Row:");
        entryNumLabel = new JLabel("Entries:");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(descField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(costLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(costField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(rowSelectLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(rowSelectField, gbc);

        topPanel.add(formPanel, BorderLayout.CENTER);


        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Entry");
        JButton clearButton = new JButton("Clear Entries");
        JButton loadButton = new JButton("Load");
        JButton saveButton = new JButton("Save to File");
        JButton quitButton = new JButton("Quit");

        quitButton.addActionListener(e -> onQuitButtonClicked());
        clearButton.addActionListener(e -> onClearButtonClicked());
        loadButton.addActionListener(e -> onLoadButtonClicked());
        saveButton.addActionListener(e -> onSaveButtonClicked());
        addButton.addActionListener(e -> onAddButtonClicked());

        buttonPanel.add(quitButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        JPanel lowerTopPanel = new JPanel();
        lowerTopPanel.setLayout(new BoxLayout(lowerTopPanel, BoxLayout.Y_AXIS));
        entryNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        entryNumLabel.setFont(entryNumLabel.getFont().deriveFont(14f));
        entryNumLabel.setText("New Entries: " + newEntries);
        lowerTopPanel.add(buttonPanel);
        lowerTopPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        lowerTopPanel.add(entryNumLabel);

        topPanel.add(lowerTopPanel, BorderLayout.SOUTH);

        // ===== TABLE ====== Displays current entries, and highlights new entries
        tableModel = new ProductTableModel(new ArrayList<>());
        productTable = new JTable(tableModel);
        centerTableCells(productTable);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(700, 200));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        this.applyCustomRowHighlighting();

        this.setVisible(true);


        // ===== END GUI CONSTRUCTION ===== //

        this.allProductRecs = new ArrayList<>();
        this.originalProductRecs = new ArrayList<>();

        System.out.println(RECORD_SIZE);
    }

    /**
     * Called when the Quit Button is clicked. Confirms whether the user wants to exit,
     * and closes the program if they answer yes.
     */
    private void onQuitButtonClicked() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit?\nAny unsaved changes will be lost.",
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

    private void onSaveButtonClicked() {
        if (saveFilePicker.getIsFileSelected() == true) {
            saveAllProducts(saveFilePicker.getSelectedFilePath(), allProductRecs);
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
            "Clear form fields and added Product entries? Any unsaved data will be lost.",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (response == JOptionPane.YES_OPTION) {
            allProductRecs.clear();
            highlightedProducts.clear();
            originalProductRecs.clear();
            this.clearFormFields();
            tableModel.setProducts(allProductRecs);
            productTable.repaint();
        }
    }

    private void onAddButtonClicked() {
        //first, validate field input

        try {
            if (idField.getText().trim().length() != 6) {
                JOptionPane.showMessageDialog(this, "ID must be 6 characters.");
                return;
            }
            if (costField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cost is a required field.");
                return;
            }
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            double cost = Double.parseDouble(costField.getText().trim());

            if (id.isEmpty() || name.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All required fields must be filled.");
                return;
            }
            if (cost < 0) {
                JOptionPane.showMessageDialog(this, "Cost cannot be negative.");
                return;
            }

            //If nothing went wrong in previous checks, we'll assume the data is safe to write
            Product product = new Product(id, name, description, cost);

            //optional row index field also needs to validated, but if it fails should still continue
            //if it fails, it should just be ignored, and product will be silently appended at end of table
            int insertIndex = tableModel.getRowCount(); //default: append to end
            String indexText = rowSelectField.getText().trim();

            if (!indexText.isEmpty()) {
                try {
                    int parsedIndex = Integer.parseInt(indexText);
                    if (parsedIndex >= 0 && parsedIndex <= tableModel.getRowCount()) {
                        insertIndex = parsedIndex; //only use if valid
                    } else{
                        //silently ignore and fallback to append
                    }
                } catch (NumberFormatException e) {
                    //silently ignore and fallback gto append
                }
            }

            // Add product
            this.insertProductAt(product, insertIndex);
            tableModel.insertProductsAt(product, insertIndex);
            newEntries++;
            entryNumLabel.setText("New Entries: " + newEntries);
            productTable.repaint();

            clearFormFields();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Input for Cost. Please enter a valid number.");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

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
            originalProductRecs.clear();
            originalProductRecs.addAll(products);
            highlightedProducts.clear();
            allProductRecs.clear();
            allProductRecs.addAll(products);
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

    /**
     * Saves the given Product List to the designated FilePath. Uses RandomAccessFile to iterate over every
     * product entry, with a fixed byte length for each. Resets stats that track unsaved new rows on a successful save.
     * @param filePath Path where file will be saved. Must be a dat or bin file.
     * @param products List of Products that will be saved to the file.
     */
    public void saveAllProducts(Path filePath, List<Product> products) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            raf.setLength(0); // clear existing file contents

            for (int i = 0; i < products.size(); i++) {
                writeProductAtIndex(raf, products.get(i), i);
            }

            JOptionPane.showMessageDialog(this, "Products saved successfully to file.");

            //clear tracked changes
            highlightedProducts.clear();
            productTable.repaint();
            newEntries = 0;
            entryNumLabel.setText("New Entries: " + newEntries);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Inserts a product at the given index in the productList, or at the end
     * if the given index is out of range. Also highlights the rows that are new, unsaved entries.
     * @param product The new product to be inserted
     * @param index list index. Starts at 0
     */
    public void insertProductAt(Product product, int index) {
        if (index < 0 || index > allProductRecs.size()) {
            index = allProductRecs.size(); //add to end if index is out of range
        }
        allProductRecs.add(index, product);
        highlightedProducts.add(product);
        tableModel.fireTableRowsInserted(index, index);
    }

    public void writeProductAtIndex(RandomAccessFile raf, Product product, int index) throws IOException {
        raf.seek(index * RECORD_SIZE); //move to correct byte position
        raf.writeBytes(pad(product.getIDNum(), ID_FIELD_LENGTH));
        raf.writeBytes(pad(product.getName(), NAME_FIELD_LENGTH));
        raf.writeBytes(pad(product.getDescription(), DESC_FIELD_LENGTH));
        raf.writeDouble(product.getCost());
    }

    private void clearFormFields() {
        nameField.setText("");
        descField.setText("");
        idField.setText("");
        costField.setText("");
        rowSelectField.setText("");
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
        System.out.println("Now New Products");
        for (Product product : highlightedProducts) {
            System.out.println(product);
        }
    }

    /**
     * Custom render change that highlights newly added rows that have yet to be saved.
     */
    private void applyCustomRowHighlighting() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                               boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Color.YELLOW);

                Product productAtRow = tableModel.getProductAt(row);

                if (highlightedProducts.contains(productAtRow)) {
                    c.setBackground(new Color(198, 239, 206)); //light green
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }

                return c;
            }

        };

        productTable.setDefaultRenderer(Object.class, renderer);
        productTable.setDefaultRenderer(String.class, renderer);
        productTable.setDefaultRenderer(Double.class, renderer);
    }

    public static void main(String[] args) {
        new RandProductMaker();
    }
}
