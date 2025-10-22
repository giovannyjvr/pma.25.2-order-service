package store.order;

import lombok.Builder;
import store.product.ProductOut;

@Builder
public record OrderItemOut(
    String id,
    ProductOut product,
    Integer quantity,
    Double total
) {}