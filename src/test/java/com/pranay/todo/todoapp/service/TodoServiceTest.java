package com.pranay.todo.todoapp.service;

import com.pranay.todo.todoapp.dto.CreateTodoRequest;
import com.pranay.todo.todoapp.dto.UpdateTodoRequest;
import com.pranay.todo.todoapp.exception.TodoNotFoundException;
import com.pranay.todo.todoapp.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
    }

    @Test
    void shouldInitDummyData() {
        todoService.initDummyData();
        List<Todo> todos = todoService.getAllTodos(null);
        assertEquals(4, todos.size());
    }

    @Test
    void shouldCreateAndGetTodo() {
        CreateTodoRequest request = new CreateTodoRequest("Test Title", "Test Description", false);
        Todo created = todoService.createTodo(request);

        assertNotNull(created.getId());
        assertEquals("Test Title", created.getTitle());
        assertEquals("Test Description", created.getDescription());
        assertFalse(created.isCompleted());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());

        Todo retrieved = todoService.getTodoById(created.getId());
        assertEquals(created.getId(), retrieved.getId());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmptyOnCreate() {
        CreateTodoRequest request = new CreateTodoRequest("   ", "Description", false);
        assertThrows(IllegalArgumentException.class, () -> todoService.createTodo(request));
    }

    @Test
    void shouldFilterTodosByCompletedStatus() {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        List<Todo> all = todoService.getAllTodos(null);
        assertEquals(2, all.size());

        List<Todo> active = todoService.getAllTodos(false);
        assertEquals(1, active.size());
        assertEquals("Task 1", active.get(0).getTitle());

        List<Todo> completed = todoService.getAllTodos(true);
        assertEquals(1, completed.size());
        assertEquals("Task 2", completed.get(0).getTitle());
    }

    @Test
    void shouldUpdateTodo() {
        Todo created = todoService.createTodo(new CreateTodoRequest("Old Title", "Old Desc", false));

        UpdateTodoRequest updateRequest = new UpdateTodoRequest("New Title", "New Desc", true);
        Todo updated = todoService.updateTodo(created.getId(), updateRequest);

        assertEquals("New Title", updated.getTitle());
        assertEquals("New Desc", updated.getDescription());
        assertTrue(updated.isCompleted());
    }

    @Test
    void shouldPatchTodo() {
        Todo created = todoService.createTodo(new CreateTodoRequest("Original Title", "Original Desc", false));

        UpdateTodoRequest patchRequest = new UpdateTodoRequest(null, null, true);
        Todo patched = todoService.patchTodo(created.getId(), patchRequest);

        assertEquals("Original Title", patched.getTitle());
        assertEquals("Original Desc", patched.getDescription());
        assertTrue(patched.isCompleted());
    }

    @Test
    void shouldDeleteTodo() {
        Todo created = todoService.createTodo(new CreateTodoRequest("Task", "Desc", false));
        todoService.deleteTodo(created.getId());

        assertThrows(TodoNotFoundException.class, () -> todoService.getTodoById(created.getId()));
    }

    @Test
    void shouldDeleteAllTodos() {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        todoService.deleteAllTodos();
        assertTrue(todoService.getAllTodos(null).isEmpty());
    }

    @Test
    void shouldSearchTodosByTitle() {
        todoService.createTodo(new CreateTodoRequest("Buy groceries", "Milk, Eggs, Bread", false));
        todoService.createTodo(new CreateTodoRequest("Learn Spring", "Spring Boot basics", true));
        todoService.createTodo(new CreateTodoRequest("Read a book", "Sci-Fi novel", false));

        List<Todo> results = todoService.searchTodos("spring", null);
        assertEquals(1, results.size());
        assertEquals("Learn Spring", results.get(0).getTitle());
    }

    @Test
    void shouldSearchTodosByDescription() {
        todoService.createTodo(new CreateTodoRequest("Shopping", "Buy groceries and Milk", false));
        todoService.createTodo(new CreateTodoRequest("Study", "Learn Spring Boot", true));

        List<Todo> results = todoService.searchTodos("groceries", null);
        assertEquals(1, results.size());
        assertEquals("Shopping", results.get(0).getTitle());
    }

    @Test
    void shouldSearchTodosWithCompletedFilter() {
        todoService.createTodo(new CreateTodoRequest("Task A", "Learn Java", false));
        todoService.createTodo(new CreateTodoRequest("Task B", "Learn Java advanced", true));

        List<Todo> completedJava = todoService.searchTodos("Java", true);
        assertEquals(1, completedJava.size());
        assertEquals("Task B", completedJava.get(0).getTitle());

        List<Todo> activeJava = todoService.searchTodos("Java", false);
        assertEquals(1, activeJava.size());
        assertEquals("Task A", activeJava.get(0).getTitle());
    }

    @Test
    void shouldReturnAllWhenSearchQueryIsNullOrBlank() {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        List<Todo> nullQueryResults = todoService.searchTodos(null, null);
        assertEquals(2, nullQueryResults.size());

        List<Todo> blankQueryResults = todoService.searchTodos("   ", null);
        assertEquals(2, blankQueryResults.size());

        List<Todo> blankQueryWithCompletedFilter = todoService.searchTodos(" ", true);
        assertEquals(1, blankQueryWithCompletedFilter.size());
        assertEquals("Task 2", blankQueryWithCompletedFilter.get(0).getTitle());
    }

    @Test
    void shouldReturnEmptyListWhenNoMatchesFound() {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));

        List<Todo> results = todoService.searchTodos("NonexistentQuery", null);
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldHandleNullDescriptionDuringSearch() {
        todoService.createTodo(new CreateTodoRequest("Fix bug", null, false));

        List<Todo> results = todoService.searchTodos("bug", null);
        assertEquals(1, results.size());
        assertEquals("Fix bug", results.get(0).getTitle());
    }
}
