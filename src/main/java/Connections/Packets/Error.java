package Connections.Packets;

/**
 * Created by jklei on 6/12/2017.
 */
public enum Error {
    NO_ERROR(0, "NO_ERROR"),
    INVALID_COMMAND(1, "INVALID_COMMAND"),
    INVALID_VALUE(2, "INVALID_VALUE"),
    COMMAND_FAILURE(3, "COMMAND_FAILURE"),
    SENSOR_FAILURE(4, "SENSOR_FAILURE"),
    OTHER_ERROR(5, "OTHER_ERROR");

    private int code;
    private String name;
    Error(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Error getError(int code) {
        if(NO_ERROR.code==code) return NO_ERROR;
        if(INVALID_COMMAND.code==code) return INVALID_COMMAND;
        if(INVALID_VALUE.code==code) return INVALID_VALUE;
        if(COMMAND_FAILURE.code==code) return COMMAND_FAILURE;
        if(SENSOR_FAILURE.code==code) return SENSOR_FAILURE;
        return OTHER_ERROR;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
