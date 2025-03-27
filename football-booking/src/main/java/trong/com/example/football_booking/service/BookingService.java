package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.BookingResponse;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.request.BookingRequestDTO;

public interface BookingService {
    PageResponse<?> getAllBookings(int pageNo, int pageSize, String sortBy);

    BookingResponse createBooking(BookingRequestDTO request, Long userId) throws IllegalArgumentException;

    BookingResponse updateBooking(Long id, BookingRequestDTO request, Long userId) throws IllegalArgumentException;

    void cancelBooking(Long bookingId, Long userId) throws IllegalArgumentException;
    PageResponse<?> getMyBookings(Long userId, int pageNo, int pageSize, String sortBy);
}

