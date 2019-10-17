package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
public class TodoControllerTest {
    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    public void getAll() throws Exception {
        List<Todo> todo = new ArrayList<>();

        when(todoRepository.getAll()).thenReturn(todo);

        ResultActions result = mvc.perform(get("/todos"));

        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", is(todo)));
    }

    @Test
    public void getTodo() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(1, "title", true, 1));
        when(todoRepository.findById(1)).thenReturn(todo);

        ResultActions result = mvc.perform(get("/todos/{id}" , 1L));

        result.andExpect(status().isOk()).andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.completed", is(true)))
                .andExpect(jsonPath("$.order", is(1)));
    }

    @Test
    public void getDidNotFoundTodo() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(1, "title", true, 1));
        when(todoRepository.findById(1)).thenReturn(todo);

        ResultActions result = mvc.perform(get("/todos/{id}" , 2L));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void saveTodo() throws Exception {

        Todo todo = new Todo(1, "title", true, 1);

        ResultActions result = mvc.perform(post("/todos")
                .content(asJsonString(todo))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.completed", is(true)))
                .andExpect(jsonPath("$.order", is(1)));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
