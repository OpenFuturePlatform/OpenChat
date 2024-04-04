package io.openfuture.openmessanger.web.response;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class LastMessage {
    String chatUid;
    boolean group;
    String chatRoomName;
    Integer memberCount;
    String displayUserName;
    String lastMessageText;
    LocalDateTime lastMessageTime;
    String chatRoomPicture;
}
