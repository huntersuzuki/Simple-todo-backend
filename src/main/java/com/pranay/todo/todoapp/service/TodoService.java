package com.pranay.todo.todoapp.service;

import com.pranay.todo.todoapp.dto.CreateTodoRequest;
import com.pranay.todo.todoapp.dto.UpdateTodoRequest;
import com.pranay.todo.todoapp.exception.TodoNotFoundException;
import com.pranay.todo.todoapp.model.Todo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final Map<Long, Todo> todoStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @jakarta.annotation.PostConstruct
    public void initDummyData() {
        if (todoStore.isEmpty()) {
            createTodo(new CreateTodoRequest("Welcome to Todo App", "This is a pre-loaded sample todo item.", false));
            createTodo(new CreateTodoRequest("Learn Spring Boot", "Explore REST APIs and in-memory data management.", true));
            createTodo(new CreateTodoRequest("Build a Frontend", "Connect a React or Vue frontend to this Todo API.", false));
            createTodo(new CreateTodoRequest("Setup CI/CD Pipeline", "Deploy the app automatically using Docker and GitHub Actions.", false));
        }
    }

    public List<Todo> getAllTodos(Boolean completed) {
        return todoStore.values().stream()
                .filter(todo -> completed == null || todo.isCompleted() == completed)
                .sorted(Comparator.comparing(Todo::getId))
                .collect(Collectors.toList());
    }

    public List<Todo> searchTodos(String query, Boolean completed) {
        String trimmedQuery = (query != null) ? query.trim().toLowerCase() : "";

        return todoStore.values().stream()
                .filter(todo -> {
                    if (completed != null && todo.isCompleted() != completed) {
                        return false;
                    }
                    if (trimmedQuery.isEmpty()) {
                        return true;
                    }
                    boolean matchTitle = todo.getTitle() != null && todo.getTitle().toLowerCase().contains(trimmedQuery);
                    boolean matchDescription = todo.getDescription() != null && todo.getDescription().toLowerCase().contains(trimmedQuery);
                    return matchTitle || matchDescription;
                })
                .sorted(Comparator.comparing(Todo::getId))
                .collect(Collectors.toList());
    }

    public Todo getTodoById(Long id) {
        Todo todo = todoStore.get(id);
        if (todo == null) {
            throw new TodoNotFoundException(id);
        }
        return todo;
    }

    public Todo createTodo(CreateTodoRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        Long newId = idGenerator.getAndIncrement();
        LocalDateTime now = LocalDateTime.now();

        boolean isCompleted = request.getCompleted() != null ? request.getCompleted() : false;

        Todo todo = new Todo(
                newId,
                request.getTitle().trim(),
                request.getDescription(),
                isCompleted,
                now,
                now
        );

        todoStore.put(newId, todo);
        return todo;
    }

    public Todo updateTodo(Long id, UpdateTodoRequest request) {
        Todo existingTodo = getTodoById(id);

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        existingTodo.setTitle(request.getTitle().trim());
        existingTodo.setDescription(request.getDescription());
        if (request.getCompleted() != null) {
            existingTodo.setCompleted(request.getCompleted());
        }
        existingTodo.setUpdatedAt(LocalDateTime.now());

        todoStore.put(id, existingTodo);
        return existingTodo;
    }

    public Todo patchTodo(Long id, UpdateTodoRequest request) {
        Todo existingTodo = getTodoById(id);

        if (request.getTitle() != null) {
            if (request.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            existingTodo.setTitle(request.getTitle().trim());
        }

        if (request.getDescription() != null) {
            existingTodo.setDescription(request.getDescription());
        }

        if (request.getCompleted() != null) {
            existingTodo.setCompleted(request.getCompleted());
        }

        existingTodo.setUpdatedAt(LocalDateTime.now());

        todoStore.put(id, existingTodo);
        return existingTodo;
    }

    public void deleteTodo(Long id) {
        if (!todoStore.containsKey(id)) {
            throw new TodoNotFoundException(id);
        }
        todoStore.remove(id);
    }

    public void deleteAllTodos() {
        todoStore.clear();
    }

    public Todo toggleTodoCompletion(Long id) {
        Todo existingTodo = getTodoById(id);
        existingTodo.setCompleted(!existingTodo.isCompleted());
        existingTodo.setUpdatedAt(LocalDateTime.now());
        todoStore.put(id, existingTodo);
        return existingTodo;
    }

    public long getTotalCount() {
        return todoStore.size();
    }

    public long getCompletedCount() {
        return todoStore.values().stream().filter(Todo::isCompleted).count();
    }

    public long getPendingCount() {
        return todoStore.values().stream().filter(t -> !t.isCompleted()).count();
    }
}
