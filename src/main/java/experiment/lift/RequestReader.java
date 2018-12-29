package experiment.lift;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 用于读入请求
 */
public class RequestReader implements Iterator<Request> {

    private Scanner scanner;

    private String nextLine;

    private boolean terminated = false;

    public RequestReader(Scanner scanner) {
        assert scanner != null;
        this.scanner = scanner;
    }

    @Override
    public boolean hasNext() {
        if (terminated)
            return false;
        if (nextLine != null)
            return true;
        if (scanner.hasNextLine()) {
            nextLine = scanner.nextLine();
            if (nextLine.contains("RUN")) {
                terminated = true;
                return false;
            }
        }
        return true;
    }

    @Override
    public Request next() {
        if (terminated)
            return null;
        String line;
        int fromFloor = 0, toFloor = 0, requestSeconds;
        FloorRequestDirection direction = null;
        RequestType type = null;
        if (nextLine == null) {
            while (true) {
                if (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.length() != 0)
                        break;
                } else return null;
            }
        }
        else {
            line = nextLine;
            nextLine = null;
        }
        if (!line.matches("\\(\\s*ER\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)") && !line.matches("\\(\\s*FR\\s*,\\s*(\\d+)\\s*,\\s*(DOWN|UP)\\s*,\\s*(\\d+)\\s*\\)"))
            throw new InputMismatchException(line);
        String[] parameters = line
                .replaceAll("\\s+", "")
                .replace("(", "")
                .replace(")", "")
                .split(",");
        if (parameters[0].equals("FR")) {
            type = RequestType.FLOOR_REQUEST;
            fromFloor = Integer.parseInt(parameters[1]);
            direction = FloorRequestDirection.valueOf(parameters[2]);
            requestSeconds = Integer.parseInt(parameters[3]);
        }
        else {
            type = RequestType.LIFT_INNER_REQUEST;
            toFloor = Integer.parseInt(parameters[1]);
            requestSeconds = Integer.parseInt(parameters[2]);
        }
        return new Request(type, requestSeconds, fromFloor, toFloor, direction);
    }

    @Override
    public void remove() {
        scanner.remove();
    }

}
