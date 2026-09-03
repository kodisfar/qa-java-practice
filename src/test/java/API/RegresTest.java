package API;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API Testing")
@Feature("JSONPlaceholder API")
public class RegresTest {

    private static final String URL = "https://jsonplaceholder.typicode.com";

    @Test
    @Story("Get users")
    @Description("Перевірка отримання списку користувачів та коректності їх основних даних")
    @Severity(SeverityLevel.CRITICAL)
    public void getUsersAndCheckData() {

        Specifications.installSpecification(
                Specifications.requestSpec(URL),
                Specifications.responseSpecOK200()
        );

        List<UserInfo> users = given()
                .when()
                .get("/users")
                .then()
                .log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("", UserInfo.class);

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
    @Story("Create post")
    @Description("Перевірка успішного створення нового поста та відповідності переданих даних")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewPost() {

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
    @Story("Get post")
    @Description("Перевірка отримання поста за його ідентифікатором та наявності основних даних")
    @Severity(SeverityLevel.NORMAL)
    public void getPostById() {

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
    @Story("Validate post sorting")
    @Description("Перевірка того, що ідентифікатори отриманих постів знаходяться у відсортованому порядку")
    @Severity(SeverityLevel.NORMAL)
    public void verifyPostIdsOrder() {

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
    @Story("Delete post")
    @Description("Перевірка виконання DELETE-запиту для видалення поста")
    @Severity(SeverityLevel.NORMAL)
    public void deletePostById() {

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