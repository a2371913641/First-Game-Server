import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GongGongZiYuan {


    public static List<Socket> clientSockets=new ArrayList<>();
    static int[] dating={100,0,0,0,0,0,0,0};

    static List<ArrayList<Room>> datingListRoomList=new ArrayList<>();
    static List<ArrayList<ClientClass>> onLineClients=new ArrayList<>();//各个大厅的总玩家

    static List<ClientClass> isLogin=new ArrayList<>();

    static List<ArrayList<ClientClass>> atDatingOutOfRoom=new ArrayList<>();

    static int[] datingRoomHaoma={0,0,0,0,0,0,0,0};//给大厅的房间设置密码的机制，不直接调用，要配合方法经行调用

    static  List<ClientClass> clients=new ArrayList<>();//所有注册过的用户

    public void allSocketSend(String data,List<ClientClass> clients) {
      for(int i=clients.size()-1;i>=0;i--){
          Socket socket=clients.get(i).socket;
          try {
              OutputStream os=socket.getOutputStream();
              os.write(data.getBytes());
              System.out.println(socket.hashCode()+":"+data);
          } catch (IOException e) {
              clientSockets.remove(socket);
              clients.remove(clients.get(i));
              isLogin.remove(clients. get(i));

          }
      }
    }

    public int getRoomHaoMa(int now){
        int i=datingRoomHaoma[now];
        datingRoomHaoma[now]=datingRoomHaoma[now]+1;
        return i;
    }


    public int getClientPostion(String zhanghao){
        int postion=-1;
        for(int i=0;i<GongGongZiYuan.clients.size();i++){
            ClientClass clientClass=GongGongZiYuan.clients.get(i);
            if(clientClass.zhanghao.equals(zhanghao)){
                postion=i;
                break;
            }
        }
        return postion;
    }

    public ClientClass getSocketClient(Socket socket){
        ClientClass client=null;
        for(ClientClass clientClass:isLogin){
            if(clientClass.socket==socket){
                client=clientClass;
                break;
            }
        }
        return client;
    }
}
