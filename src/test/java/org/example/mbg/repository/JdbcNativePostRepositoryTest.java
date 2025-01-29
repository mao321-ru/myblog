package org.example.mbg.repository;

import org.example.mbg.configuration.TestDataSourceConfiguration;
import org.example.mbg.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith( SpringExtension.class)
@ContextHierarchy({
        @ContextConfiguration(name = "data", classes = { TestDataSourceConfiguration.class}),
        @ContextConfiguration(name = "repo", classes = { JdbcNativePostRepository.class})
})
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
        var foundPosts = repo.findAll( PageRequest.of( 0, 1000)).toList();
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
    void findAll_checkPages() {
        var pg = repo.findAll( PageRequest.of( 0, TEST_POSTS_COUNT - 1));
        assertEquals( "First page: incorrect page number", 0,  pg.getNumber());
        assertEquals( "First page: incorrect page size", TEST_POSTS_COUNT - 1, pg.getSize());
        assertEquals( "First page: incorrect hasNext", true, pg.hasNext());
        assertEquals( "First page: incorrect List size", TEST_POSTS_COUNT - 1, pg.toList().size());

        pg = repo.findAll( PageRequest.of( 1, TEST_POSTS_COUNT - 1));
        assertEquals( "Last page: incorrect page number", 1,  pg.getNumber());
        assertEquals( "Last page: incorrect page size", TEST_POSTS_COUNT - 1, pg.getSize());
        assertEquals( "Last page: incorrect hasNext", false, pg.hasNext());
        assertEquals( "Last page: incorrect List size", 1, pg.toList().size());
    }

    @Test
    void findByTags_check() {
        Post post = Post.builder()
                .title( "findByTags_check: Title")
                .tags( "findByTags_check_tag")
                .build();
        repo.createPost( post);
        var foundPosts = repo.findByTags( post.getTags(), PageRequest.of( 0, 1000)).toList();
        assertEquals( "Incorrect number of posts found", foundPosts.size(), 1);
        if( foundPosts.size() == 1) {
            Post fp = foundPosts.getFirst();
            assertEquals( "Incorrect title", post.getTitle(), fp.getTitle());
        }
    }
}
