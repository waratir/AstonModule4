package com.module4.controller;

import com.module4.dto.UserCreateDTO;
import com.module4.dto.UserPatchDTO;
import com.module4.dto.UserResponseDTO;
import com.module4.dto.UserUpdateDTO;
import com.module4.exception.BusinessException;
import com.module4.exception.ResourceNotFoundException;
import com.module4.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Tests for User controller")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponseDTO userResponse;
    private UserCreateDTO userCreate;
    private UserUpdateDTO userUpdate;
    private UserPatchDTO userPatch;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        userResponse = UserResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .age(25)
                .createdAt(now)
                .build();

        userCreate = UserCreateDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .age(25)
                .build();

        userUpdate = UserUpdateDTO.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .age(26)
                .build();

        userPatch = UserPatchDTO.builder()
                .email("john.patched@example.com")
                .build();
    }

    @Nested
    @DisplayName("GET /api/users/{id} - get user by ID")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should get user by ID with status 200")
        void getUserById_WithValidId_ShouldReturnUser() throws Exception {
            when(userService.getUserById(1L)).thenReturn(userResponse);

            mockMvc.perform(get("/api/users/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john@example.com")))
                    .andExpect(jsonPath("$.age", is(25)))
                    .andExpect(jsonPath("$.createdAt").exists());

            verify(userService, times(1)).getUserById(1L);
        }

        @Test
        @DisplayName("Should get 404, if user not found")
        void getUserById_WithInvalidId_ShouldReturn404() throws Exception {
            when(userService.getUserById(999L))
                    .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

            mockMvc.perform(get("/api/users/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", containsString("not found")));

            verify(userService, times(1)).getUserById(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/users - creating user")
    class CreateUserTests {

        @Test
        @DisplayName("Should to create user and return 201")
        void createUser_WithValidData_ShouldReturn201() throws Exception {
            when(userService.createUser(any(UserCreateDTO.class))).thenReturn(userResponse);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreate)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", containsString("/api/users/1")))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john@example.com")))
                    .andExpect(jsonPath("$.age", is(25)));

            verify(userService, times(1)).createUser(any(UserCreateDTO.class));
        }

        @Test
        @DisplayName("Should return 400 when created with an existing email")
        void createUser_WithExistingEmail_ShouldReturn400() throws Exception {
            when(userService.createUser(any(UserCreateDTO.class)))
                    .thenThrow(new BusinessException("User with email john@example.com already exists"));

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreate)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }

        @Test
        @DisplayName("Should return 400 if name is empty")
        void createUser_WithEmptyName_ShouldReturn400() throws Exception {
            userCreate.setName("");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreate)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name", containsString("required")));
        }

        @Test
        @DisplayName("Should return 400 for invalid email")
        void createUser_WithInvalidEmail_ShouldReturn400() throws Exception {
            userCreate.setEmail("not-an-email");

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreate)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email", containsString("Invalid email")));
        }

        @Test
        @DisplayName("Should return 400 if age is negative")
        void createUser_WithNegativeAge_ShouldReturn400() throws Exception {
            userCreate.setAge(-5);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreate)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.age", containsString("Age must be positive")));
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id} - full user update")
    class UpdateUserTests {

        @Test
        @DisplayName("Should completely refresh the user and return 200")
        void updateUser_WithValidData_ShouldReturn200() throws Exception {
            UserResponseDTO updatedResponse = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Updated")
                    .email("john.updated@example.com")
                    .age(26)
                    .createdAt(now)
                    .build();

            when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedResponse);

            mockMvc.perform(put("/api/users/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdate)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Updated")))
                    .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                    .andExpect(jsonPath("$.age", is(26)));

            verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateDTO.class));
        }

        @Test
        @DisplayName("Should return 404 when updating a non-existent user")
        void updateUser_WithInvalidId_ShouldReturn404() throws Exception {
            when(userService.updateUser(eq(999L), any(UserUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

            mockMvc.perform(put("/api/users/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdate)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/{id} - partial user update")
    class PatchUserTests {

        @Test
        @DisplayName("Should partially update the user and return 200")
        void patchUser_WithValidData_ShouldReturn200() throws Exception {
            UserResponseDTO patchedResponse = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john.patched@example.com")
                    .age(25)
                    .createdAt(now)
                    .build();

            when(userService.patchUser(eq(1L), any(UserPatchDTO.class))).thenReturn(patchedResponse);

            mockMvc.perform(patch("/api/users/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userPatch)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", is("john.patched@example.com")))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.age", is(25)));

            verify(userService, times(1)).patchUser(eq(1L), any(UserPatchDTO.class));
        }

        @Test
        @DisplayName("Should accept empty PATCH (do not update anything))")
        void patchUser_WithEmptyBody_ShouldReturn200() throws Exception {
            when(userService.patchUser(eq(1L), any(UserPatchDTO.class))).thenReturn(userResponse);

            mockMvc.perform(patch("/api/users/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(userService, times(1)).patchUser(eq(1L), any(UserPatchDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id} - deleting a user")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete the user and return 204")
        void deleteUser_WithValidId_ShouldReturn204() throws Exception {
            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/api/users/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).deleteUser(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting a non-existent user")
        void deleteUser_WithInvalidId_ShouldReturn404() throws Exception {
            doThrow(new ResourceNotFoundException("User not found with id: 999"))
                    .when(userService).deleteUser(999L);

            mockMvc.perform(delete("/api/users/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).deleteUser(999L);
        }
    }

}
