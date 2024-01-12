package com.mjc.school.controller.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.validator.NewsInfoValidator;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    private static final String BASE_PATH = "/api/v1/comments";
    @MockBean
    private CommentService commentService;
    @MockBean
    private NewsRepository newsRepository;
    @MockBean
    private NewsInfoValidator newsInfoValidator;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void readAll() {
        doReturn(List.of(buildCommentResponse(1L, "comment1", 1L),
                buildCommentResponse(2L, "comment2", 2L)))
                .when(commentService).readAll(any());
        Pageable pageable = PageRequest.of(0, 10);

        given()
                .param("page", pageable.getPageNumber())
                .param("size", pageable.getPageSize())
                .when()
                .get(BASE_PATH)
                .then()
                .assertThat()
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].content", is("comment1"))
                .body("[0].news.id", is(1))
                .body("[1].id", is(2))
                .body("[1].content", is("comment2"))
                .body("[1].news.id", is(2))
                .statusCode(200);

        verify(commentService).readAll(pageable);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void readAllWhenCommentNotFound() {
        doReturn(List.of()).when(commentService).readAll(any());
        Pageable pageable = PageRequest.of(0, 10);

        given()
                .param("page", pageable.getPageNumber())
                .param("size", pageable.getPageSize())
                .when()
                .get(BASE_PATH)
                .then()
                .assertThat()
                .body("size()", is(0))
                .statusCode(200);

        verify(commentService).readAll(pageable);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void readById() {
        doReturn(Optional.of(buildCommentResponse(1L, "comment1", 1L)))
                .when(commentService).readById(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("comment1"))
                .body("news.id", is(1))
                .statusCode(200);

        verify(commentService).readById(1L);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void readByIdWhenCommentNotFound() {
        doReturn(Optional.empty()).when(commentService).readById(any());
        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(commentService).readById(1L);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void create() {
        doReturn(buildCommentResponse(1L, "comment1", 1L))
                .when(commentService).create(any());
        CommentDtoRequest request = buildCommentRequest("comment1");
        doReturn(Optional.of(Comment.builder().build())).when(newsRepository).readById(any());

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("comment1"))
                .body("news.id", is(1))
                .statusCode(201);

        verify(commentService).create(request);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void createWhenCommentDtoIsInvalid() {
        CommentDtoRequest request = buildCommentRequest("");
        doReturn(true).when(newsInfoValidator).isValid(any(), any());
        doReturn(Optional.of(Comment.builder().build())).when(newsRepository).readById(any());

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("content"))
                .statusCode(400);

        verifyNoMoreInteractions(commentService);
    }

    @Test
    void patch() {
        doReturn(Optional.of(buildCommentResponse(1L, "comment", 1L)))
                .when(commentService).readById(any());
        doReturn(buildCommentResponse(1L, "comment1", 1L)).when(commentService).patch(any(), any());

        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\": \"replace\", \"path\": \"/content\", \"value\": \"comment1\"}]")
                .when()
                .patch(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("comment1"))
                .body("news.id", is(1))
                .statusCode(200);

        verify(commentService).readById(1L);
        verify(commentService).patch(eq(1L), any(CommentDtoRequest.class));
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void patchWhenCommentNotFound() {
        doReturn(Optional.empty()).when(commentService).readById(any());

        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\": \"replace\", \"path\": \"/content\", \"value\": \"comment1\"}]")
                .when()
                .patch(BASE_PATH + "/{id}", 1L)
                .then()
                .assertThat()
                .statusCode(404);

        verify(commentService).readById(1L);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void update() {
        doReturn(Optional.of(buildCommentResponse(1L, "comment2", 1L)))
                .when(commentService).update(any(), any());
        CommentDtoRequest request = buildCommentRequest("comment2");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("comment2"))
                .body("news.id", is(1))
                .statusCode(200);

        verify(commentService).update(1L, request);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void updateWhenCommentDtoIsInvalid() {
        CommentDtoRequest request = buildCommentRequest("");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("content"))
                .statusCode(400);

        verifyNoMoreInteractions(commentService);
    }

    @Test
    void updateWhenCommentNotFound() {
        doReturn(Optional.empty()).when(commentService).update(any(), any());
        CommentDtoRequest request = buildCommentRequest("comment2");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(commentService).update(1L, request);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void delete() {
        doReturn(true).when(commentService).deleteById(any());

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(204);

        verify(commentService).deleteById(1L);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void deleteWhenCommentNotFound() {
        doReturn(false).when(commentService).deleteById(any());

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(204);

        verify(commentService).deleteById(1L);
        verifyNoMoreInteractions(commentService);
    }

    private CommentDtoRequest buildCommentRequest(String content) {
        return CommentDtoRequest.builder()
                .newsId(1L)
                .content(content)
                .build();
    }

    private CommentDtoResponse buildCommentResponse(Long id, String content, Long newsID) {
        return CommentDtoResponse.builder()
                .id(id)
                .news(NewsDtoResponse.builder().id(newsID).build())
                .content(content)
                .build();
    }
}