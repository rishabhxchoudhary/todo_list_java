package com.codenzyme.todolist.dto;

import java.util.UUID;

public record TodoResponse(UUID id, String name, Boolean finished) {}