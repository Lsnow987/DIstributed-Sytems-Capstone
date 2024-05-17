import edu.yu.capstone.impl.VideoTranscoderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


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
    public void test1() {
        System.out.println("Test 1");
        VideoTranscoderImpl.main(new String[] {});
        System.out.println("Test 1 done");
    }

}