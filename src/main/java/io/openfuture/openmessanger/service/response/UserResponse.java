package io.openfuture.openmessanger.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    String id;
    String firstName;
    String lastName;
    String email;
}
