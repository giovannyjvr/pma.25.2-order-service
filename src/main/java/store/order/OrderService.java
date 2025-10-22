package store.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import store.product.ProductController;
import store.product.ProductOut;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductController productController;

    public OrderOut createOrder(String userId, OrderIn orderIn) {
        // 1. Validar os itens do pedido
        if (orderIn == null || orderIn.items() == null || orderIn.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order items cannot be empty");
        }

        // 2. Buscar os produtos e calcular o total do pedido
        List<OrderItem> orderItems = new ArrayList<>();
        double orderTotal = 0.0;

        for (OrderItemIn itemIn : orderIn.items()) {
            // Buscar o produto usando o cliente Feign
            ProductOut product = productController.findById(itemIn.productId()).getBody();

            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + itemIn.productId());
            }

            // Calcular o total do item
            double itemTotal = product.price() * itemIn.quantity();
            orderTotal += itemTotal;

            // Criar o OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.productId(itemIn.productId());
            orderItem.quantity(itemIn.quantity());
            orderItem.total(itemTotal);
            orderItems.add(orderItem);
        }

        // 3. Criar o pedido
        Order order = new Order();
        order.userId(userId);
        order.date(LocalDateTime.now());
        order.total(orderTotal);
        order.items(orderItems);

        // 4. Salvar o pedido no banco de dados
        order = orderRepository.save(order);

        // 5. Associar os OrderItems ao Order
        for (OrderItem orderItem : orderItems) {
            orderItem.order(order);
        }

        // 6. Salvar os OrderItems
        order = orderRepository.save(order);

        // 7. Converter para OrderOut e retornar
        return toOrderOut(order);
    }

    public List<OrderOut> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::toOrderOut)
                .collect(Collectors.<OrderOut>toList());
    }

    public OrderOut getOrderByIdAndUserId(String id, String userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.userId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this user");
        }

        return toOrderOut(order);
    }

    private OrderOut toOrderOut(Order order) {
        List<OrderItemOut> orderItemOuts = order.items().stream()
                .map(item -> OrderItemOut.builder()
                        .id(item.id())
                        .product(productController.findById(item.productId()).getBody())
                        .quantity(item.quantity())
                        .total(item.total())
                        .build())
                .collect(Collectors.toList());

        return OrderOut.builder()
                .id(order.id())
                .date(order.date())
                .items(orderItemOuts)
                .total(order.total())
                .build();
    }
}