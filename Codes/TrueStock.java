import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// PRODUCT CLASS
class Product {
    private String id, category, name;
    private double price;
    private int quantity;

    // Constructor to initialize product attributes
    public Product(String id, String category, String name, double price, int quantity) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    // Setter
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return id + " | " + category + " | " + name + " | " + price + " | " + quantity;
    }
}

// CUSTOM EXCEPTIONS
// Thrown when product ID not found
class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String msg) { super(msg); }
}

// Thrown when quantity entered is invalid or missing
class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String msg) { super(msg); }
}

// Thrown when trying to reserve more stock than available
class InsufficientStockException extends Exception {
    public InsufficientStockException(String msg) { super(msg); }
}

// MAIN APPLICATION CLASS
public class TrueStock extends JFrame {

    private JTextField txtSearchName, txtSearchCategory, txtReserveId, txtReserveQty;
    private JTextField txtRestockId, txtRestockQty, txtRemoveId, txtAddId, txtAddCategory, txtAddName, txtAddPrice, txtAddQty;
    private JButton btnSearch, btnReserve, btnRestock, btnAdd, btnRemove;
    private JTable table;
    private DefaultTableModel model;

    private List<Product> productList;

    public TrueStock() {
        productList = new ArrayList<>();
        loadSampleProducts();

        // Frame setup
        setTitle("TrueStock Inventory System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title label
        JLabel title = new JLabel("TrueStock Inventory System", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(34, 132, 158));
        title.setBackground(new Color(253, 247, 207));
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Category", "Name", "Price", "Quantity"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        refreshTable(productList);
        setVisible(true);
    }

    // UI CREATION

    // CONTROL PANEL
    private JPanel createControlPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        mainPanel.setBackground(new Color(212, 212, 212));

        mainPanel.add(createSearchPanel());
        mainPanel.add(createRestockPanel());
        mainPanel.add(createAddPanel());

        return mainPanel;
    }

    // SEARCH + RESERVE PANEL
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBackground(new Color(212, 212, 212));

