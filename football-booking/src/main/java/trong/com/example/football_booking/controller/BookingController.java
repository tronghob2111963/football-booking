package trong.com.example.football_booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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
    public ResponseData<Long> createBooking(@RequestBody BookingRequestDTO request) {
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

            Long bookingId = bookingService.createBooking(request, userId);
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
}
