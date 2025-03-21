package trong.com.example.football_booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import trong.com.example.football_booking.common.BookingStatus;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.request.BookingRequestDTO;
import trong.com.example.football_booking.entity.Booking;
import trong.com.example.football_booking.entity.Field;
import trong.com.example.football_booking.entity.User;
import trong.com.example.football_booking.repository.BookingRepository;
import trong.com.example.football_booking.repository.FieldRepository;
import trong.com.example.football_booking.repository.UserRepository;
import trong.com.example.football_booking.service.BookingService;
import trong.com.example.football_booking.validator.BookingValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final BookingValidator bookingValidator;

    @Override
    public PageResponse<?> getAllBookings(int pageNo, int pageSize, String sortBy) {
        log.info("Fetching bookings with pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);

        int p = Math.max(pageNo - 1, 0); // Đảm bảo page không âm
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(p, pageSize, Sort.by(sorts));
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        List<Booking> bookingList = bookings.stream()
                .map(booking -> Booking.builder()
                        .id(booking.getId())
                        .field_id(booking.getField_id())
                        .user_id(booking.getUser_id())
                        .start_time(booking.getStart_time())
                        .end_time(booking.getEnd_time())
                        .status(BookingStatus.valueOf(booking.getStatus().name()))
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(bookings.getTotalElements())
                .totalPages(bookings.getTotalPages())
                .items(bookingList)
                .build();
    }

    @Override
    public Long createBooking(BookingRequestDTO request, Long userId) throws IllegalArgumentException {
        log.info("Creating booking for userId: {}, fieldId: {}, request: {}", userId, request.getField_id(), request);

        // Kiểm tra userId
        if (userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Kiểm tra fieldId
        Long fieldId = request.getField_id();
        if (fieldId == null) {
            log.error("Field ID is null in request: {}", request);
            throw new IllegalArgumentException("Field ID cannot be null");
        }

        // Validate request
        bookingValidator.validateBookingRequest(request);

        // Tìm user và field
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + fieldId));

        // Lấy thời gian từ validator
        LocalDateTime startTime = bookingValidator.getStartTime(request);
        LocalDateTime endTime = bookingValidator.getEndTime(request);

        // Kiểm tra xung đột lịch
        if (isFieldBooked(fieldId, startTime, endTime)) {
            throw new IllegalArgumentException("Field is already booked for the selected time slot");
        }

        // Tạo booking
        Booking booking = Booking.builder()
                .field_id(field)
                .user_id(user)
                .start_time(startTime)
                .end_time(endTime)
                .status(BookingStatus.PENDING)
                .build();

        // Lưu vào database
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return savedBooking.getId();
    }
    private Boolean isFieldBooked(Long field_id, LocalDateTime start_time, LocalDateTime end_time) {
        log.info("Checking if field {} is booked between {} and {}", field_id, start_time, end_time);
        boolean isBooked = bookingRepository.isFieldBooked(field_id, start_time, end_time);
        log.info("Field booked: {}", isBooked);
        return isBooked;
    }


}