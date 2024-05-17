import org.example.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;


public class RunTest {
    @BeforeAll
    public static void beforeAll() {
        System.out.println("Before all tests");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("After all tests");
    }

    @Test
    public void test1() throws InterruptedException {
        System.out.println("Test 1");
//        Main.main(new String[] {});
        Main.main(new String[] {});
        System.out.println("Test 1 done");
    }

}