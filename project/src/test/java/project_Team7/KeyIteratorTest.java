package project_Team7;


import com.intellij.testFramework.fixtures.BasePlatformTestCase;

// for widnow
// import org.testng.annotations.Test;

// for linux
import org.junit.jupiter.api.Test;



public class KeyIteratorTest extends BasePlatformTestCase {

    @Test
    public void testInitiation() {
        KeyIterator it = new KeyIterator();
        assertEquals("N", it.next());
    }

    @Test
    public void testShortIteration() {
        KeyIterator it = new KeyIterator();
        for(int i=0;i < 5; i++)
        {
            it.next();
        }
        assertEquals("S", it.next());
    }

    @Test
    public void testLongIteration() {
        KeyIterator it = new KeyIterator();
        for(int i=0;i < 1000; i++)
        {
            it.next();
        }
        assertEquals("EKZ", it.next());
    }

    @Test
    public void testHasNext() {
        KeyIterator it = new KeyIterator();
        for(int i=0;i < 1000; i++)
        {
            it.next();
            assertTrue(it.hasNext()) ;
        }
    }

}