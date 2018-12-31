package experiment.lift;

public abstract class Request {

    private int sendingSeconds;

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
