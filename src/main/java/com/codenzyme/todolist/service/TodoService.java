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
        TodoResponse todoResponse =  new TodoResponse(todo.getId(),todo.getName(), todo.getFinished());
        return todoResponse;
    }

    public List<TodoResponse> listTodo() {
        return repo.findAllByOrderByUpdatedAtDesc().stream().map(todo->{
            return toTodoResponse(todo);
        }).toList();
    }

    public Optional<TodoResponse> updateTodo(UUID uuid, String updatedName, Boolean toggleFinished, Boolean updateTitle) {
        Optional<Todo> check_todo = repo.findById(uuid);
        if (check_todo.isPresent()) {
            Todo todo = check_todo.get();
            if (updateTitle) todo.setName(updatedName);
            if (toggleFinished) todo.setFinished(!todo.getFinished());
            todo.setUpdatedAt(Instant.now());
            repo.save(todo);
            return Optional.of(toTodoResponse(todo));
        } else {
            return Optional.empty();
        }
    }

}
