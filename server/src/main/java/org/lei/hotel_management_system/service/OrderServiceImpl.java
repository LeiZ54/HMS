package org.lei.hotel_management_system.service;

import jakarta.persistence.criteria.*;
import org.lei.hotel_management_system.DTO.*;
import org.lei.hotel_management_system.entity.Order;
import org.lei.hotel_management_system.entity.User;
import org.lei.hotel_management_system.enums.Role;
import org.lei.hotel_management_system.enums.Status;
import org.lei.hotel_management_system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service implementation for managing orders in the hotel management system.
 * This service handles all operations related to creating, updating, and querying orders,
 * including status changes and detailed order information retrieval.
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;

    /**
     * Creates and saves a new order to the repository after validating room availability and existence.
     *
     * @param order The order object to be added.
     * @return A {@link Order} The saved order object.
     * @throws RuntimeException if the room does not exist or is not available.
     */
    @Override
    public Order addOrder(Order order) {
        if (roomService.getByRoomNumber(order.getRoomNumber()) == null)
            throw new RuntimeException("Room number does not exist!");
        if (!checkOrderAvailable(order.getRoomNumber(), order.getCheckInDate(), order.getCheckOutDate()))
            throw new RuntimeException("This room is not available for this time!");
        order.setUsername(userService.getCurrentUser().getUsername());
        return orderRepository.save(order);
    }

    /**
     * Updates an existing order with new customer details provided via a DTO.
     *
     * @param updateOrder Data transfer object containing updated order details.
     */
    @Override
    public void updateOrder(OrderUpdateDTO updateOrder) {
        Order order = orderRepository.findByOrderNumber(updateOrder.getOrderNumber());
        if (order == null) throw new RuntimeException("Order number does not exist");
        if (updateOrder.getCustomerName() != null && !updateOrder.getCustomerName().isEmpty())
            order.setCustomerName(updateOrder.getCustomerName());
        if (updateOrder.getCustomerEmail() != null && !updateOrder.getCustomerEmail().isEmpty())
            order.setCustomerEmail(updateOrder.getCustomerEmail());
        orderRepository.save(order);
    }

    /**
     * Changes the status of an existing order, performing validations based on the role of the current user.
     *
     * @param orderNumber The identifier of the order to change the status of.
     * @param status      The new status to set.
     * @throws RuntimeException if the order does not exist or if the status change is not permissible.
     */
    @Override
    public void changeStatus(String orderNumber, Status status) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.CUSTOMER && (status == Status.CHECKED || status == Status.FINISHED))
            throw new RuntimeException("Customer is not allowed to change the status of this order!");

        Order order = orderRepository.findByOrderNumber(orderNumber);
        if (order == null)
            throw new RuntimeException("Order does not exist!");

        if (status == Status.CHECKED) {
            // Order can only be checked when current status equals to CREATED
            if (order.getStatus() != Status.CREATED)
                throw new RuntimeException("This order is not available to be checked in!");
            roomService.setAvailable(order.getRoomNumber(), false);
        }

        if (status == Status.FINISHED) {
            // Order can only be checked when current status equals to CHECKED
            if (order.getStatus() != Status.CHECKED)
                throw new RuntimeException("This order is not available to be checked out!");
            roomService.setAvailable(order.getRoomNumber(), true);
        }

        if (status == Status.CANCELED) {
            // Order can only be canceled when the current status is CREATED
            if ((order.getStatus() != Status.CREATED))
                throw new RuntimeException("This order is not available to be canceled!");

            // CUSTOMER user can only the orders in their account
            if (currentUser.getRole() == Role.CUSTOMER && !currentUser.getUsername().equals(order.getUsername()))
                throw new RuntimeException("You are not allowed to cancel this order!");
        }

        order.setStatus(status);
        orderRepository.save(order);
    }


    /**
     * Lists orders based on filtering criteria and pagination settings.
     *
     * @param orderNumber   Filter for specific order number.
     * @param roomNumber    Filter for specific room number.
     * @param username      Filter for specific username.
     * @param customerName  Filter for specific customer name.
     * @param customerEmail Filter for specific customer email.
     * @param status        Filter for specific order status.
     * @param checkInDate   Filter for orders starting from a specific date.
     * @param checkOutDate  Filter for orders ending by a specific date.
     * @param page          Page number for pagination.
     * @return A list of orders that match the specified filters and pagination settings.
     */
    @Override
    public List<OrderListDTO> list(String orderNumber, String roomNumber, String username, String customerName, String customerEmail, Status status, String checkInDate, String checkOutDate, Integer page) {
        return orderRepository.findAll((Specification<Order>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (orderNumber != null && !orderNumber.isEmpty()) {
                predicates.add(cb.equal(root.get("orderNumber"), orderNumber));
            }

            if (roomNumber != null && !roomNumber.isEmpty()) {
                predicates.add(cb.equal(root.get("roomNumber"), roomNumber));
            }

            if (userService.getCurrentUser().getRole().equals(Role.CUSTOMER)) {
                predicates.add(cb.equal(root.get("username"), userService.getCurrentUser().getUsername()));
            }

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.equal(root.get("username"), username));
            }

            if (customerName != null && !customerName.isEmpty()) {
                predicates.add(cb.like(root.get("customerName"), "%" + customerName + "%"));
            }

            if (customerEmail != null && !customerEmail.isEmpty()) {
                predicates.add(cb.equal(root.get("customerEmail"), customerEmail));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (checkInDate != null && !checkInDate.isEmpty()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkInDate"), Date.valueOf(checkInDate)));
            }

            if (checkOutDate != null && !checkOutDate.isEmpty()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkOutDate"), Date.valueOf(checkOutDate)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"))).stream().map(this::convertOrderToOrderListDTO).toList();
    }

    /**
     * Retrieves detailed information about a specific order, accessible based on user role and ownership.
     *
     * @param orderNumber Unique identifier of the order to retrieve.
     * @return Detailed information about the order.
     * @throws RuntimeException if the user does not have permission to access the order details.
     */
    @Override
    public OrderDetailsDTO getOrderDetails(String orderNumber) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findByOrderNumber(orderNumber);
        // CUSTOMER user can only access their own orders
        if (currentUser.getRole().equals(Role.CUSTOMER) && !order.getUsername().equals(currentUser.getUsername()))
            throw new RuntimeException("You are not allowed to access this order");
        return convertOrderToOrderDetailsDTO(order);
    }

    /**
     * Converts an OrderCreateDTO to an Order entity and generates a unique order number.
     *
     * @param orderCreateDTO DTO containing order creation data.
     * @return Newly created order.
     */
    @Override
    public Order convertOrderDTOToOrder(OrderCreateDTO orderCreateDTO) {
        return new Order(orderCreateDTO.getRoomNumber(), userService.getCurrentUser().getUsername(), orderCreateDTO.getCustomerName(), orderCreateDTO.getCustomerEmail(), LocalDate.parse(orderCreateDTO.getCheckInDate()), LocalDate.parse(orderCreateDTO.getCheckOutDate()), generateOrderNumber());
    }


    /**
     * Converts an Order entity to an OrderDetailsDTO, which is a data transfer object that provides
     * comprehensive details about an order.
     *
     * @param order The Order entity to be converted.
     * @return OrderDetailsDTO containing details like room number, username, customer information, dates, and status.
     */
    private OrderDetailsDTO convertOrderToOrderDetailsDTO(Order order) {
        // Formatting the date to 'yyyy-MM-dd HH:mm:ss' format before setting it in DTO
        return new OrderDetailsDTO(
                order.getRoomNumber(),
                order.getUsername(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCheckInDate(),
                order.getCheckOutDate(),
                order.getStatus(),
                order.getOrderNumber(),
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Converts an Order entity to an OrderListDTO, which is a simplified data transfer object
     * used for listing orders with less detail.
     *
     * @param order The Order entity to be converted.
     * @return OrderListDTO containing basic order details suitable for listings.
     */
    private OrderListDTO convertOrderToOrderListDTO(Order order) {
        // Formatting the creation date before setting it in the DTO
        return new OrderListDTO(
                order.getRoomNumber(),
                order.getCustomerName(),
                order.getStatus(),
                order.getOrderNumber(),
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Checks if an order is available for a specific room within a given date range,
     * considering the room's current bookings and statuses.
     *
     * @param roomNumber   The room number to check availability for.
     * @param checkInDate  The desired check-in date.
     * @param checkOutDate The desired check-out date.
     * @return true if the room is available; false otherwise.
     */
    private boolean checkOrderAvailable(String roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        // Retrieve all orders that may conflict with the desired booking period
        return orderRepository.findAll((Specification<Order>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("roomNumber"), roomNumber));
            predicates.add(cb.greaterThan(root.get("checkInDate"), checkInDate));
            predicates.add(cb.lessThan(root.get("checkOutDate"), checkOutDate));
            predicates.add(cb.in(root.get("status")).value(Status.CREATED).value(Status.CHECKED));
            return cb.and(predicates.toArray(new Predicate[0]));
        }).stream().map(this::convertOrderToOrderDetailsDTO).toList().isEmpty();  // Check if the filtered list is empty
    }

    /**
     * Generates a unique order number based on the current timestamp and a random number.
     * Ensures uniqueness by checking the existing orders in the repository.
     *
     * @return A unique order number.
     */
    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 10000);
        String orderNumber = timestamp + randomNum;
        // Loop to ensure the order number is unique
        while (orderRepository.findByOrderNumber(orderNumber) != null) {
            orderNumber = generateOrderNumber();
        }
        return orderNumber;
    }
}
