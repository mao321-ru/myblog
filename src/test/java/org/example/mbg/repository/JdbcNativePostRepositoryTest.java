package org.example.mbg.repository;

import org.checkerframework.checker.units.qual.C;
import org.example.mbg.configuration.DataSourceConfiguration;
import org.example.mbg.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.AssertionErrors;

import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringJUnitConfig( classes = { DataSourceConfiguration.class, JdbcNativePostRepository.class})
@TestPropertySource( locations = "classpath:test-application.properties")
// расскомментировать для перезаливки тестовых данных
//@Sql( scripts = {"/clear-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
@Sql( scripts = {"/clear-temp-data.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS )
// очищаем временные данные ДО а не ПОСЛЕ для возможности просмотра в БД данных последнего выполнявшегося теста
@Sql( scripts = {"/clear-temp-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD )
public class JdbcNativePostRepositoryTest {

    // число постов в тестовых данных
    private final int TEST_POSTS_COUNT = 4;

    @Autowired
    private PostRepository repo;

    @Autowired
    private JdbcTemplate jt;

    @Test
    void findAll_check() {
        var foundPosts = repo.findAll();
        assertEquals( "Incorrect number of all founded posts", foundPosts.size(), TEST_POSTS_COUNT);
        // заголовок проверямого тестового поста
        var CHECK_POST_TITLE = "Волга";
        Optional<Post> po = foundPosts.stream()
                .filter( p -> p.getTitle().equals( CHECK_POST_TITLE))
                .findFirst();
        assertTrue( "Not found post with title: '" + CHECK_POST_TITLE + "'", po.isPresent());
        if( po.isPresent()) {
            Post fp = po.get();
            assertEquals( "Incorrect: likesCount", 5, fp.getLikesCount());
            assertEquals( "Incorrect: commentsCount", 2, fp.getCommentsCount());
        }
    }

    @Test
    void findByTags_check() {
        Post post = Post.builder()
                .title( "findByTags_check: Title")
                .tags( "findByTags_check_tag")
                .build();
        repo.createPost( post);
        var foundPosts = repo.findByTags( post.getTags());
        assertEquals( "Incorrect number of posts found", foundPosts.size(), 1);
        if( foundPosts.size() == 1) {
            Post fp = foundPosts.getFirst();
            assertEquals( "Incorrect title", post.getTitle(), fp.getTitle());
        }
    }
}
