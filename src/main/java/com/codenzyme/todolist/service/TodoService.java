package com.codenzyme.todolist.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.codenzyme.todolist.entity.AppUser;
import org.springframework.stereotype.Service;

import com.codenzyme.todolist.dto.TodoResponse;
import com.codenzyme.todolist.entity.Todo;
import com.codenzyme.todolist.repository.TodoRepository;

@Service
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public TodoResponse addTodo(String name, AppUser currentUser) {
        Todo newTodoItem = new Todo();
        newTodoItem.setName(name);
        newTodoItem.setFinished(false);
        newTodoItem.setCreatedAt(Instant.now());
        newTodoItem.setUpdatedAt(Instant.now());
        newTodoItem.setOwner(currentUser);
        return toTodoResponse(repo.save(newTodoItem));
    }

    public boolean removeTodo(UUID uuid, AppUser currentUser) {
        return repo.findByIdAndOwner(uuid, currentUser).map(todo -> {
            repo.delete(todo);
            return true;
        }).orElse(false);
    }

    private TodoResponse toTodoResponse(Todo todo) {
        TodoResponse todoResponse = new TodoResponse(todo.getId(), todo.getName(), todo.getFinished());
        return todoResponse;
    }

    public List<TodoResponse> listTodo(AppUser currentUser) {
        return repo.findAllByOwnerOrderByUpdatedAtDesc(currentUser).stream().map(this::toTodoResponse).toList();
    }

    public Optional<TodoResponse> updateTitle(UUID uuid, String name, AppUser currentUser) {
        return repo.findByIdAndOwner(uuid, currentUser).map(todo -> {
            todo.setName(name);
            todo.setUpdatedAt(Instant.now());
            return toTodoResponse(repo.save(todo));
        });
    }

    public Optional<TodoResponse> toggleFinished(UUID uuid, AppUser currentUser) {
        return repo.findByIdAndOwner(uuid, currentUser).map(todo -> {
            todo.setFinished(!todo.getFinished());
            todo.setUpdatedAt(Instant.now());
            return toTodoResponse(repo.save(todo));
        });
    }

}
