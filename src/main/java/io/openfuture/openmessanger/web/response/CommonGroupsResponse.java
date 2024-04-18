package io.openfuture.openmessanger.web.response;

import java.util.List;

import lombok.Value;

@Value
public class CommonGroupsResponse {
    String email;
    String fullName;
    List<GroupInfo> groups;

    @Value
    public static class GroupInfo {
        Integer id;
        String name;
    }

}
