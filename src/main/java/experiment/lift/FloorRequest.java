package experiment.lift;

public class FloorRequest extends Request {

    private RequestDirection direction;

    public FloorRequest(int floor, RequestDirection direction, int sendingSeconds) {
        this.direction = direction;
        setSendSeconds(sendingSeconds);
        setFloor(floor);
    }

    public RequestDirection getDirection() {
        return direction;
    }

    public void setDirection(RequestDirection direction) {
        this.direction = direction;
    }

}
