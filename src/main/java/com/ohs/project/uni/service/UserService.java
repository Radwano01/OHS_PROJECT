package com.ohs.project.uni.service;

import com.ohs.project.uni.dto.*;
import com.ohs.project.uni.entity.User;

public interface UserService {
    void save(CreateDTO createDTO);

    User edit(String userId, EditDTO editDTO);

    void delete(String userId);

    UserAndTokenDTO login(LoginDTO loginDTO);

    void editPassword(String userId, PasswordDTO passwordDTO);

    User getDetails(String userId);
}
