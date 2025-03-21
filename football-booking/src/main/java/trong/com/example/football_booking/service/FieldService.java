package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.entity.Field;

public interface FieldService {
    PageResponse<?> getAllFields(int pageNo, int pageSize, String sortBy);
    Field getFieldById(Long id);

}
