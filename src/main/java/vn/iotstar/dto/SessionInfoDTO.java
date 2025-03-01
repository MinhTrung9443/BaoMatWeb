package vn.iotstar.dto;

import java.time.LocalDate;
import java.util.UUID; // Để tạo sessionId ngẫu nhiên

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionInfoDTO {
    private String sessionId;
    private String fullname;

    // Constructor với 1 tham số String để tạo giá trị ngẫu nhiên cho sessionId và userName
    public SessionInfoDTO(String fullname) {
        // Tạo sessionId ngẫu nhiên với UUID
        this.sessionId = UUID.randomUUID().toString();
        
        // Tạo userName ngẫu nhiên với giá trị prefix
        this.fullname = fullname;
    }
}
