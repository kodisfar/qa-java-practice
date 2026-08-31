package API;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class RegresTest {

    private static final String URL = "https://jsonplaceholder.typicode.com";

    @Test
    public void checkUsersTest() {

        Specifications.installSpecification(
                Specifications.requestSpec(URL),
                Specifications.responseSpecOK200()
        );

        List<UserData> users = given()
                .when()
                .get("/users")
                .then()
                .log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("", UserData.class);

        assertFalse(users.isEmpty());

        users.forEach(user -> {
            assertNotNull(user.getId());
            assertNotNull(user.getName());
            assertNotNull(user.getUsername());
            assertNotNull(user.getEmail());
        });

        assertTrue(
                users.stream()
                        .allMatch(user -> user.getEmail().contains("@"))
        );
    }

    @Test
    public void createPostTest() {

        Specifications.installSpecification(
                Specifications.requestSpec(URL),
                Specifications.responseSpecCreated201()
        );

        Post post = new Post(
                1,
                "API Testing",
                "This is a new test post"
        );

        Post responsePost = given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .post("/posts")
                .then()
                .log().all()
                .extract()
                .as(Post.class);

        assertNotNull(responsePost.getId());
        assertEquals(post.getUserId(), responsePost.getUserId());
        assertEquals(post.getTitle(), responsePost.getTitle());
        assertEquals(post.getBody(), responsePost.getBody());
    }

    @Test
    public void getPostTest() {

        Specifications.installSpecification(
                Specifications.requestSpec(URL),
                Specifications.responseSpecOK200()
        );

        Post post = given()
                .when()
                .get("/posts/5")
                .then()
                .log().all()
                .extract()
                .as(Post.class);

        assertEquals(5, post.getId());
        assertNotNull(post.getTitle());
        assertNotNull(post.getBody());
        assertNotNull(post.getUserId());
    }

    @Test
    public void sortedPostIdsTest() {

        Specifications.installSpecification(
                Specifications.requestSpec(URL),
                Specifications.responseSpecOK200()
        );

        List<Post> posts = given()
                .when()
                .get("/posts")
                .then()
                .log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("", Post.class);

        List<Integer> ids = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        List<Integer> sortedIds = ids.stream()
                .sorted()
                .collect(Collectors.toList());

        assertEquals(sortedIds, ids);

        System.out.println("IDs: " + ids);
        System.out.println("Sorted IDs: " + sortedIds);
    }

    @Test
    public void deletePostTest() {

        Specifications.installSpecification(
                Specifications.requestSpec(URL),
                Specifications.responseSpecOK200()
        );

        given()
                .when()
                .delete("/posts/5")
                .then()
                .log().all();
    }
}
