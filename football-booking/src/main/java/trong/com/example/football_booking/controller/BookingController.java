package trong.com.example.football_booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import trong.com.example.football_booking.dto.reponse.BookingResponse;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.reponse.ResponseData;
import trong.com.example.football_booking.dto.request.BookingRequestDTO;
import trong.com.example.football_booking.repository.BookingRepository;
import trong.com.example.football_booking.service.BookingService;

@Slf4j
@RestController
@RequestMapping("api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @PostMapping("/create-booking")
    public ResponseData<?> createBooking(@RequestBody BookingRequestDTO request) {
        log.info("Creating new booking with request: {}", request);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("User is not authenticated");
                return new ResponseData<>(HttpStatus.UNAUTHORIZED.value(), "User is not authenticated", null);
            }
            Long userId = (Long) authentication.getCredentials();
            log.info("Authenticated userId: {}", userId);
            if (userId == null) {
                log.warn("User ID not found in token");
                return new ResponseData<>(HttpStatus.UNAUTHORIZED.value(), "User ID not found in token", null);
            }

            BookingResponse bookingId = bookingService.createBooking(request, userId);
            log.info("Booking created with ID: {}", bookingId);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Booking created successfully", bookingId);
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating booking: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/get-all-booking")
    public ResponseData<?> getAllBooking() {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get all booking successfully", bookingRepository.findAll());
        } catch (Exception e) {
            log.error("Error getting all booking", e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error getting all booking: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/cancel-booking/{id}")
    public ResponseData<Void> cancelBooking(
            @PathVariable Long id,
            @RequestParam Long userId) {
        log.info("Canceling booking with id: {}", id);
        try {
            bookingService.cancelBooking(id, userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Booking canceled successfully", null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error canceling booking: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error canceling booking", null);
        }
    }
    @GetMapping("/my-bookings")
    public ResponseData<?> getMyBookings(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id:asc") String sortBy) {
        log.info("Fetching bookings for userId: {} with pageNo: {}, pageSize: {}, sortBy: {}", userId, pageNo, pageSize, sortBy);
        try {
            PageResponse<?> response = bookingService.getMyBookings(userId, pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "User bookings retrieved successfully", response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error fetching user bookings: {}", e.getMessage(), e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching user bookings: " + e.getMessage(), null);
        }

    }
    @PutMapping("/update-booking/{id}")
    public ResponseData<BookingResponse> updateBooking(@PathVariable Long id, @RequestBody BookingRequestDTO request, @RequestParam Long userId) {
        log.info("Updating booking with id: {}, userId: {}", id, userId);
        try {
            BookingResponse response = bookingService.updateBooking(id, request, userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Booking updated successfully", response);
        } catch (Exception e) {
            log.error("Error updating booking: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating booking", null);
        }
    }
}
