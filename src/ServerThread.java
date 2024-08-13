import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable{

    Socket socket;


    public ServerThread(Socket socket){
        super();
        this.socket=socket;

    }
    @Override
    public void run() {
        new Thread(new ReaderThread(socket)).start();
        new Thread(new WriterThread(socket)).start();
    }

}
