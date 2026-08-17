package com.pranay.todo.todoapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TodoappApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldRenderThymeleafIndexPage() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("TaskFlow")))
				.andExpect(content().string(containsString("Create New Task")))
				.andExpect(content().string(containsString("Completion Progress")));
	}

	@Test
	void shouldRenderThymeleafTodosPageWithData() throws Exception {
		mockMvc.perform(get("/todos?filter=pending"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("TaskFlow")))
				.andExpect(content().string(containsString("Pending")));
	}
}
