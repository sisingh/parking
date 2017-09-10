package com.gojek.parking;

import java.io.FileNotFoundException;
import org.junit.*;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author siddharthasingh
 */
public class ParkingManagerTest {

    private String[] args = null;

    public ParkingManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ParkingManager.
     */
    @Test
    public void testMainWithOutFile() {
        System.out.println("test case with null ");
        try {
            ParkingManager.main(args);
            Assert.fail("It must have thrown FileNotFoundException after passing a null argument");
        } catch (FileNotFoundException ex) {
        } catch (RuntimeException ex) {
            Assert.fail("It must have thrown FileNotFoundException after passing a null argument");
        }
    }

    /**
     * Test of main method, of class ParkingManager.
     */
    @Test
    public void testMainWithFile() throws Exception {
        System.out.println("test case with actual file");
        args = new String[1];
        args[0] = "/Users/siddharthasingh/hike/ex/Infile.txt";
        try {
            ParkingManager.main(args);
        } catch (FileNotFoundException ex) {
            Assert.fail("Shouldn't have gotten here..." + ex.getMessage());
        } catch (RuntimeException ex) {
            Assert.fail(ex.getMessage());
        }
        ParkingManager pm = new ParkingManager();
        try {
            Whitebox.invokeMethod(pm, "parseFile", args[0]);
        } catch (FileNotFoundException ex) {
            Assert.fail("Shouldn't have gotten here..." + ex.getMessage());
        } catch (RuntimeException ex) {
            Assert.fail(ex.getMessage());
        }
    }

}
