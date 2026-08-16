package com.pranay.todo.todoapp.controller;

import com.pranay.todo.todoapp.dto.CreateTodoRequest;
import com.pranay.todo.todoapp.dto.UpdateTodoRequest;
import com.pranay.todo.todoapp.model.Todo;
import com.pranay.todo.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@RequestParam(required = false) Boolean completed) {
        List<Todo> todos = todoService.getAllTodos(completed);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody CreateTodoRequest request) {
        Todo createdTodo = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        Todo updatedTodo = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updatedTodo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> patchTodo(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        Todo patchedTodo = todoService.patchTodo(id, request);
        return ResponseEntity.ok(patchedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTodos() {
        todoService.deleteAllTodos();
        return ResponseEntity.noContent().build();
    }
}
