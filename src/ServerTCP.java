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
        IOUtil io=new IOUtil();
        io.createFile("AllClientZhanghao.txt");
        String data=io.inputFile("AllClientZhanghao.txt");
        if(!data.equals("")){
            String[] strings=data.split("/n");
            System.out.println(strings.length);
            for(int i=0;i<strings.length;i++){
                String[] strings1=io.inputFile(strings[i]+"ziliao.txt").split("/n");
                System.out.println(strings[i]+":"+strings1.length);
                //String zhanghao,String admin,String name,String xb
                GongGongZiYuan.clients.add(new ClientClass(strings1[0],strings1[1],strings1[2],strings1[3],Integer.parseInt(strings1[4]),false));
                String string2=io.inputFile(strings[i]+"的好友.txt");
                if(!string2.equals("")){
                    String[] strings2= string2.split("/n");
                    for(int j=0;j<strings2.length;j++){
                        GongGongZiYuan.clients.get(i).haoyouList.add(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings2[j])));
                    }
                }
            }
        }

        for(int i=0;i<8;i++){
            GongGongZiYuan.onLineClients.add(i,new ArrayList<ClientClass>());
            GongGongZiYuan.atDatingOutOfRoom.add(i,new ArrayList<ClientClass>());
        }

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
