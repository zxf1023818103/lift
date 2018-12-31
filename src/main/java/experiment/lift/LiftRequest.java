package experiment.lift;

public class LiftRequest extends Request {
    public LiftRequest(int floor, int sendingSeconds) {
        setFloor(floor);
        setSendSeconds(sendingSeconds);
    }
}
