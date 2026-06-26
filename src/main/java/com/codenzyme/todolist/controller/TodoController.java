package com.codenzyme.todolist.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listTodo());
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody CreateTodoDto body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addTodo(body.name()));
    }

    @PatchMapping("/{uuid}/updateTitle")
    public ResponseEntity<TodoResponse> updateTitle(@PathVariable UUID uuid, @Valid @RequestBody CreateTodoDto body) {
        return service.updateTitle(uuid, body.name())
            .map(todo -> ResponseEntity.ok(todo))
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{uuid}/toggle")
    public ResponseEntity<TodoResponse> toggleTodo(@PathVariable UUID uuid) {
        return service.toggleFinished(uuid)
            .map(todo->ResponseEntity.ok(todo))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID uuid) {
        service.removeTodo(uuid);
        return ResponseEntity.noContent().build();
    }
    
}
