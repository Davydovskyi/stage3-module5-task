package com.mjc.school.controller.impl;

import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.author.AuthorDtoRequest;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
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

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {
    private static final String BASE_PATH = "/api/v1/authors";
    @MockBean
    private AuthorService authorService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void readAll() {
        doReturn(List.of(buildAuthorResponse(1L, "author1"), buildAuthorResponse(2L, "author2")))
                .when(authorService).readAll(any());
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
                .body("[0].name", is("author1"))
                .body("[1].id", is(2))
                .body("[1].name", is("author2"))
                .statusCode(200);

        verify(authorService).readAll(pageable);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void readAllWhenAuthorNotFound() {
        doReturn(List.of()).when(authorService).readAll(any());
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

        verify(authorService).readAll(pageable);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void readById() {
        doReturn(Optional.of(buildAuthorResponse(1L, "author1"))).when(authorService).readById(1L);

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("author1"))
                .statusCode(200);

        verify(authorService).readById(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void readByIdWhenAuthorNotFound() {
        doReturn(Optional.empty()).when(authorService).readById(1L);

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(authorService).readById(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void create() {
        doReturn(buildAuthorResponse(1L, "author1")).when(authorService).create(any());
        AuthorDtoRequest request = buildAuthorRequest("author1");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("author1"))
                .statusCode(201);

        verify(authorService).create(request);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void createWhenAuthorDtoIsInvalid() {
        AuthorDtoRequest request = buildAuthorRequest("as");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("name"))
                .statusCode(400);

        verifyNoMoreInteractions(authorService);
    }

    @Test
    void patch() {
        doReturn(Optional.of(buildAuthorResponse(1L, "author1"))).when(authorService).readById(1L);
        doReturn(buildAuthorResponse(1L, "author2")).when(authorService).patch(any(), any());

        given()
                .contentType("application/json-patch+json")
                .body("[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"author2\"}]")
                .when()
                .patch(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("author2"))
                .statusCode(200);

        verify(authorService).readById(1L);
        verify(authorService).patch(eq(1L), any(AuthorDtoRequest.class));
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void patchWhenAuthorNotFound() {
        doReturn(Optional.empty()).when(authorService).readById(1L);

        given()
                .contentType("application/json-patch+json")
                .body("[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"author2\"}]")
                .when()
                .patch(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(authorService).readById(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void update() {
        doReturn(Optional.of(buildAuthorResponse(1L, "author2"))).when(authorService).update(any(), any());
        AuthorDtoRequest request = buildAuthorRequest("author2");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("author2"))
                .statusCode(200);

        verify(authorService).update(1L, request);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void updateWhenAuthorNotFound() {
        doReturn(Optional.empty()).when(authorService).update(any(), any());
        AuthorDtoRequest request = buildAuthorRequest("author2");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(authorService).update(1L, request);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void updateWhenAuthorDtoIsInvalid() {
        AuthorDtoRequest request = buildAuthorRequest("as");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("name"))
                .statusCode(400);

        verifyNoMoreInteractions(authorService);
    }

    @Test
    void deleteById() {
        doReturn(true).when(authorService).deleteById(1L);

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(204);

        verify(authorService).deleteById(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void deleteByIdWhenAuthorNotFound() {
        doReturn(false).when(authorService).deleteById(1L);

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(204);

        verify(authorService).deleteById(1L);
        verifyNoMoreInteractions(authorService);
    }

    private AuthorDtoResponse buildAuthorResponse(Long id, String name) {
        return AuthorDtoResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

    private AuthorDtoRequest buildAuthorRequest(String name) {
        return AuthorDtoRequest.builder()
                .name(name)
                .build();
    }
}