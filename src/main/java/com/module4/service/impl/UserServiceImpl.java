package com.module4.service.impl;

import com.module4.dto.UserCreateDTO;
import com.module4.dto.UserPatchDTO;
import com.module4.dto.UserResponseDTO;
import com.module4.dto.UserUpdateDTO;
import com.module4.dto.event.UserEvent;
import com.module4.entity.User;
import com.module4.exception.BusinessException;
import com.module4.exception.ResourceNotFoundException;
import com.module4.mapper.UserMapper;
import com.module4.messaging.UserEventProducer;
import com.module4.repository.UserRepository;
import com.module4.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for User entity.
 * Provides methods for user gets, creates, updates, partial update, and management.
 */
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id User entity id
     */
    public UserResponseDTO getUserById(Long id) {
        log.debug("Getting user by id: {}", id);
        User user = findUserById(id);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Registers a new user after basic validation.
     *
     * @param createDTO Entity to be created.
     */
    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        log.debug("Creating new user with email: {}", createDTO.getEmail());

        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new BusinessException(
                    String.format("User with email %s already exists", createDTO.getEmail()));
        }

        User user = userMapper.toEntity(createDTO);
        User savedUser = userRepository.save(user);

        sendUserEvent(savedUser, UserEvent.OperationType.CREATED);

        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponseDTO(savedUser);
    }

    /**
     * Updates existing user information.
     *
     * @param id        User entity id.
     * @param updateDTO User's field for update.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        log.debug("Updating user with id: {}", id);

        User user = findUserById(id);

        if (!user.getEmail().equals(updateDTO.getEmail()) &&
                userRepository.existsByEmail(updateDTO.getEmail())) {
            throw new BusinessException(
                    String.format("User with email %s already exists", updateDTO.getEmail())
            );
        }

        userMapper.updateEntity(user, updateDTO);

        log.info("User updated successfully with id: {}", id);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Partial user update.
     *
     * @param id       User entity id.
     * @param patchDTO User's field for update.
     */
    @Override
    @Transactional
    public UserResponseDTO patchUser(Long id, UserPatchDTO patchDTO) {
        log.debug("Patching user with id: {}", id);

        User user = findUserById(id);

        if (patchDTO.getEmail() != null &&
                !patchDTO.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(patchDTO.getEmail())) {
            throw new BusinessException(
                    String.format("User with email %s already exists", patchDTO.getEmail())
            );
        }

        userMapper.updatePartial(user, patchDTO);

        log.info("User patched successfully with id: {}", id);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Removes a user from the system by ID.
     *
     * @param id User entity id
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.deleteById(id);
        sendUserEvent(user, UserEvent.OperationType.DELETED);
        log.info("User deleted successfully with id: {}", id);
    }

    private void sendUserEvent(User user, UserEvent.OperationType operationType) {
        UserEvent event = UserEvent.builder()
                .email(user.getEmail())
                .operationType(operationType)
                .build();
        userEventProducer.sendUserEvent(event);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
