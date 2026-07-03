package com.codenzyme.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(@NotBlank String username,@NotBlank @Size(min=8, max=72) String password) {
}
