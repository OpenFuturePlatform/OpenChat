package io.openfuture.openmessanger.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageResponse {
    private String uniqueId;
    private String recipientName;
    private String recipientAvatarUrl;
    private String lastMessage;
    private LocalDateTime lastAt;
}
