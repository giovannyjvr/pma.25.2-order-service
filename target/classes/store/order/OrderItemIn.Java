package store.order;

import lombok.Builder;

@Builder
public record OrderItemIn(
    String productId,
    Integer quantity
) {}