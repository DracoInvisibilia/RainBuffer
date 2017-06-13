package Connections.Packets;

/**
 * Created by jklei on 6/12/2017.
 */
public enum Command {
    INIT (0, "INITIALISATION"),
    GET(1, "GET_VALUE"),
    SET(2, "SET_VALUE"),
    RESET(3, "SET_VALUE"),
    NO_COMMAND(-1, "NO_COMMAND");

    private int code;
    private String name;
    Command(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Command getCommand(int code) {
        if(code == INIT.getCode()) return INIT;
        if(code == GET.getCode()) return GET;
        if(code == SET.getCode()) return SET;
        if(code == RESET.getCode()) return RESET;
        return NO_COMMAND;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
