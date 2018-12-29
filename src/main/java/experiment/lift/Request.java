package experiment.lift;

/**
 * 请求
 */
public class Request {

    private RequestType type;

    private int requestSeconds;

    private int fromFloor;

    private int toFloor;

    private FloorRequestDirection direction;

    @java.beans.ConstructorProperties({"type", "requestSeconds", "fromFloor", "toFloor", "direction"})
    public Request(RequestType type, int requestSeconds, int fromFloor, int toFloor, FloorRequestDirection direction) {
        this.type = type;
        this.requestSeconds = requestSeconds;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.direction = direction;
    }

    public Request() {
    }

    public RequestType getType() {
        return this.type;
    }

    public int getRequestSeconds() {
        return this.requestSeconds;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public FloorRequestDirection getDirection() {
        return this.direction;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public void setRequestSeconds(int requestSeconds) {
        this.requestSeconds = requestSeconds;
    }

    public void setFromFloor(int fromFloor) {
        this.fromFloor = fromFloor;
    }

    public void setToFloor(int toFloor) {
        this.toFloor = toFloor;
    }

    public void setDirection(FloorRequestDirection direction) {
        this.direction = direction;
    }
}
