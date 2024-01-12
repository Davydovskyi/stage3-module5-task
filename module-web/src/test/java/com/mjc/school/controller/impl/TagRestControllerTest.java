package com.mjc.school.controller.impl;

import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.tag.TagDtoRequest;
import com.mjc.school.service.dto.tag.TagDtoResponse;
import io.restassured.http.ContentType;
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

@WebMvcTest(TagRestController.class)
class TagRestControllerTest {
    private static final String BASE_PATH = "/api/v1/tags";
    @MockBean
    private TagService tagService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void readAll() {
        doReturn(List.of(buildTagResponse(1L, "tag1"),
                buildTagResponse(2L, "tag2")))
                .when(tagService).readAll(any());
        Pageable pageable = PageRequest.of(0, 10);

        given()
                .param("page", pageable.getPageNumber())
                .param("size", pageable.getPageSize())
                .when()
                .get(BASE_PATH)
                .then()
                .assertThat()
                .body("size()", is(2))
                .body("get(0).id", is(1))
                .body("get(1).id", is(2))
                .body("get(0).name", is("tag1"))
                .body("get(1).name", is("tag2"))
                .statusCode(200);

        verify(tagService).readAll(pageable);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void readAllWhenTagsNotFound() {
        doReturn(List.of()).when(tagService).readAll(any());
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

        verify(tagService).readAll(pageable);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void readById() {
        doReturn(Optional.of(buildTagResponse(1L, "tag1")))
                .when(tagService).readById(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("tag1"))
                .statusCode(200);

        verify(tagService).readById(1L);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void readByIdWhenTagNotFound() {
        doReturn(Optional.empty()).when(tagService).readById(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(tagService).readById(1L);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void create() {
        doReturn(buildTagResponse(1L, "tag1")).when(tagService).create(any());
        TagDtoRequest request = buildTagRequest("tag1");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("tag1"))
                .statusCode(201);

        verify(tagService).create(buildTagRequest("tag1"));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void createWhenTagDtoIsInvalid() {
        TagDtoRequest request = buildTagRequest("");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("name"))
                .statusCode(400);

        verifyNoMoreInteractions(tagService);
    }

    @Test
    void update() {
        doReturn(Optional.of(buildTagResponse(1L, "tag1"))).when(tagService).update(any(), any());
        TagDtoRequest request = buildTagRequest("tag1");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("tag1"))
                .statusCode(200);

        verify(tagService).update(1L, request);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void updateWhenTagDtoIsInvalid() {
        TagDtoRequest request = buildTagRequest("");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("name"))
                .statusCode(400);

        verifyNoMoreInteractions(tagService);
    }

    @Test
    void updateWhenTagNotFound() {
        doReturn(Optional.empty()).when(tagService).update(any(), any());

        given()
                .contentType(ContentType.JSON)
                .body(buildTagRequest("tag1"))
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(tagService).update(1L, buildTagRequest("tag1"));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void patch() {
        doReturn(Optional.of(buildTagResponse(1L, "tag"))).when(tagService).readById(any());
        doReturn(buildTagResponse(1L, "tag1")).when(tagService).patch(any(), any());

        given()
                .contentType("application/json-patch+json")
                .body("[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"tag1\"}]")
                .when()
                .patch(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("tag1"))
                .statusCode(200);

        verify(tagService).readById(1L);
        verify(tagService).patch(eq(1L), any(TagDtoRequest.class));
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void patchWhenTagNotFound() {
        doReturn(Optional.empty()).when(tagService).readById(any());

        given()
                .contentType("application/json-patch+json")
                .body("[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"tag1\"}]")
                .when()
                .patch(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(tagService).readById(1L);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void delete() {
        doReturn(true).when(tagService).deleteById(any());

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(204);

        verify(tagService).deleteById(1L);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void deleteWhenTagNotFound() {
        doReturn(false).when(tagService).deleteById(any());

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(204);

        verify(tagService).deleteById(1L);
        verifyNoMoreInteractions(tagService);
    }

    private TagDtoResponse buildTagResponse(Long id, String name) {
        return TagDtoResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

    private TagDtoRequest buildTagRequest(String name) {
        return TagDtoRequest.builder()
                .name(name)
                .build();
    }
}