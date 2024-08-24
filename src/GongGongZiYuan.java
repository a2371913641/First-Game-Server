import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.PublicKey;
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

    static List<MyListListener> listListeners=new ArrayList<>();

    public void allSocketSend(String data,List<ClientClass> clients) {
      for(int i=clients.size()-1;i>=0;i--){
          if(clients.get(i).onLine) {
              Socket socket=clients.get(i).socket;
              try {
                  OutputStream os = socket.getOutputStream();
                  os.write(data.getBytes());
                  System.out.println(socket.hashCode() + ":" + data);
              } catch (IOException e) {
                  ClientClass clientClass = getSocketClient(socket);
                  LiXian(clientClass);

              }
          }
      }
    }

    public void sendOne(ClientClass clientClass,String s) throws IOException {
        Socket socket=clientClass.socket;
        OutputStream so=socket.getOutputStream();
        so.write(s.getBytes());
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

    public int getClientNamePostion(String name){
        int postion=-1;
        for(int i=0;i<GongGongZiYuan.clients.size();i++){
            ClientClass clientClass=GongGongZiYuan.clients.get(i);
            if(clientClass.name.equals(name)){
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

    public String DatinString(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<GongGongZiYuan.dating.length;i++){
            if(i==GongGongZiYuan.dating.length-1){
                sb.append(GongGongZiYuan.dating[i]);
                break;
            }
            sb.append(GongGongZiYuan.dating[i]+"/n");
        }
        return sb.toString();
    }

    public String getClientString(ArrayList<ClientClass> clientClasses){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<clientClasses.size();i++){
            ClientClass clientClass=clientClasses.get(i);
            if(i==clientClasses.size()-1){
                sb.append(clientClass.name+"/n"+clientClass.zhanghao+"/n"+clientClass.xinbie+"/n"+clientClass.image+"/n"+clientClass.onLine);
                break;
            }
            sb.append(clientClass.name+"/n"+clientClass.zhanghao+"/n"+clientClass.xinbie+"/n"+clientClass.image+"/n"+clientClass.onLine+"/n");
        }
        return sb.toString();
    }

    public String roomListString(ArrayList<Room> list){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<list.size();i++){
            Room room=list.get(i);
            if(i==list.size()-1){
                sb.append(room.roomName+"/n"+room.roomType+"/n"+room.roomAdmin+"/n"+room.roomHaoMa);
                break;
            }
            sb.append(room.roomName+"/n"+room.roomType+"/n"+room.roomAdmin+"/n"+room.roomHaoMa+"/n");
        }
        return sb.toString();
    }

    public void outRoom(ClientClass clientClass){
        clientClass.atRoom.clientClasses.remove(clientClass);
        Room room=clientClass.atRoom;
        clientClass.setLocation("在大厅"+clientClass.nowAtHall);
        clientClass.atRoom=null;
        if(room.clientClasses.isEmpty()){
            removeRoom(clientClass.nowAtHall,room);
        }else {
            allSocketSend("setRoomList:/n" +
                            roomListString(GongGongZiYuan.datingListRoomList.get(clientClass.nowAtHall)) + "_",
                    GongGongZiYuan.onLineClients.get(clientClass.nowAtHall));

            allSocketSend("setInTheRoomClient:/n"+getClientString(room.clientClasses)+"_",room.clientClasses);
            allSocketSend
                    ("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(clientClass.nowAtHall))+"_",room.clientClasses);
        }
        setHaoYouList(clientClass.haoyouList);
        GongGongZiYuan.atDatingOutOfRoom.get(clientClass.nowAtHall).add(clientClass);
    }

    public void removeRoom(int nowAtHall,Room room){
        datingListRoomList.get(nowAtHall).remove(room);
        allSocketSend("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));
        System.out.println("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_"+GongGongZiYuan.datingListRoomList.get(nowAtHall).size());

    }

    public void outDating(ClientClass clientClass){
        int nowAtHall=clientClass.nowAtHall;
        dating[nowAtHall] = dating[nowAtHall] - 1;
        atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
        onLineClients.get(nowAtHall).remove(clientClass);
        allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_",
                GongGongZiYuan.onLineClients.get(nowAtHall));
        allSocketSend("dating:/n" + DatinString()+ "_",isLogin);
        allSocketSend("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));
        nowAtHall=-1;
        clientClass.setNowAtHall(nowAtHall);
        clientClass.setLocation("在选择大厅");
        setHaoYouList(clientClass.haoyouList);

    }

    public List<ClientClass> onLineFriend(List<ClientClass> FirendList){
        List<ClientClass> onLineFriend=new ArrayList<>();
        for(ClientClass clientClass:FirendList){
            if(clientClass.onLine){
                onLineFriend.add(clientClass);
            }
        }
        return onLineFriend;
    }
    public void outDatingLixian(ClientClass clientClass){
        int nowAtHall=clientClass.nowAtHall;
        dating[nowAtHall] = dating[nowAtHall] - 1;
        atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
        onLineClients.get(nowAtHall).remove(clientClass);
        allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_",
                GongGongZiYuan.onLineClients.get(nowAtHall));
        nowAtHall=-1;
        clientClass.setNowAtHall(nowAtHall);
        clientClass.setLocation("在选择大厅");
        setHaoYouList(clientClass.haoyouList);
    }
    public void outSelectDating(ClientClass clientClass){
        clientClass.setLocation("还未进入选择大厅");
        setHaoYouList(clientClass.haoyouList);
    }

    public void CompleteExit(ClientClass clientClass){
        clientClass.setLocation(null);
        clientClass.onLine=false;
        isLogin.remove(clientClass);
        setHaoYouList(clientClass.haoyouList);
        clientSockets.remove(clientClass.socket);
        System.out.println(clientClass.name+"已退出");
    }

    public void LiXian(ClientClass clientClass){
        Room room=null;
        if(clientClass.atRoom!=null){
            room=clientClass.atRoom;
        }
        int notAtHall=clientClass.nowAtHall;
        clientClass.nowAtHall=-1;
        tuichuRemoveList(clientClass,room,notAtHall);
        clientClass.onLine=false;
        clientClass.setLocation(null);

        if(notAtHall!=-1){
            allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(notAtHall))+"_",
                    GongGongZiYuan.onLineClients.get(notAtHall));
            allSocketSend("dating:/n" + DatinString()+ "_",isLogin);
            if(room!=null&&room.clientClasses.isEmpty()){
                removeRoom(notAtHall,room);
            }else if(room!=null){
                allSocketSend("setRoomList:/n" +
                                roomListString(GongGongZiYuan.datingListRoomList.get(notAtHall)) + "_",
                        GongGongZiYuan.onLineClients.get(notAtHall));

                allSocketSend("setInTheRoomClient:/n"+getClientString(room.clientClasses)+"_",room.clientClasses);
                allSocketSend
                        ("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(notAtHall))+"_",room.clientClasses);
            }
        }
        setHaoYouList(clientClass.haoyouList);

    }


    public void tuichuRemoveList(ClientClass clientClass,Room room,int nowAtHall){
        isLogin.remove(clientClass);
        if(nowAtHall!=-1) {
            atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
            if (onLineClients.get(nowAtHall).contains(clientClass)) {
                onLineClients.get(nowAtHall).remove(clientClass);
                dating[nowAtHall] = dating[nowAtHall] - 1;
            }
            if(room!=null) {
                room.clientClasses.remove(clientClass);
            }
        }

    }

    public void setHaoYouList(List<ClientClass> clients){
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).onLine) {
                Socket socket=clients.get(i).socket;
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(("setHaoYouList:/n"+getClientString(beingChlentTiTheFront(clients.get(i).haoyouList))+"_").getBytes());

                } catch (IOException e) {
                    ClientClass clientClass = getSocketClient(socket);
                    LiXian(clientClass);

                }
            }
        }
    }

    public ArrayList<ClientClass> beingChlentTiTheFront(ArrayList<ClientClass> clients){

        for(int i=0;i<clients.size();i++){
            if(clients.get(i).onLine){
                ClientClass clientClass=clients.get(i);
                clients.remove(i);
                clients.add(0,clientClass);
            }
        }
        return clients;
    }

    public void sendSiXin(ClientClass client,String myName,String content,String time){
        if(client.onLine&&!client.getLocation().equals("还未进入选择大厅")){
            System.out.println("onLine="+client.onLine);
            try {
                sendOne(client,"ServerSendSiXin:/n"+myName+"/n"+content+"/n"+time+"_");
            } catch (IOException e) {
                System.out.println("SendFail");
                IOUtil io=new IOUtil();
                io.outputFile(client.name+"的私信.txt",myName+"/n"+content+"/n"+time+"_",true);
            }
        }else{
            IOUtil io=new IOUtil();
            io.outputFile(client.name+"的私信.txt",myName+"/n"+content+"/n"+time+"_",true);
        }
    }

    public void sendTSSiXin(String myName){
        IOUtil io=new IOUtil();
        String s=io.inputFile(myName+"的私信.txt");
        System.out.println(myName+"的私信.txt,s="+s);
        if(!s.isEmpty()){
            System.out.println("S="+s);
            String[] strings=s.split("_");
            System.out.println("sixin="+s);
            for (String string : strings) {
                try {
                    sendOne(clients.get(getClientNamePostion(myName)), "ServerSendSiXin:/n" + string + "_");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        io.deleteFile(myName+"的私信.txt");
    }



}
