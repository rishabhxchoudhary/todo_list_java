package com.codenzyme.todolist.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public TodoResponse addTodo(String name) {
        Todo newTodoItem = new Todo();
        newTodoItem.setName(name);
        newTodoItem.setFinished(false);
        newTodoItem.setCreatedAt(Instant.now());
        newTodoItem.setUpdatedAt(Instant.now());
        return toTodoResponse(repo.save(newTodoItem));
    }

    public void removeTodo(UUID uuid) {
        repo.deleteById(uuid);
    }

    private TodoResponse toTodoResponse(Todo todo) {
        TodoResponse todoResponse = new TodoResponse(todo.getId(), todo.getName(), todo.getFinished());
        return todoResponse;
    }

    public List<TodoResponse> listTodo() {
        return repo.findAllByOrderByUpdatedAtDesc().stream().map(this::toTodoResponse).toList();
    }

    public Optional<TodoResponse> updateTitle(UUID uuid, String name) {
        return repo.findById(uuid).map(todo -> {
            todo.setName(name);
            todo.setUpdatedAt(Instant.now());
            return toTodoResponse(repo.save(todo));
        });
    }

    public Optional<TodoResponse> toggleFinished(UUID uuid) {
        return repo.findById(uuid).map(todo -> {
            todo.setFinished(!todo.getFinished());
            todo.setUpdatedAt(Instant.now());
            return toTodoResponse(repo.save(todo));
        });
    }

}
