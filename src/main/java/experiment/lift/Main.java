package experiment.lift;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /**
     * 输入流
     */
    private static InputStream inputStream = System.in;

    /**
     * 输出流
     */
    private static PrintStream outputStream = System.out;

    private static RequestReader requestReader = new RequestReader(new Scanner(inputStream));

    private static List<Floor> floors = new ArrayList<>();

    private static RequestQueue requestQueue = new RequestQueue();

    static {
        for (int i = 1; i <= 10; i++)
            floors.add(new Floor(i, requestQueue));
    }
    private static Lift lift = new Lift(floors);

    private static Scheduler scheduler = new Scheduler(lift, requestQueue);

    public static void main(String... args) {
        System.out.print("Input filename: ");
        try {
            inputStream = new FileInputStream(new Scanner(System.in).nextLine());
            requestReader = new RequestReader(new Scanner(inputStream));
            while (requestReader.hasNext()) {
                Request request = requestReader.next();
                requestQueue.offer(request);
            }
            scheduler.start();
        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println("# " + e.getMessage());
        }
    }

}
