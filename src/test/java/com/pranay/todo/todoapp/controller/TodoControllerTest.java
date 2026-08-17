package com.pranay.todo.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pranay.todo.todoapp.dto.CreateTodoRequest;
import com.pranay.todo.todoapp.dto.UpdateTodoRequest;
import com.pranay.todo.todoapp.exception.GlobalExceptionHandler;
import com.pranay.todo.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoControllerTest {

    private MockMvc mockMvc;
    private TodoService todoService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
        TodoController controller = new TodoController(todoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateTodo() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest("Buy milk", "2% Organic milk", false);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Buy milk")))
                .andExpect(jsonPath("$.description", is("2% Organic milk")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void shouldGetAllTodos() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Task 1")))
                .andExpect(jsonPath("$[1].title", is("Task 2")));
    }

    @Test
    void shouldGetFilterByCompleted() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        mockMvc.perform(get("/api/todos?completed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Task 2")));
    }

    @Test
    void shouldGetTodoById() throws Exception {
        var todo = todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));

        mockMvc.perform(get("/api/todos/" + todo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(todo.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Task 1")));
    }

    @Test
    void shouldReturn404WhenTodoNotFound() throws Exception {
        mockMvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Todo not found with id: 999")));
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        var todo = todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));

        UpdateTodoRequest updateRequest = new UpdateTodoRequest("Task 1 Updated", "Desc 1 Updated", true);

        mockMvc.perform(put("/api/todos/" + todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Task 1 Updated")))
                .andExpect(jsonPath("$.description", is("Desc 1 Updated")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void shouldPatchTodo() throws Exception {
        var todo = todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));

        UpdateTodoRequest patchRequest = new UpdateTodoRequest(null, null, true);

        mockMvc.perform(patch("/api/todos/" + todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Task 1")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        var todo = todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));

        mockMvc.perform(delete("/api/todos/" + todo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos/" + todo.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllTodos() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        mockMvc.perform(delete("/api/todos"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldSearchTodosByQueryParam() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Buy almond milk", "From grocery store", false));
        todoService.createTodo(new CreateTodoRequest("Learn Java", "Practice Spring Boot", true));

        mockMvc.perform(get("/api/todos/search?query=milk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Buy almond milk")));
    }

    @Test
    void shouldSearchTodosByQParam() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Buy almond milk", "From grocery store", false));
        todoService.createTodo(new CreateTodoRequest("Learn Java", "Practice Spring Boot", true));

        mockMvc.perform(get("/api/todos/search?q=Practice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Learn Java")));
    }

    @Test
    void shouldSearchTodosWithCompletedFilter() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Learn Spring Core", "Basics", false));
        todoService.createTodo(new CreateTodoRequest("Learn Spring Security", "Advanced", true));

        mockMvc.perform(get("/api/todos/search?query=Spring&completed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Learn Spring Security")));
    }

    @Test
    void shouldSearchTodosReturningEmptyWhenNoMatches() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));

        mockMvc.perform(get("/api/todos/search?query=NonExistentTask"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
