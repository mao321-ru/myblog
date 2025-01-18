package org.example.mbg.controller;

import org.example.mbg.configuration.WebConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringJUnitWebConfig( classes = { WebConfiguration.class})
@TestPropertySource( locations = "classpath:test-application.properties")
// расскомментировать для перезаливки тестовых данных
//@Sql( scripts = {"/clear-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
@Sql( scripts = {"/clear-temp-data.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
// очищаем временные данные ДО а не ПОСЛЕ для возможности просмотра в БД изменений последнего выполнявшегося теста
@Sql( scripts = {"/clear-temp-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD )
public class PostControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext).build();
    }

    @Test
    void findPosts_checkPostsCount() throws Exception {
        mockMvc.perform( get( "/"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // проверка числа постов, выводимых в <div class="post" ...>
                .andExpect( xpath( "//div[@class=\"post\"]").nodeCount( 2))
        ;
    }

    @Test
    void createPost_checkNewPostInTop() throws Exception {
        mockMvc.perform(
                    post( "/")
                            .param( "title", "newPost")
                            .param( "text", "Text of new post")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/"))
        ;

        // Проверяем страницу, выводимую по редиректу после создания поста
        // XPath для выбора верхнего поста
        final var TOP_POST_XPATH = "//div[@class=\"post\"][1]";
        mockMvc.perform( get( "/"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // проверка вывода данных нового поста
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__title\"]").string( "newPost"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__preview\"]").string( "Text of new post"))
        ;
    }
}