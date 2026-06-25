package com.codenzyme.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateTodoDto {
    @NotBlank
    @Size(max=500, message="Todo Task name cannot be more than 500 chars")
    private String name;
}
