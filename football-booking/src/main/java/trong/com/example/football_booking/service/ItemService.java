package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.ItemResponseDTO;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.request.ItemRequestDTO;

public interface ItemService {
//    ItemResponseDTO createItem(ItemRequestDTO request);
    ItemResponseDTO updateItem(Long id, ItemRequestDTO request);
    void deleteItem(Long id);
    PageResponse<?> getAllItems(int pageNo, int pageSize, String sortBy);
}