package com.gojek.parking;

import com.gojek.parking.model.Vehicle;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.PriorityQueue;
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
    }

    @Test
    public void testCreateParkingLotCommand() throws Exception {
        System.out.println("test create_parking_lot command");
        String command = "create_parking_lot 4";
        ParkingManager pm = new ParkingManager();
        try {
            executeACommand(pm, command);
            Vehicle[] slots = (Vehicle[]) (Object[]) (Whitebox.invokeMethod(pm, "getSlots"));
            Assert.assertEquals(4, slots.length);
            PriorityQueue<Integer> priorityQueue = Whitebox.invokeMethod(pm, "getPriorityQueue");
            Assert.assertEquals(4, priorityQueue.size());
        } catch (RuntimeException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testpark() throws Exception {
        System.out.println("test park command");
        ParkingManager pm = new ParkingManager();
        String vehiclesToPark[] = {
            "park KA-05-MH-1384 Silver",
            "park HR-26-CW-0181 Black",
            "park MG-14-N-2493 Black",
            "park KA-05-MH-9702 Gold"
        };

        try {
            String command = "create_parking_lot " + vehiclesToPark.length;
            executeACommand(pm, command);
            for (int i = 0; i < vehiclesToPark.length; ++i) {
                executeACommand(pm, vehiclesToPark[i]);
                PriorityQueue<Integer> priorityQueue = Whitebox.invokeMethod(pm, "getPriorityQueue");
                Assert.assertEquals(vehiclesToPark.length - (i + 1), priorityQueue.size());
                HashMap<String, Integer> registrationSlot = Whitebox.invokeMethod(pm, "getRegistrationSlot");
                Assert.assertEquals((i + 1), registrationSlot.size());
                Assert.assertTrue(registrationSlot.containsKey(vehiclesToPark[i].split(" ")[1]));
            }
        } catch (RuntimeException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    private void executeACommand(ParkingManager pm, String command) throws Exception {
        Whitebox.invokeMethod(pm, "parseALine", command);
    }

}
