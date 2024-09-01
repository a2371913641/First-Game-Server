import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerTCP {





    public static void main(String[] args) throws IOException {

        ServerSocket severSocket=new ServerSocket(8088);
        GongGongZiYuan gongGongZiYuan=new GongGongZiYuan();
//
        gongGongZiYuan.readFromAFileAllClient();

        gongGongZiYuan.readFromAFileHaoYouList();

        gongGongZiYuan.initializeDatingAndOnLienClient();

        System.out.println("---------server---------");
        for(int i=0;i<8;i++){
            GongGongZiYuan.datingListRoomList.add(new ArrayList<Room>());
        }
            while (true){
                Socket socket=severSocket.accept();
                System.out.println("用户"+socket.getPort()+"已连接");
                GongGongZiYuan.clientSockets.add(socket);
//                new Thread(new ServerThread(socket)).start();

                new Thread(new ReaderThread(socket)).start();
                new Thread(new WriterThread(socket)).start();
            }

    }

}
