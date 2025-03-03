package trong.com.example.football_booking.dto.reponse;


import lombok.Builder;
import lombok.Getter;
import java.io.Serializable;
@Getter
@Builder
public class PageResponse<T> implements Serializable {
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private T items;
}
