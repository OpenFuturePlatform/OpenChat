package io.openfuture.openmessanger.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {

    String message;
    Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data{
        String email;
        String firstName;
        String lastName;
    }

}


