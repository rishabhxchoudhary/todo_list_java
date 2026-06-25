package com.codenzyme.todolist.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean finished;
}