        // Left: Search Section
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(218, 237, 223));
        searchPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        txtSearchCategory = new JTextField(8);
        txtSearchName = new JTextField(8);
        btnSearch = new JButton("Search");

        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(txtSearchCategory);
        searchPanel.add(new JLabel("Name:"));
        searchPanel.add(txtSearchName);
        searchPanel.add(btnSearch);

        // Right: Reserve Section
        JPanel reservePanel = new JPanel();
        reservePanel.setBackground(new Color(237, 230, 218));
        reservePanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        txtReserveId = new JTextField(5);
        txtReserveQty = new JTextField(3);
        btnReserve = new JButton("Reserve");

        reservePanel.add(new JLabel("Reserve ID:"));
        reservePanel.add(txtReserveId);
        reservePanel.add(new JLabel("Quantity:"));
        reservePanel.add(txtReserveQty);
        reservePanel.add(btnReserve);

        panel.add(searchPanel);
        panel.add(reservePanel);

        btnSearch.addActionListener(e -> searchProducts());
        btnReserve.addActionListener(e -> handleReserve());

        return panel;
    }

    // RESTOCK + REMOVE PANEL
    private JPanel createRestockPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBackground(new Color(212, 212, 212));

        // Left: Restock Section
        JPanel restockPanel = new JPanel();
        restockPanel.setBackground(new Color(224, 218, 237));
        restockPanel.setBorder(new EmptyBorder(25, 10, 10, 10));
        txtRestockId = new JTextField(5);
        txtRestockQty = new JTextField(5);
        btnRestock = new JButton("Restock");

        restockPanel.add(new JLabel("Restock ID:"));
        restockPanel.add(txtRestockId);
        restockPanel.add(new JLabel("Quantity:"));
        restockPanel.add(txtRestockQty);
        restockPanel.add(btnRestock);

        // Right: Remove Section
        JPanel removePanel = new JPanel();
        removePanel.setBackground(new Color(208, 223, 227));
        removePanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        txtRemoveId = new JTextField(5);
        btnRemove = new JButton("Remove Item");

        removePanel.add(new JLabel("Remove ID:"));
        removePanel.add(txtRemoveId);
        removePanel.add(btnRemove);

        panel.add(restockPanel);
        panel.add(removePanel);

        btnRestock.addActionListener(e -> handleRestock());
        btnRemove.addActionListener(e -> handleRemoveProduct());

        return panel;
    }

    // ADD PRODUCT PANEL
    private JPanel createAddPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setBackground(new Color(237, 218, 227));
        addPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        txtAddId = new JTextField(4);
        txtAddCategory = new JTextField(5);
        txtAddName = new JTextField(7);
        txtAddPrice = new JTextField(5);
        txtAddQty = new JTextField(5);
        btnAdd = new JButton("Add Product");

        addPanel.add(new JLabel("ID:"));
        addPanel.add(txtAddId);
        addPanel.add(new JLabel("Category:"));
        addPanel.add(txtAddCategory);
        addPanel.add(new JLabel("Name:"));
        addPanel.add(txtAddName);
        addPanel.add(new JLabel("Price:"));
        addPanel.add(txtAddPrice);
        addPanel.add(new JLabel("Qty:"));
        addPanel.add(txtAddQty);
        addPanel.add(btnAdd);

        btnAdd.addActionListener(e -> handleAddProduct());
        return addPanel;
    }

    // ACTION HANDLERS
    private void searchProducts() {
        String name = txtSearchName.getText().trim().toLowerCase();
        String category = txtSearchCategory.getText().trim().toLowerCase();

        if (name.isEmpty() && category.isEmpty()) {
            refreshTable(productList);
            return;
        }

        List<Product> found = new ArrayList<>();
        for (Product p : productList) {
            boolean matchesName = name.isEmpty() || p.getName().toLowerCase().contains(name);
            boolean matchesCategory = category.isEmpty() || p.getCategory().toLowerCase().contains(category);
            if (matchesName && matchesCategory) found.add(p);
        }

        if (found.isEmpty())
            JOptionPane.showMessageDialog(this, "Product not found!");
        else
            refreshTable(found);
    }

    private void handleReserve() {
        try {
            reserveProduct(txtReserveId.getText(), txtReserveQty.getText());
            refreshTable(productList);
            JOptionPane.showMessageDialog(this, "Reserved successfully!");
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void handleRemoveProduct() {
        try {
            removeProduct(txtRemoveId.getText());
            refreshTable(productList);
            JOptionPane.showMessageDialog(this, "Product removed successfully!");
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void handleRestock() {
        try {
            restockProduct(txtRestockId.getText(), txtRestockQty.getText());
            refreshTable(productList);
            JOptionPane.showMessageDialog(this, "Restocked successfully!");
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void handleAddProduct() {
        try {
            addProduct(txtAddId.getText(), txtAddCategory.getText(), txtAddName.getText(),
                    txtAddPrice.getText(), txtAddQty.getText());
            refreshTable(productList);
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // CORE LOGIC
    private void reserveProduct(String id, String qtyText)
            throws ProductNotFoundException, InvalidQuantityException, InsufficientStockException {
        if (id.isEmpty() || qtyText.isEmpty())
            throw new InvalidQuantityException("Please fill ID and quantity!");

        int qty;
        try { qty = Integer.parseInt(qtyText); }
        catch (NumberFormatException e) { throw new InvalidQuantityException("Please enter a valid quantity!"); }

        if (qty <= 0)
            throw new InvalidQuantityException("Quantity must be greater than 0!");

        for (Product p : productList) {
            if (p.getId().equalsIgnoreCase(id)) {
                if (p.getQuantity() < qty)
                    throw new InsufficientStockException("Not enough stock for product ID: " + id);
                p.setQuantity(p.getQuantity() - qty);
                return;
            }
        }
        throw new ProductNotFoundException("Product with ID " + id + " not found");
    }

    private void restockProduct(String id, String qtyText)
            throws ProductNotFoundException, InvalidQuantityException {
        if (id.isEmpty() || qtyText.isEmpty())
            throw new InvalidQuantityException("Please fill ID and quantity");

        int qty;
        try { qty = Integer.parseInt(qtyText); }
        catch (NumberFormatException e) { throw new InvalidQuantityException("Please enter a valid quantity!"); }

        if (qty <= 0)
            throw new InvalidQuantityException("Quantity must be greater than 0!");

        for (Product p : productList) {
            if (p.getId().equalsIgnoreCase(id)) {
                p.setQuantity(p.getQuantity() + qty);
                return;
            }
        }
        throw new ProductNotFoundException("Product with ID " + id + " not found");
    }

    private void removeProduct(String id) throws ProductNotFoundException {
        if (id.isEmpty()) throw new ProductNotFoundException("Please enter a product ID to remove!");

        boolean found = false;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equalsIgnoreCase(id)) {
                productList.remove(i);
                found = true;
                break;
            }
        }
        if (!found) throw new ProductNotFoundException("Product with ID " + id + " not found!");
    }

    private void addProduct(String productId, String productCategory, String name, String priceText, String qtyText)
            throws InvalidQuantityException {
        if (productId.isEmpty() || productCategory.isEmpty() || name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty())
            throw new InvalidQuantityException("All fields are required!");

        String id = productId.toUpperCase();
        String category = productCategory.substring(0, 1).toUpperCase() + productCategory.substring(1).toLowerCase();

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceText);
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            throw new InvalidQuantityException("Please enter a valid quantity and price!");
        }

        if (price <= 0 || qty <= 0)
            throw new InvalidQuantityException("Price and Quantity must be greater than 0!");

        for (Product p : productList) {
            if (p.getId().equalsIgnoreCase(id))
                throw new InvalidQuantityException("Product ID already exists!");
        }

        productList.add(new Product(id, category, name, price, qty));
    }

    // TABLE UPDATE
    private void refreshTable(List<Product> products) {
        model.setRowCount(0); // Clear table
        for (Product product : products) {
            String formattedPrice = "Rs. " + product.getPrice();
            model.addRow(new Object[]{product.getId(), product.getCategory(), product.getName(), formattedPrice, product.getQuantity()});
        }
    }

    // SAMPLE DATA
    private void loadSampleProducts() {
        productList.add(new Product("L001", "Laptop", "Apple MacBook Pro M4", 479900.00, 5));
        productList.add(new Product("L002", "Laptop", "Lenovo Yoga Slim 7", 579000.00, 10));
        productList.add(new Product("C001", "Console", "Sony PlayStation 5 (Slim)", 185000.00, 15));
        productList.add(new Product("M001", "Monitor", "LG ULTRAGEAR 27' 4K IPS", 349000.00, 10));
        productList.add(new Product("K001", "Keyboard", "Logitech G512 CARBON", 34500.00, 10));
    }

    // CLEAR FIELDS
    private void clearFields() {
        txtSearchName.setText("");
        txtSearchCategory.setText("");
        txtReserveId.setText("");
        txtReserveQty.setText("");
        txtRestockId.setText("");
        txtRestockQty.setText("");
        txtRemoveId.setText("");
        txtAddId.setText("");
        txtAddCategory.setText("");
        txtAddName.setText("");
        txtAddPrice.setText("");
        txtAddQty.setText("");
    }

    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrueStock::new);
    }
}