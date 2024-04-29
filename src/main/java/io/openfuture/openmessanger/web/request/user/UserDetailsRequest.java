package io.openfuture.openmessanger.web.request.user;

public record UserDetailsRequest(
        String username,
        String email
) {
}
