package org.lei.hotel_management_system.service;

import jakarta.persistence.criteria.Predicate;
import org.lei.hotel_management_system.DTO.RoomDetailsDTO;
import org.lei.hotel_management_system.DTO.RoomTypeListDTO;
import org.lei.hotel_management_system.entity.Room;
import org.lei.hotel_management_system.entity.RoomTypeInfo;
import org.lei.hotel_management_system.enums.Type;
import org.lei.hotel_management_system.repository.RoomRepository;
import org.lei.hotel_management_system.repository.RoomTypeInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing room-related operations in the hotel management system.
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeInfoRepository roomTypeRepository;

    @Autowired
    private RoomTypeInfoRepository roomTypeInfoRepository;

    @Autowired
    private RoomAndOrderService roomAndOrderService;

    /**
     * Adds a room to the repository.
     *
     * @param room The room to add.
     */
    @Override
    public void addRoom(Room room) {
        roomRepository.save(room);
    }

    /**
     * Adds room type information to the repository.
     *
     * @param info The room type info to add.
     */
    @Override
    public void addRoomTypeInfo(RoomTypeInfo info) {
        roomTypeRepository.save(info);
    }

    /**
     * Retrieves detailed information about a room by its room number.
     *
     * @param roomNumber The room number to search for.
     * @return A DTO containing detailed room information.
     * @throws RuntimeException If the room is not found.
     */
    @Override
    public RoomDetailsDTO getRoomDetailsByRoomNumber(String roomNumber) {
        Room room = roomRepository.getByRoomNumber(roomNumber);
        if (room == null) throw new RuntimeException("Room not found!");
        return convertRoomToRoomDetailsDTO(room);
    }

    /**
     * Retrieves a room entity by its room number.
     *
     * @param roomNumber The room number to search for.
     * @return The room entity.
     */
    @Override
    public Room getByRoomNumber(String roomNumber) {
        return roomRepository.getByRoomNumber(roomNumber);
    }

    /**
     * Sets the availability of a room.
     *
     * @param roomNumber The room number to update.
     * @param available  The new availability status.
     */
    @Override
    public void setAvailable(String roomNumber, boolean available) {
        Room room = roomRepository.getByRoomNumber(roomNumber);
        room.setAvailable(available);
        roomRepository.save(room);
    }

    /**
     * Lists rooms based on provided criteria, filtering by room number, type, availability,
     * and optionally checks availability between given dates.
     *
     * @param roomNumber   The room number to filter by.
     * @param type         The type of room to filter by.
     * @param available    Filter rooms by availability.
     * @param checkInDate  Check-in date for availability filtering.
     * @param checkOutDate Check-out date for availability filtering.
     * @param page         The page number for pagination.
     * @return A list of DTOs representing the filtered rooms.
     */
    @Override
    public List<RoomDetailsDTO> list(String roomNumber, Type type, Boolean available, String checkInDate, String checkOutDate, Integer page) {
        return roomRepository.findAll((Specification<Room>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (checkInDate != null && !checkInDate.isEmpty() && checkOutDate != null && !checkOutDate.isEmpty()) {
                if (Date.valueOf(checkInDate).before(Date.valueOf(LocalDate.now())))
                    throw new RuntimeException("Your check in date is before today!");
                if (Date.valueOf(checkOutDate).before(Date.valueOf(checkInDate)))
                    throw new RuntimeException("Your check in date is after your check out date!");
                System.out.println(roomAndOrderService.findUnavailableRoomNumbersByDates(checkInDate, checkOutDate));
                predicates.add(cb.not(root.get("roomNumber").in(roomAndOrderService.findUnavailableRoomNumbersByDates(checkInDate, checkOutDate))));
            }

            if (roomNumber != null && !roomNumber.isEmpty()) {
                predicates.add(cb.equal(root.get("roomNumber"), roomNumber));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (available != null) {
                predicates.add(cb.equal(root.get("available"), available));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(page, 10)).stream().map(this::convertRoomToRoomDetailsDTO).toList();
    }

    /**
     * Retrieves a list of all room types.
     *
     * @return A list of DTOs for room types.
     */
    @Override
    public List<RoomTypeListDTO> typeList() {
        return roomTypeInfoRepository.findAll().stream().map(this::convertRoomTypeToRoomTypeListDTO).toList();
    }

    /**
     * Converts a Room entity to a RoomDetailsDTO object.
     * This method extracts details from a Room object and uses related repository to fetch additional details like room type specific price and images.
     *
     * @param room the Room entity to convert.
     * @return RoomDetailsDTO containing the detailed information of the room including room number, price, availability status, type, and images.
     */
    private RoomDetailsDTO convertRoomToRoomDetailsDTO(Room room) {
        RoomDetailsDTO roomDetailsDTO = new RoomDetailsDTO();
        roomDetailsDTO.setRoomNumber(room.getRoomNumber());
        roomDetailsDTO.setPrice(roomTypeRepository.findByType(room.getType()).getPrice());
        roomDetailsDTO.setImages(roomTypeRepository.findByType(room.getType()).getImages());
        roomDetailsDTO.setAvailable(room.getAvailable());
        roomDetailsDTO.setType(room.getType());
        return roomDetailsDTO;
    }

    /**
     * Converts a RoomTypeInfo entity to a RoomTypeListDTO.
     * This method is used to transform RoomTypeInfo data into a more simplified data transfer object format that includes essential information such as type, price, and images.
     *
     * @param roomTypeInfo the RoomTypeInfo entity to convert.
     * @return RoomTypeListDTO with simplified data suitable for listing purposes including the type, price, and images of the room type.
     */
    private RoomTypeListDTO convertRoomTypeToRoomTypeListDTO(RoomTypeInfo roomTypeInfo) {
        RoomTypeListDTO roomTypeListDTO = new RoomTypeListDTO();
        roomTypeListDTO.setType(roomTypeInfo.getType());
        roomTypeListDTO.setPrice(roomTypeInfo.getPrice());
        roomTypeListDTO.setImages(roomTypeInfo.getImages());
        return roomTypeListDTO;
    }

}
