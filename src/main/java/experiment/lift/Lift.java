package experiment.lift;

import java.io.PrintStream;
import java.util.List;

/**
 * 电梯
 */
public class Lift {

    private static final double OPEN_CLOSE_DOOR_SECONDS = 1;

    private static final double RUNNING_SECOND_PER_FLOOR = 0.5;

    /**
     * 关联的楼层
     */
    private List<Floor> floors;

    /**
     * 当前所在楼层
     */
    private Floor currentFloor;

    /**
     * 电梯状态
     */
    private LiftState state = LiftState.STILL;

    /**
     * 输出流
     */
    private PrintStream outputStream = System.out;

    /**
     * 经过的时间（秒）
     */
    private double elapsedSeconds = 0;

    public Lift(List<Floor> floors) {
        assert floors != null;
        assert floors.size() != 0;
        this.floors = floors;
        this.currentFloor = floors.get(0);
    }

    public void runTo(Request request) {
        if (currentFloor.getFloorNumber() == request.getToFloor())
            state = LiftState.STILL;
        else if (currentFloor.getFloorNumber() < request.getToFloor())
            state = LiftState.UP;
        else
            state = LiftState.DOWN;
        elapsedSeconds = request.getRequestSeconds() + Math.abs(request.getToFloor() - currentFloor.getFloorNumber()) * RUNNING_SECOND_PER_FLOOR + OPEN_CLOSE_DOOR_SECONDS;
        outputStream.println(String.format("(%d, %s, %.1f)", request.getToFloor() == 0 ? 1 : request.getToFloor(), state, elapsedSeconds));
        currentFloor = floors.get(request.getToFloor());
    }

    public Floor getCurrentFloor() {
        return this.currentFloor;
    }

    public double getElapsedSeconds() {
        return this.elapsedSeconds;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}
