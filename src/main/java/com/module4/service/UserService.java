package com.module4.service;

import com.module4.dto.UserCreateDTO;
import com.module4.dto.UserPatchDTO;
import com.module4.dto.UserResponseDTO;
import com.module4.dto.UserUpdateDTO;

public interface UserService {
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserCreateDTO createDTO);
    UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO);
    UserResponseDTO patchUser(Long id, UserPatchDTO patchDTO);
    void deleteUser(Long id);
}