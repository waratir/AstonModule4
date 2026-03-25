package com.module4.controller;

import com.module4.dto.UserCreateDTO;
import com.module4.dto.UserPatchDTO;
import com.module4.dto.UserResponseDTO;
import com.module4.dto.UserUpdateDTO;
import com.module4.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Returns a user based on their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by id: {}", id);
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user and returns the created data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                    {
                        "name": "Name is required",
                        "email": "Invalid email format",
                        "age": "Age must be positive"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                    {
                        "message": "User with email john@example.com already exists"
                    }
                    """
                            )
                    )
            )
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        log.info("REST request to create user with email: {}", createDTO.getEmail());

        UserResponseDTO createdUser = userService.createUser(createDTO);

        URI location = URI.create("/api/users/" + createdUser.getId());

        return ResponseEntity
                .created(location)
                .body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Fully update a user",
            description = "Updates all fields of a user (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {

        log.info("REST request to update user with id: {}", id);

        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update a user",
            description = "Updates only the provided fields of a user (PATCH)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> patchUser(
            @PathVariable Long id,
            @Valid @RequestBody UserPatchDTO patchDTO) {

        log.info("REST request to patch user with id: {}", id);

        UserResponseDTO patchedUser = userService.patchUser(id, patchDTO);
        return ResponseEntity.ok(patchedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user with id: {}", id);

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();  // status 204 No Content
    }
}