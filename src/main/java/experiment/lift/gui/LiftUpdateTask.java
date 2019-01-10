package experiment.lift.gui;

import experiment.lift.*;
import experiment.lift.gui.controller.LiftController;
import javafx.application.Platform;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;

public class LiftUpdateTask extends TimerTask {

    private LiftController liftController;

    private Scheduler scheduler;

    private Lift lift;

    public LiftUpdateTask(LiftController liftController) throws FileNotFoundException {
        this.liftController = liftController;
        Scanner inputScanner = new Scanner(new FileInputStream("input.txt"));
        PrintStream outputStream = new PrintStream("output.txt");
        ArrayList<Request> requests = new ArrayList<>();
        while (inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();
            if (line.contains("RUN"))
                break;
            String[] results = line.replace("(", "").replace(")", "").split(",");
            if (line.matches("\\s*\\(\\s*FR\\s*,\\s*\\d+\\s*,\\s*(UP|DOWN)\\s*,\\s*\\d+\\s*\\)\\s*")) {
                requests.add(new FloorRequest(Integer.parseInt(results[1]), RequestDirection.valueOf(results[2]), Integer.parseInt(results[3])));
            } else if (line.matches("\\s*\\(\\s*ER\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*")) {
                requests.add(new LiftRequest(Integer.parseInt(results[1]), Integer.parseInt(results[2])));
            } else {
                outputStream.println("INVALID[" + line + "]");
            }
        }

        scheduler = new Scheduler(requests, outputStream, 13, liftController);
        lift = new Lift(11, scheduler, outputStream, liftController.openDoorAnimation, liftController);
        scheduler.setLift(lift);
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            if (!liftController.paused) {
                if (!lift.update()) {
                    /// 结束任务
                    cancel();
                    liftController.pauseButton.setDisable(true);
                    liftController.startButton.setDisable(false);
                }
            }
        });
    }
}
