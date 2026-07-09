package com.codenzyme.todolist.controller;

import java.util.List;
import java.util.UUID;

import com.codenzyme.todolist.entity.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codenzyme.todolist.dto.CreateTodoDto;
import com.codenzyme.todolist.dto.TodoResponse;
import com.codenzyme.todolist.service.TodoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos(@AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listTodo(currentUser));
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody CreateTodoDto body, @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addTodo(body.name(), currentUser));
    }

    @PreAuthorize("hasRole('PAID')")
    @PatchMapping("/{uuid}/updateTitle")
    public ResponseEntity<TodoResponse> updateTitle(@PathVariable UUID uuid, @Valid @RequestBody CreateTodoDto body, @AuthenticationPrincipal AppUser currentUser) {
        return service.updateTitle(uuid, body.name(), currentUser)
            .map(todo -> ResponseEntity.ok(todo))
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{uuid}/toggle")
    public ResponseEntity<TodoResponse> toggleTodo(@PathVariable UUID uuid, @AuthenticationPrincipal AppUser currentUser) {
        return service.toggleFinished(uuid, currentUser)
            .map(todo->ResponseEntity.ok(todo))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID uuid, @AuthenticationPrincipal AppUser currentUser) {
        if (service.removeTodo(uuid, currentUser)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}
