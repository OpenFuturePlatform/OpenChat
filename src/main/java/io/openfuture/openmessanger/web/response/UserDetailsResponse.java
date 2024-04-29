package io.openfuture.openmessanger.web.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    String email;
    String fullName;
    List<CommonGroupsResponse.GroupInfo> groups;

    @Value
    public static class GroupInfo {
        Integer id;
        String name;
    }
}
