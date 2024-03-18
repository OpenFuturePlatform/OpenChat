package io.openfuture.openmessanger.web.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticatedResponse implements Serializable {

    private String username;

    private String accessToken;

    private String idToken;

    private String refreshToken;

}
