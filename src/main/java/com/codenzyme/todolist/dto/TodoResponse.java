package com.codenzyme.todolist.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
public class TodoResponse {
    private UUID id;
    private String name;
    private Boolean finished;
}
