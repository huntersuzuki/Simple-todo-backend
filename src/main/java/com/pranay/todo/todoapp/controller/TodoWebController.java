package com.pranay.todo.todoapp.controller;

import com.pranay.todo.todoapp.dto.CreateTodoRequest;
import com.pranay.todo.todoapp.dto.UpdateTodoRequest;
import com.pranay.todo.todoapp.model.Todo;
import com.pranay.todo.todoapp.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TodoWebController {

    private final TodoService todoService;

    public TodoWebController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping({"/", "/todos"})
    public String index(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String q,
            Model model) {

        String searchTerm = (query != null && !query.trim().isEmpty()) ? query : q;

        Boolean completedFilter = completed;
        String activeTab = "all";

        if (completedFilter != null) {
            activeTab = completedFilter ? "completed" : "pending";
        } else if ("completed".equalsIgnoreCase(filter)) {
            completedFilter = true;
            activeTab = "completed";
        } else if ("pending".equalsIgnoreCase(filter) || "active".equalsIgnoreCase(filter)) {
            completedFilter = false;
            activeTab = "pending";
        }

        List<Todo> todos;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            todos = todoService.searchTodos(searchTerm, completedFilter);
        } else {
            todos = todoService.getAllTodos(completedFilter);
        }

        long totalCount = todoService.getTotalCount();
        long completedCount = todoService.getCompletedCount();
        long pendingCount = todoService.getPendingCount();

        int progressPercent = totalCount > 0 ? (int) Math.round(((double) completedCount / totalCount) * 100) : 0;

        model.addAttribute("todos", todos);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("progressPercent", progressPercent);
        model.addAttribute("currentFilter", activeTab);
        model.addAttribute("searchQuery", (searchTerm != null) ? searchTerm.trim() : "");
        model.addAttribute("newTodo", new CreateTodoRequest("", "", false));

        return "index";
    }

    @PostMapping("/todos/create")
    public String createTodo(@ModelAttribute("newTodo") CreateTodoRequest request, RedirectAttributes redirectAttributes) {
        try {
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                todoService.createTodo(request);
                redirectAttributes.addFlashAttribute("successMessage", "Task created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Task title cannot be empty.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/toggle")
    public String toggleTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Todo updated = todoService.toggleTodoCompletion(id);
            String status = updated.isCompleted() ? "completed" : "marked as pending";
            redirectAttributes.addFlashAttribute("successMessage", "Task " + status + "!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/update")
    public String updateTodo(@PathVariable Long id, @ModelAttribute UpdateTodoRequest request, RedirectAttributes redirectAttributes) {
        try {
            todoService.updateTodo(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            todoService.deleteTodo(id);
            redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/todos";
    }

    @PostMapping("/todos/delete-all")
    public String deleteAllTodos(RedirectAttributes redirectAttributes) {
        try {
            todoService.deleteAllTodos();
            redirectAttributes.addFlashAttribute("successMessage", "All tasks cleared!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/todos";
    }
}
