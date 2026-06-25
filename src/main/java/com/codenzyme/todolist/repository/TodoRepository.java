package com.codenzyme.todolist.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codenzyme.todolist.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findAllByOrderByUpdatedAtDesc();
}
