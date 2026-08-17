package com.pranay.todo.todoapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class WelcomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Welcome to the Todo API! The application is running successfully.");
        response.put("status", "UP");
        response.put("version", "1.0.0");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("getAllTodos", "GET /api/todos");
        endpoints.put("getCompletedTodos", "GET /api/todos?completed=true");
        endpoints.put("getPendingTodos", "GET /api/todos?completed=false");
        endpoints.put("searchTodos", "GET /api/todos/search?query={query}&completed={completed}");
        endpoints.put("getTodoById", "GET /api/todos/{id}");
        endpoints.put("createTodo", "POST /api/todos");
        endpoints.put("updateTodo", "PUT /api/todos/{id}");
        endpoints.put("patchTodo", "PATCH /api/todos/{id}");
        endpoints.put("deleteTodo", "DELETE /api/todos/{id}");
        endpoints.put("deleteAllTodos", "DELETE /api/todos");

        response.put("endpoints", endpoints);

        return ResponseEntity.ok(response);
    }
}
