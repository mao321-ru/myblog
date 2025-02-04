package org.example.myblog.controller;

import org.example.myblog.configuration.TestDataSourceConfiguration;
import org.example.myblog.configuration.WebConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith( SpringExtension.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(name = "data", classes = { TestDataSourceConfiguration.class}),
        @ContextConfiguration(name = "web", classes = { WebConfiguration.class })
})
// расскомментировать для пересоздания объектов схемы
//@Sql( scripts = {"/uninstall.sql", "/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
// расскомментировать для перезаливки тестовых данных
//@Sql( scripts = {"/clear-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
@Sql( scripts = {"/clear-temp-data.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
// очищаем временные данные ДО а не ПОСЛЕ для возможности просмотра в БД данных последнего выполнявшегося теста
@Sql( scripts = {"/clear-temp-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD )
public class CommentControllerTest {

    // выбор элементов списка комментариев
    private final String POST_COMMENT_XPATH = "(//li[@class=\"comment\"])";

    // post_id первого поста, созданного при выполнении теста
    private final long START_TEMP_POST_ID = 1001L;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext).build();
    }

    @Test
    void createComment_check() throws Exception {
        // создаем новый пост для теста
        final long postId = createTempPost( "createComment_check");
        final String commentText = "Новый комментарий";

        // добавляем комментарий и проверяем результат
        mockMvc.perform( post( "/posts/{postId}/comments", postId)
                        .param( "commentText", commentText)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/posts/" + postId))
        ;
        mockMvc.perform( get( "/posts/{postId}", postId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath(  POST_COMMENT_XPATH + "[1]/input/@value").string( commentText))
        ;
    }

    @Test
    void updateComment_check() throws Exception {
        // создаем новый пост с комментарием для теста
        final long postId = createTempPost( "updateComment_check");
        final long commentId = createComment( postId, "Старый комментарий");
        final String commentText = "Новый комментарий";

        // меняем комментарий и проверяем результат
        mockMvc.perform( post( "/posts/{postId}/comments/{commentId}", postId, commentId)
                    .param( "_method", "")
                    .param( "commentText", commentText)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/posts/" + postId))
        ;
        mockMvc.perform( get( "/posts/{postId}", postId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath(  POST_COMMENT_XPATH + "[1]/input/@value").string( commentText))
        ;
    }

    @Test
    void deleteComment_check() throws Exception {
        // создаем новый пост с комментарием для теста
        final long postId = createTempPost( "deleteComment_check");
        final long commentId = createComment( postId, "Ненужный комментарий");

        // меняем комментарий и проверяем результат
        mockMvc.perform( post( "/posts/{postId}/comments/{commentId}", postId, commentId)
                        .param( "_method", "delete")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/posts/" + postId))
        ;
        mockMvc.perform( get( "/posts/{postId}", postId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath( POST_COMMENT_XPATH).nodeCount( 0))
        ;
    }

    private long createTempPost( String title) throws Exception {
        mockMvc.perform( multipart( "/posts")
                    .param( "title", title)
                )
                .andExpect( status().isFound())
        ;
        return START_TEMP_POST_ID;
    }

    private long createComment(long postId, String commentText) throws Exception {
        mockMvc.perform( post( "/posts/{postId}/comments", postId)
                        .param( "commentText", commentText)
                )
                .andExpect( status().isFound())
        ;
        return START_TEMP_POST_ID;
    }
}
