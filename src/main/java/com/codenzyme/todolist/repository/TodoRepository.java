package com.codenzyme.todolist.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.codenzyme.todolist.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codenzyme.todolist.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findAllByOwnerOrderByUpdatedAtDesc(AppUser owner);
    Optional<Todo> findByIdAndOwner(UUID id, AppUser owner);
}
