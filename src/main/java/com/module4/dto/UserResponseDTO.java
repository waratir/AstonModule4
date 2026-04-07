package com.module4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "users", itemRelation = "user")
@Schema(description = "DTO with user data for response with HATEOAS links")
public class UserResponseDTO extends RepresentationModel<UserResponseDTO> {

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
