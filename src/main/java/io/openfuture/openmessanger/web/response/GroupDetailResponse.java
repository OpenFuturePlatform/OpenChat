package io.openfuture.openmessanger.web.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupDetailResponse {
    private Integer id;
    private String name;
    private String creator;
    private String avatar;
    private List<String> participants;
}
