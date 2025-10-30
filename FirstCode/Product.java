import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Product class
class Product {
    private String id;
    private String name;
    private int quantity;
    private int reorderThreshold;

    public Product(String id, String name, int quantity, int reorderThreshold) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

// Observer interface for stock alerts
interface StockObserver {
    void onLowStock(Product product);
}

// Warehouse class managing inventory
class Warehouse {
    private Map<String, Product> products;
    private List<StockObserver> observers;

    public Warehouse() {
        this.products = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public void addObserver(StockObserver observer) {
        this.observers.add(observer);
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public void receiveShipment(String productId, int quantity) {
        Product product = products.get(productId);
        if (product != null) {
            product.setQuantity(product.getQuantity() + quantity);
            System.out.println("Received shipment of " + quantity + " units for " + product.getName());
        } else {
            System.out.println("Product not found with ID: " + productId);
        }
    }

    public void fulfillOrder(String productId, int quantity) {
        Product product = products.get(productId);
        if (product != null) {
            if (product.getQuantity() >= quantity) {
                product.setQuantity(product.getQuantity() - quantity);
                System.out.println("Fulfilled order of " + quantity + " units for " + product.getName());
                checkLowStock(product);
            } else {
                System.out.println("Insufficient stock for " + product.getName());
            }
        } else {
            System.out.println("Product not found with ID: " + productId);
        }
    }

    private void checkLowStock(Product product) {
        if (product.getQuantity() < product.getReorderThreshold()) {
            notifyObservers(product);
        }
    }

    private void notifyObservers(Product product) {
        for (StockObserver observer : observers) {
            observer.onLowStock(product);
        }
    }
}

// AlertService implementing StockObserver
class AlertService implements StockObserver {
    @Override
    public void onLowStock(Product product) {
        System.out.println("Low stock for " + product.getName() + " – only " + product.getQuantity() + " left!");
    }
}

public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        AlertService alertService = new AlertService();
        warehouse.addObserver(alertService);

        Product laptop = new Product("P001", "Laptop", 0, 5);
        warehouse.addProduct(laptop);

        warehouse.receiveShipment("P001", 10); // Total = 10
        warehouse.fulfillOrder("P001", 6); // Remaining = 4, Triggers alert
    }
}
