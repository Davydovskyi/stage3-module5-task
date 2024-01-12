package com.mjc.school.controller.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.dto.news.NewsQueryParams;
import com.mjc.school.service.dto.tag.TagDtoResponse;
import com.mjc.school.service.validator.AuthorInfoValidator;
import com.mjc.school.service.validator.TagsInfoValidator;
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

@WebMvcTest(NewsRestController.class)
class NewsRestControllerTest {
    private static final String BASE_PATH = "/api/v1/news";
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private NewsService newsService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private TagService tagService;
    @MockBean
    private CommentService commentService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthorInfoValidator authorInfoValidator;
    @MockBean
    private TagsInfoValidator tagsInfoValidator;
    @MockBean
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void readAll() {
        doReturn(List.of(buildNewsResponse(1L, "title1", "content1"),
                buildNewsResponse(2L, "title2", "content2")))
                .when(newsService).readAll(any());
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
                .body("[0].content", is("content1"))
                .body("[0].title", is("title1"))
                .body("[1].id", is(2))
                .body("[1].content", is("content2"))
                .body("[1].title", is("title2"))
                .statusCode(200);

        verify(newsService).readAll(pageable);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void readAllWhenNewsNotFound() {
        doReturn(List.of()).when(newsService).readAll(any());
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

        verify(newsService).readAll(pageable);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void readById() {
        doReturn(Optional.of(buildNewsResponse(1L, "title1", "content1")))
                .when(newsService).readById(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("content1"))
                .body("title", is("title1"))
                .statusCode(200);

        verify(newsService).readById(1L);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void readByIdWhenNewsNotFound() {
        doReturn(Optional.empty()).when(newsService).readById(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(newsService).readById(1L);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void create() {
        doReturn(buildNewsResponse(1L, "title1", "content1"))
                .when(newsService).create(any());
        NewsDtoRequest request = buildNewsRequest();
        doReturn(Optional.of(Author.builder().build())).when(authorRepository).readById(any());
        doReturn(List.of(Tag.builder().id(1L).build())).when(tagRepository).readAllByIds(any());

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("content1"))
                .body("title", is("title1"))
                .statusCode(201);

        verify(newsService).create(request);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void createWhenNewsDtoIsInvalid() {
        NewsDtoRequest request = buildNewsRequest();

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(BASE_PATH)
                .then()
                .assertThat()
                .body("errors.size()", is(2))
                .body(containsString("authorId"))
                .body(containsString("tagIds"))
                .statusCode(400);

        verifyNoInteractions(newsService);
    }

    @Test
    void update() {
        doReturn(Optional.of(buildNewsResponse(1L, "title1", "content1")))
                .when(newsService).update(any(), any());
        NewsDtoRequest request = buildNewsRequest();
        doReturn(Optional.of(Author.builder().build())).when(authorRepository).readById(any());

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("content1"))
                .body("title", is("title1"))
                .statusCode(200);

        verify(newsService).update(1L, request);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void updateWhenNewsDtoIsInvalid() {
        NewsDtoRequest request = buildNewsRequest();

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("errors.size()", is(1))
                .body(containsString("authorId"))
                .statusCode(400);

        verifyNoMoreInteractions(newsService);
    }

    @Test
    void updateWhenNewsNotFound() {
        doReturn(Optional.empty()).when(newsService).update(any(), any());
        doReturn(Optional.of(Author.builder().build())).when(authorRepository).readById(any());
        NewsDtoRequest request = buildNewsRequest();

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(newsService).update(1L, request);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void patch() {
        doReturn(Optional.of(buildNewsResponse(1L, "title", "content1")))
                .when(newsService).readById(any());
        doReturn(buildNewsResponse(1L, "title1", "content1")).when(newsService).patch(any(), any());

        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\": \"replace\", \"path\": \"/title\", \"value\": \"title1\"}])")
                .when()
                .patch(BASE_PATH + "/{id}", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("content", is("content1"))
                .body("title", is("title1"))
                .statusCode(200);

        verify(newsService).readById(1L);
        verify(newsService).patch(eq(1L), any(NewsDtoRequest.class));
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void patchWhenNewsNotFound() {
        doReturn(Optional.empty()).when(newsService).readById(any());

        given()
                .contentType("application/json-patch+json")
                .body("[{\"op\": \"replace\", \"path\": \"/title\", \"value\": \"title1\"}])")
                .when()
                .patch(BASE_PATH + "/{id}", 1L)
                .then()
                .assertThat()
                .statusCode(404);

        verify(newsService).readById(1L);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void delete() {
        doReturn(true).when(newsService).deleteById(any());

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1L)
                .then()
                .assertThat()
                .statusCode(204);

        verify(newsService).deleteById(1L);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void deleteWhenNewsNotFound() {
        doReturn(false).when(newsService).deleteById(any());

        given()
                .when()
                .delete(BASE_PATH + "/{id}", 1L)
                .then()
                .assertThat()
                .statusCode(204);

        verify(newsService).deleteById(1L);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void readAllByFilter() {
        doReturn(List.of(buildNewsResponse(1L, "title1", "content1"))).when(newsService).readAllByFilter(any(), any());
        Pageable pageable = PageRequest.of(0, 10);
        NewsQueryParams filter = NewsQueryParams.builder().title("title1").build();

        given()
                .queryParam("title", filter.title())
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .when()
                .get(BASE_PATH + "/filter")
                .then()
                .assertThat()
                .body("size()", is(1))
                .body("get(0).id", is(1))
                .body("get(0).title", is("title1"))
                .body("get(0).content", is("content1"))
                .statusCode(200);

        verify(newsService).readAllByFilter(filter, pageable);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void readAllByFilterWhenNewsNotFound() {
        doReturn(List.of()).when(newsService).readAllByFilter(any(), any());
        Pageable pageable = PageRequest.of(0, 10);
        NewsQueryParams filter = NewsQueryParams.builder().title("title1").build();

        given()
                .queryParam("title", filter.title())
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .when()
                .get(BASE_PATH + "/filter")
                .then()
                .assertThat()
                .body("size()", is(0))
                .statusCode(200);

        verify(newsService).readAllByFilter(filter, pageable);
        verifyNoMoreInteractions(newsService);
    }

    @Test
    void readAuthorByNewsId() {
        doReturn(Optional.of(buildAuthorResponse())).when(authorService).readByNewsId(1L);

        given()
                .when()
                .get(BASE_PATH + "/{id}/author", 1)
                .then()
                .assertThat()
                .body("id", is(1))
                .body("name", is("author1"))
                .statusCode(200);

        verify(authorService).readByNewsId(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void readAuthorByNewsIdWhenAuthorNotFound() {
        doReturn(Optional.empty()).when(authorService).readByNewsId(1L);

        given()
                .when()
                .get(BASE_PATH + "/{id}/author", 1)
                .then()
                .assertThat()
                .statusCode(404);

        verify(authorService).readByNewsId(1L);
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void readAllTagsByNewsId() {
        doReturn(List.of(buildTagResponse(1L, "tag1"),
                buildTagResponse(2L, "tag2")))
                .when(tagService).readAllByNewsId(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}/tags", 1)
                .then()
                .assertThat()
                .body("size()", is(2))
                .body("get(0).id", is(1))
                .body("get(1).id", is(2))
                .body("get(0).name", is("tag1"))
                .body("get(1).name", is("tag2"))
                .statusCode(200);

        verify(tagService).readAllByNewsId(1L);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void readAllTagsByNewsIdWhenTagsNotFound() {
        doReturn(List.of()).when(tagService).readAllByNewsId(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}/tags", 1)
                .then()
                .assertThat()
                .body("size()", is(0))
                .statusCode(200);

        verify(tagService).readAllByNewsId(1L);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    void readAllCommentsByNewsId() {
        doReturn(List.of(buildCommentResponse()))
                .when(commentService).readAllByNewsId(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}/comments", 1)
                .then()
                .assertThat()
                .body("size()", is(1))
                .body("[0].id", is(1))
                .body("[0].content", is("comment1"))
                .body("[0].news.id", is(1))
                .statusCode(200);

        verify(commentService).readAllByNewsId(1L);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    void readAllCommentsByNewsIdWhenNewsNotFound() {
        doReturn(List.of()).when(commentService).readAllByNewsId(any());

        given()
                .when()
                .get(BASE_PATH + "/{id}/comments", 1)
                .then()
                .assertThat()
                .body("size()", is(0))
                .statusCode(200);

        verify(commentService).readAllByNewsId(1L);
        verifyNoMoreInteractions(commentService);
    }

    private NewsDtoResponse buildNewsResponse(Long id, String title, String content) {
        return NewsDtoResponse.builder()
                .id(id)
                .content(content)
                .title(title)
                .author(AuthorDtoResponse.builder().id(1L).build())
                .build();
    }

    private NewsDtoRequest buildNewsRequest() {
        return NewsDtoRequest.builder()
                .title("title1")
                .content("content1")
                .authorId(1L)
                .tagIds(List.of(1L))
                .build();
    }

    private AuthorDtoResponse buildAuthorResponse() {
        return AuthorDtoResponse.builder()
                .id(1L)
                .name("author1")
                .build();
    }

    private TagDtoResponse buildTagResponse(Long id, String name) {
        return TagDtoResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

    private CommentDtoResponse buildCommentResponse() {
        return CommentDtoResponse.builder()
                .id(1L)
                .news(NewsDtoResponse.builder().id(1L).build())
                .content("comment1")
                .build();
    }
}