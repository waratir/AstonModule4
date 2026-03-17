package com.module4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO with user data for response")
public class UserResponseDTO {

    @Schema(description = "User ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "User Name", example = "John Doe")
    private String name;

    @Schema(description = "User Email", example = "john@example.com")
    private String email;

    @Schema(description = "User Age", example = "25")
    private Integer age;

    @Schema(description = "Created Date", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Updated Date", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

}
