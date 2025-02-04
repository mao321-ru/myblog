package org.example.mbg.controller;

import org.example.mbg.configuration.TestDataSourceConfiguration;
import org.example.mbg.configuration.WebConfiguration;
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
public class LikesControllerTest {

    // выбор всех div с постами
    private final String POSTS_XPATH = "//div[@class=\"post\"]";

    // выбор верхнего поста
    private final String TOP_POST_XPATH = "(" + POSTS_XPATH + ")[1]";

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
    void addLike_check() throws Exception {
        // создаем новый пост для теста
        final long postId = START_TEMP_POST_ID;
        mockMvc.perform( multipart( "/posts")
                    .param( "title", "addLike_check")
                )
                .andExpect( status().isFound())
        ;

        // добавляем лайк и проверяем результат
        mockMvc.perform( post( "/posts/{postId}/add-like", postId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/posts/" + postId))
        ;
        mockMvc.perform( get( "/posts/{postId}", postId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__likes_count\"]").string( "1"))
        ;
    }

}