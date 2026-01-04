package com.smartcafe.dto;

import com.smartcafe.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * StatusUpdateRequest DTO - Used for updating order status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
