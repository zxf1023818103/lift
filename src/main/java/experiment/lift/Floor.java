package experiment.lift;

/**
 * 楼层
 */
public class Floor {

    private int floorNumber;

    private RequestQueue requestQueue;

    private boolean upRequestSent = false;

    private boolean downRequestSent = false;

    public Floor(int floorNumber, RequestQueue requestQueue) {
        this.floorNumber = floorNumber;
        this.requestQueue = requestQueue;
    }

    public int getFloorNumber() {
        return this.floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }
}
