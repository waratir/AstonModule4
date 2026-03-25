package com.module4.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    private String email;
    private OperationType operationType;

    public enum OperationType {
        CREATED, DELETED
    }
}
