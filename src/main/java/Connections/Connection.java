package Connections;

/**
 * Created by jklei on 6/4/2017.
 */
public interface Connection {
    void write(int request);
    void write(byte[] request);
    int writeAndRead(int request);
    int read();
}
