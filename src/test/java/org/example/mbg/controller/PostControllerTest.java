package org.example.mbg.controller;

import org.example.mbg.configuration.WebConfiguration;
import org.example.mbg.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig( classes = { WebConfiguration.class})
@TestPropertySource( locations = "classpath:test-application.properties")
// расскомментировать для пересоздания объектов схемы
//@Sql( scripts = {"/uninstall.sql", "/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
// расскомментировать для перезаливки тестовых данных
//@Sql( scripts = {"/clear-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
@Sql( scripts = {"/clear-temp-data.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
// очищаем временные данные ДО а не ПОСЛЕ для возможности просмотра в БД данных последнего выполнявшегося теста
@Sql( scripts = {"/clear-temp-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD )
public class PostControllerTest {

    // число постов в тестовых данных
    private final int TEST_POSTS_COUNT = 4;

    // выбор всех div с постами
    private final String POSTS_XPATH = "//div[@class=\"post\"]";

    // выбор верхнего поста
    private final String TOP_POST_XPATH = POSTS_XPATH + "[1]";

    // выбор элементов списка комментариев
    private final String POST_COMMENT_XPATH = "//ul[@class=\"comments__list\"]/li";

    // Путь к png-картинке для использования в тестах
    private final String TEST_PNG_IMAGE_PATH = "static/images/btn_close_popup.png";

    // post_id несуществуюего поста
    private final long NOT_EXITS_POST_ID = 999L;

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
    void findPosts_checkPostsCount() throws Exception {
        mockMvc.perform( get( "/"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // проверка числа постов, выводимых в <div class="post" ...>
                .andExpect( xpath( POSTS_XPATH).nodeCount( TEST_POSTS_COUNT))
        ;
    }

    @Test
    void findPosts_filterEmpty() throws Exception {
        mockMvc.perform(
                    // проверяем "/posts" дополнительно к "/" (используется в остальных тестах)
                    get( "/posts")
                        .param( "tags", "not_exists_tag")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // нет постов
                .andExpect( xpath( POSTS_XPATH).nodeCount( 0))
        ;
    }

    @Test
    void findPosts_filterOnePost() throws Exception {
        mockMvc.perform(
                    get( "/")
                    .param( "tags", "  river   nice nice  ")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // в тестовых данных должен быть найден один пост "Волга"
                .andExpect( xpath( POSTS_XPATH).nodeCount( 1))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__title\"]").string( "Волга"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__tags\"]").string( "nice river saratov"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__preview\"]").string( "Это прекрасная река"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__likes_count\"]").string( "5"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__comments_count\"]").string( "2"))
        ;
    }

    @Test
    void getPostImage_checkNotFound() throws Exception {
        mockMvc.perform( get( "/posts/{postId}/image", NOT_EXITS_POST_ID))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isNotFound())
        ;
    }

    @Test
    void showPost_check() throws Exception {
        mockMvc.perform( get( "/posts/{postId}", 3) )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // в тестовых данных должен быть найден один пост "Волга"
                .andExpect( xpath( POSTS_XPATH).nodeCount( 1))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__title\"]").string( "Волга"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__tags\"]").string( "nice river saratov"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__text\"]").string( "Это прекрасная река"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__likes_count\"]").string( "5"))
                .andExpect( xpath(  POST_COMMENT_XPATH).nodeCount( 2))
                .andExpect( xpath(  POST_COMMENT_XPATH + "[1]/*[@class=\"comment_text\"]/@value").string( "Красивая река!"))
                .andExpect( xpath(  POST_COMMENT_XPATH + "[2]/*[@class=\"comment_text\"]/@value").string( "И широкая!"))
        ;
    }

    @Test
    void createPost_checkNewPostInTop() throws Exception {
        mockMvc.perform(
                    post( "/posts")
                            .param( "title", "newPost")
                            .param( "tags", "  tagFirst tag2   tagLast  tag2 ")
                            .param( "text", "Text of new post")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/"))
        ;

        // Проверяем страницу, выводимую по редиректу после создания поста
        mockMvc.perform( get( "/"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // проверка вывода данных нового поста
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__title\"]").string( "newPost"))
                // для тегов проверяем нормализацию пробелов и сохранение исходного порядка
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__tags\"]").string( "tagFirst tag2 tagLast"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__preview\"]").string( "Text of new post"))
        ;
    }

    @Test
    void createPost_checkSaveImage() throws Exception {
        byte[] fileData = getClass().getClassLoader().getResourceAsStream(TEST_PNG_IMAGE_PATH).readAllBytes();
        var file = new MockMultipartFile(
            "file",
            "createPost_checkSaveImage_img.png",
            "image/png",
            fileData
        );
        mockMvc.perform( multipart( "/posts")
                    .file( file)
                    .param( "title", "createPost_checkSaveImage")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/"))
        ;

        // Проверяем возврат добавленного изображения
        checkPostImage( START_TEMP_POST_ID, fileData);
    }

    @Test
    void updatePost_checkChangeAll() throws Exception {
        // создаем новый пост
        mockMvc.perform( multipart( "/posts")
                        .file(
                            new MockMultipartFile(
                                "file",
                                "updatePost_checkChangeAll_before.png",
                                "image/png",
                                "before_image_data".getBytes( StandardCharsets.UTF_8)
                            )
                        )
                        .param( "title", "updatePost_checkChangeAll")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/"))
        ;

        byte[] fileData = getClass().getClassLoader().getResourceAsStream(TEST_PNG_IMAGE_PATH).readAllBytes();

        Post p = Post.builder()
                .postId( START_TEMP_POST_ID)
                .title( "updatePost_checkChangeAll")
                .tags( "updatePost_checkChangeAll_tags")
                .text( "updatePost_checkChangeAll text")
                .build();

        // обновляем пост
        mockMvc.perform( multipart( "/posts/{postId}", START_TEMP_POST_ID)
                    .file(
                        new MockMultipartFile(
                            "file",
                            "updatePost_checkChangeAll_after.png",
                            "image/png",
                            fileData
                        )
                    )
                    .param( "title", p.getTitle())
                    .param( "tags", p.getTags())
                    .param( "text", p.getText())
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/posts/" + START_TEMP_POST_ID))
        ;

        // Проверяем основные данные
        mockMvc.perform( get( "/posts/{postId}", p.getPostId()) )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__title\"]").string( p.getTitle()))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__tags\"]").string( p.getTags()))
                .andExpect( xpath( TOP_POST_XPATH + "//*[@class=\"post__text\"]").string( p.getText()))
        ;

        // Проверяем возврат обновленного изображения
        checkPostImage( START_TEMP_POST_ID, fileData);
    }

    @Test
    void updatePost_checkDeleteImage() throws Exception {
        Post p = Post.builder()
                .postId( START_TEMP_POST_ID)
                .title( "updatePost_checkDeleteImage")
                .build();

        // создаем новый пост
        mockMvc.perform( multipart( "/posts")
                        .file(
                                new MockMultipartFile(
                                        "file",
                                        "updatePost_checkDeleteImage.png",
                                        "image/png",
                                        "image_data".getBytes( StandardCharsets.UTF_8)
                                )
                        )
                        .param( "title", p.getTitle())
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/"))
        ;

        // обновляем пост
        mockMvc.perform( multipart( "/posts/{postId}", p.getPostId())
                        .param( "title", p.getTitle())
                        .param( "tags", p.getTags())
                        .param( "text", p.getText())
                        .param( "delImage", "on")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/posts/" + p.getPostId()))
        ;

        // проверяем что изображение удалено
        mockMvc.perform( get( "/posts/{postId}/image", p.getPostId()))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isNotFound())
        ;
    }

    @Test
    void addLike_check() throws Exception {
        // создаем новый пост для теста
        final long postId = START_TEMP_POST_ID;
        mockMvc.perform( multipart( "/posts")
                        .param( "title", "addLike_check")
                )
                //.andDo( print()) // вывод запроса и ответа
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

    private void checkPostImage(long postId, byte[] fileData) throws Exception {
        mockMvc.perform( get( "/posts/{postId}/image", postId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "image/png"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Length", String.valueOf( fileData.length)))
                .andExpect( content().bytes( fileData))
        ;
    }

}