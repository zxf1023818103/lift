package experiment.lift;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        Scanner stdinScanner = new Scanner(System.in);

        System.out.print("请输入输入文件名：");

        String inputFile = stdinScanner.nextLine();

        Scanner inputScanner = new Scanner(new FileInputStream(inputFile));

        System.out.print("请输入输出文件名：");

        String outputFile = stdinScanner.nextLine();

        PrintStream output = new PrintStream(new FileOutputStream(outputFile));

        List<Request> requests = new ArrayList<>();

        while (inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();
            if (line.contains("RUN"))
                break;
            String[] results = line.replace("(", "").replace(")", "").split(",");
            if (line.matches("\\s*\\(\\s*FR\\s*,\\s*\\d+\\s*,\\s*(UP|DOWN)\\s*,\\s*\\d+\\s*\\)\\s*")) {
                requests.add(new FloorRequest(Integer.parseInt(results[1]), RequestDirection.valueOf(results[2]), Integer.parseInt(results[3])));
            }
            else if (line.matches("\\s*\\(\\s*ER\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*")) {
                requests.add(new LiftRequest(Integer.parseInt(results[1]), Integer.parseInt(results[2])));
            }
            else {
                output.println("INVALID[" + line + "]");
            }
        }

        Scheduler scheduler = new Scheduler(requests, output, 13);

        Lift lift = new Lift(11, scheduler, output);

        scheduler.setLift(lift);

        while (true) {
            if (!lift.update())
                break;
        }

        double averageWaitingSeconds = 0;
        output.println("Leaving Seconds:");
        for (Request request : requests) {
            averageWaitingSeconds = request.getWaitingSeconds();
            output.println(request + ": " + request.getCompleteSeconds());
        }
        averageWaitingSeconds /= requests.size();
        output.println("Average Waiting Seconds: " + averageWaitingSeconds);
    }

}
