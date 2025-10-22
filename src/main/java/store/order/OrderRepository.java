package store.order;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, String> {

    // Adicione um método para buscar os pedidos de um usuário específico
    List<Order> findByUserId(String userId);
}