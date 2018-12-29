package experiment.lift;

/**
 * 调度器
 */
public class Scheduler {

    private RequestQueue queue;

    private Lift lift;

    private boolean startFromZeroSecond = false;

    public Scheduler(Lift lift, RequestQueue queue) {
        assert lift != null;
        assert queue != null;
        this.lift = lift;
        this.queue = queue;
    }

    /**
     * 开始运行电梯
     *
     * @throws InvalidUpRequestException 当在顶楼请求上行时抛出
     * @throws InvalidDownRequestException 当在一楼请求下行时抛出
     */
    public void start() throws InvalidUpRequestException, InvalidDownRequestException {
        while (!queue.isEmpty()) {
            Request request = queue.poll();
            if (request.getType() == RequestType.FLOOR_REQUEST) {
                if (request.getDirection() == FloorRequestDirection.UP && request.getFromFloor() == 10)
                    throw new InvalidUpRequestException();
                else if (request.getDirection() == FloorRequestDirection.DOWN && request.getFromFloor() == 1)
                    throw new InvalidDownRequestException();
            }
            if (request.getRequestSeconds() >= lift.getElapsedSeconds()) {
                if (request.getRequestSeconds() == 0)
                    startFromZeroSecond = true;
                if (startFromZeroSecond)
                    lift.runTo(request);
            }
        }
    }

}
