package experiment.lift;

import experiment.lift.gui.controller.LiftController;
import javafx.animation.Timeline;
import javafx.application.Platform;

import java.io.PrintStream;

public class Lift {

    /**
     * 经过一个楼层的时间（秒）
     */
    protected final static double SECONDS_PER_FLOOR = 0.5;

    /**
     * 到达楼层后的停止时间（秒）
     */
    protected final static int PAUSE_SECONDS = 1;

    /**
     * 状态更新间隔时间
     */
    protected final static double TICK_SECONDS = SECONDS_PER_FLOOR;

    /**
     * 电梯所用的调度器
     */
    private Scheduler scheduler;

    /**
     * 电梯内按钮的状态，是否按下
     */
    private final boolean[] floorButtonPressed;

    /**
     * 当前所在楼层
     */
    private int currentFloor;

    /**
     * 电梯当前状态
     */
    private LiftState currentState;

    /**
     * 当前运行时间（秒）
     */
    private double currentSeconds;

    /**
     * 下一个到达的楼层
     */
    private int nextDestination;

    /**
     * 下一个请求
     */
    private Request nextRequest;

    /**
     * 信息输出流
     */
    private PrintStream output;

    /**
     * 停靠剩余时间
     */
    private double pauseRestSeconds;

    /**
     * 停靠之前的状态，用于从停靠状态恢复成向上/向下状态
     */
    private LiftState stateBeforeStopped;

    /**
     * 楼层数
     */
    private int floorSize;

    /**
     * 开关门动画
     */
    private Timeline openDoorAnimation;

    /**
     *
     */
    private LiftController liftController;

    /**
     * @param floorSize 楼层数
     * @param openDoorAnimation
     * @param liftController
     *
     */
    public Lift(int floorSize, Scheduler scheduler, PrintStream output, Timeline openDoorAnimation, LiftController liftController) {

        /// 初始化楼层按钮
        floorButtonPressed = new boolean[floorSize];

        /// 设置初始时电梯所在楼层为 1 层
        currentFloor = 1;

        /// 设置初始状态为停止
        currentState = LiftState.STILL;

        /// 默认从 0 秒开始运行
        currentSeconds = 0;

        /// 下一个到达的楼层默认为 1
        nextDestination = 1;

        /// 默认之前的状态是停靠，这样会在没有请求时永远保持停靠状态
        stateBeforeStopped = LiftState.STILL;

        /// 设置楼层数
        this.floorSize = floorSize;

        /// 设置电梯的调度器
        this.scheduler = scheduler;

        /// 设置输出流
        this.output = output;

        /// 设置开关门动画
        this.openDoorAnimation = openDoorAnimation;

        ///
        this.liftController = liftController;
    }

    public int getFloorSize() {
        return floorSize;
    }

    public boolean pressButton(int floor) {
        if (floorButtonPressed[floor]) {
            return false;
        }
        floorButtonPressed[floor] = true;
        onButtonPressed(floor);
        return true;
    }

    private void cancelPressedButton(int floor) {
        if (!floorButtonPressed[floor]) {
            return;
        }
        floorButtonPressed[floor] = false;
        onPressedButtonCanceled(floor);
    }

    private void moveDownOneFloor() {
        currentFloor -= 1;
        onMoving(currentFloor);
    }

    private void moveUpOneFloor() {
        currentFloor += 1;
        onMoving(currentFloor);
    }

    private void setCurrentState(LiftState currentState) {
        if (currentState == this.currentState)
            return;
        this.currentState = currentState;
        onStateChanged(currentState);
    }

    protected void onPressedButtonCanceled(int floor) {
        scheduler.onPeopleNumberDecreased(floor, currentSeconds);
        liftController.paused = true;
        Platform.runLater(() -> openDoorAnimation.play());
    }

    protected void onMoving(int currentFloor) {}

    protected void onStateChanged(LiftState currentState) {}

    protected void onButtonPressed(int floor) {
        /// 以按钮的按下作为人数增加的标志
        /// 通知调度器人数改变
        scheduler.onPeopleNumberIncreased(floor, currentSeconds);
    }

    public boolean update() {

        if (!scheduler.update(currentSeconds, currentState, currentFloor))
            return false;

        if (nextDestination == currentFloor) {
            if (nextRequest != null)
                output.println(nextRequest.toString().replace("(", "[").replace(")", "]") + " / (" + currentFloor + ", " + currentState + ", " + String.format("%.1f", currentSeconds) + ")");
            /// 到达目标楼层，取消按钮的按下状态
            cancelPressedButton(currentFloor);
            setCurrentState(LiftState.STILL);
            if (stateBeforeStopped != LiftState.STILL)
                pauseRestSeconds = PAUSE_SECONDS;
            if (scheduler.hasNext()) {
                nextRequest = scheduler.next();
                nextDestination = nextRequest.getFloor();
            }
        }
        else {
            stateBeforeStopped = currentState;
            if (nextDestination > currentFloor)
                setCurrentState(LiftState.UP);
            else
                setCurrentState(LiftState.DOWN);
        }

        switch (currentState) {
            case UP:
                moveUpOneFloor();
                break;
            case DOWN:
                moveDownOneFloor();
                break;
            case STILL:
                if (pauseRestSeconds != 0)
                    pauseRestSeconds -= 0.5;
                else {
                    setCurrentState(stateBeforeStopped);
                }
                break;
        }

        currentSeconds += TICK_SECONDS;

        return true;
    }

}
