package io.openfuture.openmessanger.web.request;

import io.openfuture.openmessanger.repository.entity.MessageContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageRequest {
    private String sender;
    private Integer groupId;
    private MessageContentType contentType;
    private String body;
}
