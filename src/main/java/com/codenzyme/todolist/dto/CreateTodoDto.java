package com.codenzyme.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoDto(@NotBlank @Size(max=500, message="Todo Task name cannot be more than 500 chars") String name) {}