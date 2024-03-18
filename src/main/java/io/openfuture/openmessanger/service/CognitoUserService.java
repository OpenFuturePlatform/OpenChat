package io.openfuture.openmessanger.service;

import java.util.Optional;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutResult;
import com.amazonaws.services.cognitoidp.model.UserType;

import io.openfuture.openmessanger.service.dto.UserSignUpRequest;

public interface CognitoUserService {

    Optional<AdminInitiateAuthResult> initiateAuth(String username, String password);

    Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session);

    GlobalSignOutResult signOut(String accessToken);

    AdminGetUserResult getUserDetails(String email);

    ForgotPasswordResult forgotPassword(String username);

    void addUserToGroup(String username, String groupName);

    AdminSetUserPasswordResult setUserPassword(String username, String password);

    UserType signUp(UserSignUpRequest signUpDTO);

    Optional<AdminRespondToAuthChallengeResult> respondToAuthSmsChallenge(
            String username, String smsCode, String session);

    AdminListUserAuthEventsResult getUserAuthEvents(String username, int maxResult, String nextToken);

}
