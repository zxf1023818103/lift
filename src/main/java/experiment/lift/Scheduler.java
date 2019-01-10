package experiment.lift;

import experiment.lift.gui.controller.LiftController;
import javafx.application.Platform;

import java.awt.*;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

public class Scheduler implements Iterator<Request> {

    private List<Request> requests;

    private List<Request> backupRequests = new ArrayList<>();

    private double currentSeconds;

    private LiftState currentState;

    private int currentFloor;

    private Lift lift;

    private PrintStream output;

    private int limit;

    private int currentPeopleNumber;

    // 记录重复按下按钮的次数
    private int[] buttonPressingNumber;

    private LiftController liftController;

    private HashMap<Integer, ArrayList<Request>> floorRequestsMap = new HashMap<>();

    public Scheduler(List<Request> requests, PrintStream output, int limit, LiftController liftController) {
        this.requests = requests;
        this.output = output;
        this.limit = limit;
        this.currentPeopleNumber = 0;
        this.liftController = liftController;
        requests.sort(Comparator.comparingInt(Request::getSendSeconds));
        requests.forEach(request -> {
            int floor = request.getFloor();
            ArrayList<Request> requests1 = floorRequestsMap.getOrDefault(floor, new ArrayList<>());
            requests1.add(request);
            backupRequests.add(request);
            floorRequestsMap.put(floor, requests1);
        });
    }

    public void setLift(Lift lift) {
        this.lift = lift;
        this.buttonPressingNumber = new int[lift.getFloorSize()];
    }

    /**
     * @return 当前时间是否有下一个目标楼层
     */
    @Override
    public boolean hasNext() {
        return !backupRequests.isEmpty() && backupRequests.get(0).getSendSeconds() <= currentSeconds && currentPeopleNumber < limit;
    }

    /**
     * 获取下一个目标楼层
     *
     * @return 下一个目标楼层
     */
    @Override
    public Request next() {
        if (hasNext()) {
            Iterator<Request> iterator = backupRequests.listIterator();
            int destination = 0;
            Request request = null;
            loop:
            while (iterator.hasNext()) {
                request = iterator.next();
                LiftState nextState = request.getLiftStateByCurrentFloor(currentFloor);
                switch (nextState) {
                    case DOWN:
                        if (currentFloor > request.getFloor()) {
                            destination = request.getFloor();
                            iterator.remove();
                            break loop;
                        }
                    case UP:
                        if (currentFloor < request.getFloor()) {
                            destination = request.getFloor();
                            iterator.remove();
                            break loop;
                        }
                    case STILL:
                        destination = request.getFloor();
                        iterator.remove();
                        break loop;
                }
            }
            if (destination == 0) {
                return null;
            }
            if (lift != null && !lift.pressButton(destination)) {
                buttonPressingNumber[destination]++;
                output.println("SAME[" + request + "]");
                return null;
            }
            return request;
        }
        return null;
    }

    public boolean update(double currentSeconds, LiftState currentState, int currentFloor) {
        if (backupRequests.isEmpty())
            return false;
        this.currentSeconds = currentSeconds;
        this.currentState = currentState;
        this.currentFloor = currentFloor;
        liftController.flicker(currentState);
        Platform.runLater(() -> liftController.floorLabel.setText(Integer.toString(currentFloor)));
        return true;
    }

    public void onPeopleNumberIncreased(int floor, double currentSeconds) {
        Platform.runLater(() -> liftController.currentPeopleNumberLabel.setText(Integer.toString(currentPeopleNumber)));
        if (currentPeopleNumber == limit) {
            // 超载发出警报
            Toolkit.getDefaultToolkit().beep();
        } else {
            currentPeopleNumber++;
            buttonPressingNumber[floor]++;
            floorRequestsMap.getOrDefault(floor, new ArrayList<>()).forEach(request -> request.setStartSeconds(currentSeconds));
        }
    }

    public void onPeopleNumberDecreased(int floor, double currentSeconds) {
        Platform.runLater(() -> liftController.currentPeopleNumberLabel.setText(Integer.toString(currentPeopleNumber)));
        floorRequestsMap.getOrDefault(floor, new ArrayList<>()).forEach(request -> request.setCompleteSeconds(currentSeconds));
        currentPeopleNumber--;
        buttonPressingNumber[floor] = 0;
    }

}
