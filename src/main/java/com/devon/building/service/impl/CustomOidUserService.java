package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOidUserService extends OidcUserService {
    UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        if (email == null || email.isBlank() || !Boolean.TRUE.equals(oidcUser.getEmailVerified())) {
            throw new OAuth2AuthenticationException("Google email is missing or not verified");
        }
        String googleAccountId = oidcUser.getSubject();
        UserEntity user = userRepository.findByGoogleAccountId(googleAccountId).orElse(null);
        if (user == null) {
            user = userRepository.findByUserName(email);
            if (user == null) {
                user = createGoogleUser(oidcUser);
            } else {
                ensureActive(user);
                if (user.getGoogleAccountId() != null
                        && !user.getGoogleAccountId().equals(googleAccountId)) {
                    throw new OAuth2AuthenticationException("Google account is already linked to another identity");
                }
                user.setGoogleAccountId(googleAccountId);
                user = userRepository.save(user);
            }
        }
        ensureActive(user);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getUserRole());
        return new DefaultOidcUser(Collections.singleton(grantedAuthority),oidcUser.getIdToken(),oidcUser.getUserInfo(),"email");
    }

    private void ensureActive(UserEntity user) {
        if (!user.isActive()) {
            throw new OAuth2AuthenticationException("This account is inactive");
        }
    }

    private UserEntity createGoogleUser(OidcUser oidcUser){
        UserEntity user = new UserEntity();
        user.setUserName(oidcUser.getEmail());
        user.setActive(true);
        user.setUserRole(SystemConstant.USER_ROLE);
        String fullName = oidcUser.getFullName();
        user.setFullName(fullName == null || fullName.isBlank() ? oidcUser.getEmail() : fullName);
        user.setGoogleAccountId(oidcUser.getSubject());
        user.setEncrytedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userRepository.save(user);
    }
}
