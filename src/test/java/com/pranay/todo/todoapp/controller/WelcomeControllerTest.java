package com.pranay.todo.todoapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WelcomeControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new WelcomeController()).build();
    }

    @Test
    void shouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Welcome to the Todo API!")))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.version", is("1.0.0")))
                .andExpect(jsonPath("$.endpoints.getAllTodos", is("GET /api/todos")));
    }
}
