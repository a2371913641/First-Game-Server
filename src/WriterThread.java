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
            ClientClass clientClass=gongGongZiYuan.getSocketClient(socket);
//            if(clientClass.atRoom!=null){
//                gongGongZiYuan.outRoom(clientClass);
//            }
//            gongGongZiYuan.outDating(clientClass);
//            gongGongZiYuan.outSelectDating(clientClass);
//            gongGongZiYuan.CompleteExit(clientClass);

            System.out.println("WriterThread="+clientClass.name);

        }
    }
}
