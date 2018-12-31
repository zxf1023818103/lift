package experiment.lift;

import java.io.PrintStream;
import java.util.*;

public class Scheduler implements Iterator<Request> {

    private List<Request> requests;

    private double currentSeconds;

    private LiftState currentState;

    private int currentFloor;

    private Lift lift;

    private PrintStream output;

    public Scheduler(List<Request> requests, PrintStream output) {
        this.requests = requests;
        this.output = output;
        requests.sort(Comparator.comparingInt(Request::getSendSeconds));
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }

    /**
     * @return 当前时间是否有下一个目标楼层
     */
    @Override
    public boolean hasNext() {
        return !requests.isEmpty() && requests.get(0).getSendSeconds() <= currentSeconds;
    }

    /**
     * 获取下一个目标楼层
     * @return 下一个目标楼层
     */
    @Override
    public Request next() {
        if (hasNext()) {
            Iterator<Request> iterator = requests.listIterator();
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
                output.println("SAME[" + request + "]");
                return null;
            }
            return request;
        }
        return null;
    }

    public boolean update(double currentSeconds, LiftState currentState, int currentFloor) {
        if (requests.isEmpty())
            return false;
        this.currentSeconds = currentSeconds;
        this.currentState = currentState;
        this.currentFloor = currentFloor;
        return true;
    }

}
