package com.codenzyme.todolist.dto;

import com.codenzyme.todolist.entity.AppUser;

public record RotateResponse(AppUser appUser, String token) {
}
