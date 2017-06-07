package Connections;

/**
 * Created by jklei on 6/4/2017.
 */
public interface Connection {
    void write(int request);
    int read();
}
