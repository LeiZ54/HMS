package org.lei.hotel_management_system.service;

import jakarta.persistence.criteria.Predicate;
import org.lei.hotel_management_system.entity.Order;
import org.lei.hotel_management_system.enums.Status;
import org.lei.hotel_management_system.repository.OrderRepository;
import org.lei.hotel_management_system.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

/**
 * Service implementation for managing room and order data interactions.
 * This class provides functionalities to determine room availability based on booked orders.
 */
@Service
public class RoomAndOrderServiceImpl implements RoomAndOrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RoomRepository roomRepository;

    /**
     * Retrieves a list of room numbers that are unavailable within the given date range due to existing bookings.
     * This method filters orders that have a status of either CREATED or CHECKED and checks if the booking dates
     * overlap with the specified date range.
     *
     * @param checkInDate  The start date of the date range to check for room availability (inclusive).
     * @param checkOutDate The end date of the date range to check for room availability (inclusive).
     * @return A list of unique room numbers that are unavailable for the specified date range.
     * If the provided date parameters are null or empty, it returns an empty list.
     */
    @Override
    public List<String> findUnavailableRoomNumbersByDates(String checkInDate, String checkOutDate) {
        if (checkInDate == null || checkInDate.isEmpty() || checkOutDate == null || checkOutDate.isEmpty()) {
            return Collections.emptyList();
        }

        Specification<Order> spec = (root, query, cb) -> {
            Predicate statusPredicate = root.get("status").in(Status.CREATED, Status.CHECKED);
            Predicate overlapPredicate = cb.or(
                    cb.between(root.get("checkInDate"), Date.valueOf(checkInDate), Date.valueOf(checkOutDate)),
                    cb.between(root.get("checkOutDate"), Date.valueOf(checkInDate), Date.valueOf(checkOutDate)),
                    cb.and(
                            cb.lessThan(root.get("checkInDate"), Date.valueOf(checkInDate)),
                            cb.greaterThan(root.get("checkOutDate"), Date.valueOf(checkOutDate))
                    )
            );
            return cb.and(statusPredicate, overlapPredicate);
        };

        return orderRepository.findAll(spec).stream().map(Order::getRoomNumber).distinct().toList();
    }
}

