package Event;

/**
 * Created by jklei on 6/13/2017.
 */
public enum Priority {
    CRITICAL (0, "CRITICAL"),
    ERROR(1, "ERROR"),
    WARNING(2, "WARNING"),
    NOTIFICATION(3, "NOTIFICATION");

    private int code;
    private String name;
    Priority(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getPriority() {
        return this.code;
    }

    String getName() {
        return this.name;
    }
}
