import org.example.HttpClientExample;
import org.junit.jupiter.api.Test;
//mvn clean test -DmyIpAddress=myIpAddress -DtargetIpAddress=targetIpAddress
public class RunTest {

    @Test
    public void test1() {
        // Access command line arguments
        String myIpAddress = System.getProperty("myIpAddress");
        String targetIpAddress = System.getProperty("targetIpAddress");

        // Pass command line arguments to the main method
        HttpClientExample.main(new String[] {myIpAddress, targetIpAddress});
    }
}
