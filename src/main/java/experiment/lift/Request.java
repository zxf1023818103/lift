package experiment.lift;

public abstract class Request implements Cloneable {

    private int sendingSeconds;

    private double completeSeconds;

    private double startSeconds;

    private int floor;

    public int getSendSeconds() {
        return sendingSeconds;
    }

    public void setSendSeconds(int sendingSeconds) {
        this.sendingSeconds = sendingSeconds;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getWaitingSeconds() {
        return startSeconds - sendingSeconds;
    }

    public int getSendingSeconds() {
        return sendingSeconds;
    }

    public void setSendingSeconds(int sendingSeconds) {
        this.sendingSeconds = sendingSeconds;
    }

    public double getCompleteSeconds() {
        return completeSeconds;
    }

    public void setCompleteSeconds(double completeSeconds) {
        this.completeSeconds = completeSeconds;
    }

    public double getStartSeconds() {
        return startSeconds;
    }

    public void setStartSeconds(double startSeconds) {
        this.startSeconds = startSeconds;
    }

    public LiftState getLiftStateByCurrentFloor(int currentFloor) {
        if (this instanceof FloorRequest) {
            switch (((FloorRequest) this).getDirection()) {
                case UP:
                    return LiftState.UP;
                case DOWN:
                    return LiftState.DOWN;
            }
        }
        if (floor > currentFloor)
            return LiftState.UP;
        else if (floor < currentFloor)
            return LiftState.DOWN;
        else
            return LiftState.STILL;
    }

    @Override
    public String toString() {
        if (this instanceof FloorRequest) {
            return "(FR, " + floor + ", " + ((FloorRequest) this).getDirection() + ", " + sendingSeconds + ")";
        }
        else
            return "(ER, " + floor + ", " + sendingSeconds + ")";
    }
}
