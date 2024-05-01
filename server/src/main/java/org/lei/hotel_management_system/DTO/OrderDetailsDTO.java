package org.lei.hotel_management_system.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lei.hotel_management_system.enums.Status;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {
    private String roomNumber;

    private String username;

    private String customerName;

    private String customerEmail;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Status status;

    private String orderNumber;

    private String createdAt;
}
