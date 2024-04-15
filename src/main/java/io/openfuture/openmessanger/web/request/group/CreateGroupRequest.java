package io.openfuture.openmessanger.web.request.group;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    String name;
    String creator;
    List<String> participants = new ArrayList<>();
}
