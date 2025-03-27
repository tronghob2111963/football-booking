package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.FieldReponse;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.request.FieldRequestDTO;
import trong.com.example.football_booking.entity.Field;

public interface FieldService {
    PageResponse<?> getAllFields(int pageNo, int pageSize, String sortBy);
    Field getFieldById(Long id);
    FieldReponse updateField(Long id, FieldRequestDTO request);

}
