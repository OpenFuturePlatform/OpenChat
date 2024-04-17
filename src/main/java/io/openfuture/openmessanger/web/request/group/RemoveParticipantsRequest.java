package io.openfuture.openmessanger.web.request.group;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveParticipantsRequest {
    Integer groupId;
    List<String> users;
}
