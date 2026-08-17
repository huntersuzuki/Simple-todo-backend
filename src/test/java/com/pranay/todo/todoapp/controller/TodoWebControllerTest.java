package com.pranay.todo.todoapp.controller;

import com.pranay.todo.todoapp.dto.CreateTodoRequest;
import com.pranay.todo.todoapp.model.Todo;
import com.pranay.todo.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoWebControllerTest {

    private MockMvc mockMvc;
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
        TodoWebController webController = new TodoWebController(todoService);
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    void shouldRenderIndexPageAtRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("todos", "totalCount", "completedCount", "pendingCount", "progressPercent", "currentFilter", "newTodo"))
                .andExpect(model().attribute("currentFilter", is("all")));
    }

    @Test
    void shouldRenderIndexPageAtTodos() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(model().attribute("currentFilter", is("all")));
    }

    @Test
    void shouldFilterCompletedTodos() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        mockMvc.perform(get("/todos?filter=completed"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("currentFilter", is("completed")))
                .andExpect(model().attribute("todos", hasSize(1)));
    }

    @Test
    void shouldFilterPendingTodos() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Task 1", "Desc 1", false));
        todoService.createTodo(new CreateTodoRequest("Task 2", "Desc 2", true));

        mockMvc.perform(get("/todos?filter=pending"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("currentFilter", is("pending")))
                .andExpect(model().attribute("todos", hasSize(1)));
    }

    @Test
    void shouldFilterBySearchQuery() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Buy milk", "Almond milk", false));
        todoService.createTodo(new CreateTodoRequest("Learn Thymeleaf", "Spring template engine", true));

        mockMvc.perform(get("/todos?query=Thymeleaf"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("searchQuery", is("Thymeleaf")))
                .andExpect(model().attribute("todos", hasSize(1)));
    }

    @Test
    void shouldCreateTodoViaForm() throws Exception {
        mockMvc.perform(post("/todos/create")
                        .param("title", "New Task")
                        .param("description", "New Details")
                        .param("completed", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void shouldToggleTodoCompletionViaForm() throws Exception {
        Todo created = todoService.createTodo(new CreateTodoRequest("Test Task", "Desc", false));

        mockMvc.perform(post("/todos/" + created.getId() + "/toggle"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void shouldUpdateTodoViaForm() throws Exception {
        Todo created = todoService.createTodo(new CreateTodoRequest("Test Task", "Desc", false));

        mockMvc.perform(post("/todos/" + created.getId() + "/update")
                        .param("title", "Updated Title")
                        .param("description", "Updated Desc")
                        .param("completed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void shouldDeleteTodoViaForm() throws Exception {
        Todo created = todoService.createTodo(new CreateTodoRequest("Test Task", "Desc", false));

        mockMvc.perform(post("/todos/" + created.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void shouldDeleteAllTodosViaForm() throws Exception {
        todoService.createTodo(new CreateTodoRequest("Test Task 1", "Desc", false));
        todoService.createTodo(new CreateTodoRequest("Test Task 2", "Desc", true));

        mockMvc.perform(post("/todos/delete-all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
