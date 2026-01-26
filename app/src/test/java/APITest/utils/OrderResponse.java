package APITest.utils;

public class OrderResponse {
    
    private String orderId;
    private boolean created;

    public OrderResponse() { }

    public String getOrderId() {
        return orderId;
    }

    public boolean isCreated() {
        return created;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "OrderResponse [orderId=" + orderId + ", created=" + created + "]";
    }
    
}
