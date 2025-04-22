import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Custom TableModel class for use with the Product class. Uses a product object
 * for each row, and calls the appropriate getters and setters to display and change
 * each cell value.
 */
public class ProductTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ID", "Name", "Description", "Cost"};
    private List<Product> products;

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    public void setProducts (List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }

    public void insertProductsAt(Product product, int index) {
        if (index < 0 || index > products.size()) {
            index = products.size(); // default to appending
        }
        products.add(index, product);
        fireTableRowsInserted(index, index);
    }

    public Product getProductAt(int rowIndex) {
        return products.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return products == null ? 0 : products.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = products.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getIDNum();
            case 1: return p.getName();
            case 2: return p.getDescription();
            case 3: return p.getCost();
            default: return null;
        }
    }
}
