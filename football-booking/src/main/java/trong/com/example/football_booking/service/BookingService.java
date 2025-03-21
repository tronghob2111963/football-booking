package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.reponse.ResponseData;
import trong.com.example.football_booking.dto.request.BookingRequestDTO;

public interface BookingService {
    PageResponse<?> getAllBookings(int pageNo, int pageSize, String sortBy);

    Long createBooking(BookingRequestDTO request, Long userId) throws IllegalArgumentException;
}
