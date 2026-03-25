package com.module4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for partial user update (PATCH)")
public class UserPatchDTO {

    @Schema(description = "User Name", example = "John Patched")
    private String name;

    @Schema(description = "User Email", example = "john.patched@example.com")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User Age", example = "27")
    @Min(value = 0, message = "Age must be positive")
    private Integer age;

}
