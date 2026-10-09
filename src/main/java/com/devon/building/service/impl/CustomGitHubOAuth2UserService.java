package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomGitHubOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User githubUser = super.loadUser(userRequest);
        Map<String, Object> attributes = githubUser.getAttributes();

        Object idAttribute = attributes.get("id");
        Object loginAttribute = attributes.get("login");
        if (idAttribute == null || loginAttribute == null) {
            throw new OAuth2AuthenticationException("GitHub account information is incomplete");
        }

        String githubAccountId = idAttribute.toString();
        String githubLogin = loginAttribute.toString();
        UserEntity user = userRepository.findByGithubAccountId(githubAccountId)
                .orElseGet(() -> createGitHubUser(githubAccountId, githubLogin, attributes));

        if (!user.isActive()) {
            throw new OAuth2AuthenticationException("This account is inactive");
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole())),
                attributes,
                "id");
    }

    private UserEntity createGitHubUser(String githubAccountId,
                                       String githubLogin,
                                       Map<String, Object> attributes) {
        String localUserName;
        try {
            localUserName = "gh" + new BigInteger(githubAccountId).toString(36);
        } catch (NumberFormatException exception) {
            throw new OAuth2AuthenticationException("GitHub account ID is invalid");
        }

        // Never attach a GitHub identity to an unrelated local account by username or email.
        if (userRepository.findByUserName(localUserName) != null) {
            throw new OAuth2AuthenticationException("Generated GitHub username is already in use");
        }

        Object nameAttribute = attributes.get("name");
        String fullName = nameAttribute == null || nameAttribute.toString().isBlank()
                ? githubLogin
                : nameAttribute.toString();

        UserEntity user = new UserEntity();
        user.setUserName(localUserName);
        user.setFullName(fullName);
        user.setActive(true);
        user.setUserRole(SystemConstant.USER_ROLE);
        user.setGithubAccountId(githubAccountId);
        user.setEncrytedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userRepository.save(user);
    }
}
