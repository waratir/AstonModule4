package com.module4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for full user update (PUT)")
public class UserUpdateDTO {

    @Schema(description = "User Name", example = "John Updated", required = true)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "User Email", example = "john.updated@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User Age", example = "26", required = true)
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    private Integer age;
}
