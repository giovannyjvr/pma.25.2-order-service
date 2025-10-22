package store.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders") // Adicione o mapeamento base para o controlador
public class OrderResource {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderOut> createOrder(@RequestBody OrderIn orderIn) {
        // TODO: Implementar a autenticação para obter o ID do usuário atual
        String userId = "usuario_autenticado"; // Substitua pelo ID do usuário autenticado

        try {
            OrderOut orderOut = orderService.createOrder(userId, orderIn);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderOut);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create order", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderOut>> getOrders() {
        // TODO: Implementar a autenticação para obter o ID do usuário atual
        String userId = "usuario_autenticado"; // Substitua pelo ID do usuário autenticado

        try {
            List<OrderOut> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get orders", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderOut> getOrder(@PathVariable String id) {
        // TODO: Implementar a autenticação para obter o ID do usuário atual
        String userId = "usuario_autenticado"; // Substitua pelo ID do usuário autenticado

        try {
            OrderOut order = orderService.getOrderByIdAndUserId(id, userId);
            return ResponseEntity.ok(order);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get order", e);
        }
    }
}