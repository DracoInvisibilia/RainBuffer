package Event;

/**
 * Created by jklei on 6/13/2017.
 */
public enum Priority {
    CRITICAL (0, "CRITICAL"),
    ERROR(1, "GET_VALUE"),
    WARNING(2, "SET_VALUE"),
    NOTIFICATION(3, "SET_VALUE");

    private int code;
    private String name;
    Priority(int code, String name) {
        this.code = code;
        this.name = name;
    }

    int getPriority() {
        return this.code;
    }

    String getName() {
        return this.name;
    }
}
