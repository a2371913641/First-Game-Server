import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Spliterator;

public class WriterThread implements Runnable{

    Socket socket;
    GongGongZiYuan gongGongZiYuan=new GongGongZiYuan();
    public WriterThread(Socket socket){
        super();
        this.socket=socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            OutputStream os=socket.getOutputStream();
            while (true){
                String data=br.readLine();
                if(data!=null) {
                    System.out.println(Thread.currentThread().threadId());
                    os.write(data.getBytes());
                }
                os.flush();
            }
        } catch (IOException e) {;
            GongGongZiYuan.clientSockets.remove(socket);
            GongGongZiYuan.isLogin.remove(gongGongZiYuan.getSocketClient(socket));
            GongGongZiYuan.
        }
    }
}
