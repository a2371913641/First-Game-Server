import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

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

    IOUtil io=new IOUtil();

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
       if(clientClass.socket!=null){
           Socket socket=clientClass.socket;
           OutputStream so=socket.getOutputStream();
           so.write(s.getBytes());
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
            if(clientClass.getZhanghao().equals(zhanghao)){
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
            if(clientClass.getName().equals(name)){
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
                sb.append(clientClass.getName()+"/n"+clientClass.getZhanghao()+"/n"+clientClass.getXinbie()+"/n"+clientClass.image+"/n"+clientClass.onLine);
                break;
            }
            sb.append(clientClass.getName()+"/n"+clientClass.getZhanghao()+"/n"+clientClass.getXinbie()+"/n"+clientClass.image+"/n"+clientClass.onLine+"/n");
        }
        return sb.toString();
    }

    private String getClientRoomString(ArrayList<ClientClass> clientClasses){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<clientClasses.size();i++){
            ClientClass clientClass=clientClasses.get(i);
            if(i==clientClasses.size()-1){
                sb.append(clientClass.getName()+"/n"+clientClass.getZhanghao()+"/n"+clientClass.getXinbie()+"/n"+clientClass.image+"/n"+clientClass.onLine+"/n"+clientClass.getSeat()+"/n"+clientClass.getClientState()+"/n"+clientClass.team);
                break;
            }
            sb.append(clientClass.getName()+"/n"+clientClass.getZhanghao()+"/n"+clientClass.getXinbie()+"/n"+clientClass.image+"/n"+clientClass.onLine+"/n"+clientClass.getSeat()+"/n"+clientClass.getClientState()+"/n"+clientClass.team+"/n");
        }
        return sb.toString();
    }

    public String roomListString(ArrayList<Room> list){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<list.size();i++){
            Room room=list.get(i);
            if(i==list.size()-1){
                sb.append(room.getRoomName()+"/n"+room.getRoomType()+"/n"+room.getRoomAdmin()+"/n"+room.getRoomHaoMa());
                break;
            }
            sb.append(room.getRoomName()+"/n"+room.getRoomType()+"/n"+room.getRoomAdmin()+"/n"+room.getRoomHaoMa()+"/n");
        }
        return sb.toString();
    }


    //离开房间，改变全部需要改变的变量
    public void outRoom(ClientClass clientClass){
        int seat=clientClass.getSeat();
        Room room=clientOutRoom(clientClass);
        room.giveBackTeam(clientClass.getTeam());
        room.giveBackSeat(clientClass.getSeat());
        clientClass.clearTeam();
        clientClass.setSeat(-1);
        if(room.clientClasses.isEmpty()){
            removeRoom(clientClass.nowAtHall,room);
        }else {
            if(seat==room.getFangZhu()) {
                resetFangZhu(room);
            }
            resetRoomNumberOfPeople(room);
            resetRoomClientState(room);
            resetDatingRoomList(clientClass.nowAtHall);
        }
        resetYaoqingList(clientClass.nowAtHall);
        setClientsIsHaoYouList(clientClass.haoyouList);

    }

    //离开房间，仅仅改变用户参数
    private Room clientOutRoom(ClientClass clientClass){
        Room room=clientClass.getAtRoom();
        clientClass.getAtRoom().clientClasses.remove(clientClass);
        clientClass.setLocation("在大厅"+clientClass.nowAtHall);
        clientClass.setAtRoom(null);
        atDatingOutOfRoom.get(clientClass.nowAtHall).add(clientClass);
        tellClientDaTingClientList(clientClass);
        return room;
    }

    public void tellClientDaTingClientList(ClientClass clientClass){
        try {
            sendOne(clientClass,"datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(clientClass.nowAtHall))+"_");
        } catch (IOException e) {
            outDatingLixian(clientClass);
        }

    }

    public void removeRoom(int nowAtHall,Room room){
        datingListRoomList.get(nowAtHall).remove(room);
        resetDatingRoomList(nowAtHall);
        resetYaoqingList(nowAtHall);
    }

    //重置房间的人数
    public void resetRoomNumberOfPeople(Room room){
        allSocketSend("setInTheRoomClient:/n"+getClientRoomString(room.clientClasses)+"_",room.clientClasses);
    }

    //重置当前大厅邀请列表
    public void resetYaoqingList(int nowAtHall){
        allSocketSend
                ("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall))+"_",onLineClients.get(nowAtHall));
    }

    //重置当前大厅房间列表
    public void resetDatingRoomList(int nowAtHall){
        allSocketSend("setRoomList:/n" +
                        roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall)) + "_",
                GongGongZiYuan.onLineClients.get(nowAtHall));
    }

    public void outDating(ClientClass clientClass){
        int nowAtHall=clientClass.nowAtHall;
        dating[nowAtHall] = dating[nowAtHall] - 1;
        atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
        onLineClients.get(nowAtHall).remove(clientClass);
        allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_",
                GongGongZiYuan.onLineClients.get(nowAtHall));
        allSocketSend("dating:/n" + DatinString()+ "_",isLogin);
        resetYaoqingList(nowAtHall);
        nowAtHall=-1;
        clientClass.setNowAtHall(nowAtHall);
        clientClass.setLocation("在选择大厅");
        setClientsIsHaoYouList(clientClass.haoyouList);

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
        setClientsIsHaoYouList(clientClass.haoyouList);
    }


    public void LiXian(ClientClass clientClass){
        Room room=null;
        int seat=-1;
        if(clientClass.getAtRoom()!=null){
            seat=clientClass.getSeat();
            room=clientClass.getAtRoom();
        }
        int notAtHall=clientClass.nowAtHall;
        clientClass.nowAtHall=-1;
        tuichuRemoveList(clientClass,room,notAtHall);
        clientClass.onLine=false;
        clientClass.setLocation(null);
        clientClass.clearTeam();
        clientClass.setSeat(-1);
        if(notAtHall!=-1){
            allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(notAtHall))+"_",
                    GongGongZiYuan.onLineClients.get(notAtHall));
            allSocketSend("dating:/n" + DatinString()+ "_",isLogin);
            if(room!=null&&room.clientClasses.isEmpty()){
                removeRoom(notAtHall,room);
            }else if(room!=null){
                if(seat!=-1&&seat==room.getFangZhu()) {
                    resetFangZhu(room);
                }
                resetRoomNumberOfPeople(room);
                resetDatingRoomList(notAtHall);

            }
        }
        resetYaoqingList(notAtHall);
        setClientsIsHaoYouList(clientClass.haoyouList);

    }


    //退出房间，大厅，在线等列表
    public void tuichuRemoveList(ClientClass clientClass,Room room,int nowAtHall){
        isLogin.remove(clientClass);
        if(nowAtHall!=-1) {
            atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
            if (onLineClients.get(nowAtHall).contains(clientClass)) {
                onLineClients.get(nowAtHall).remove(clientClass);
                dating[nowAtHall] = dating[nowAtHall] - 1;
            }
            if(room!=null) {
                room.giveBackTeam(clientClass.getTeam());
                room.giveBackSeat(clientClass.getSeat());
                room.clientClasses.remove(clientClass);
                clientClass.setSeat(-1);

            }
        }

    }

    public void setClientsIsHaoYouList(List<ClientClass> clients){
        for(int i = 0; i< clients.size(); i++){
            if(clients.get(i).onLine) {
                Socket socket= clients.get(i).socket;
                try {
                    OutputStream os = socket.getOutputStream();
                    callClientMyselfHaoYouList(os,clients.get(i));

                } catch (IOException e) {
                    ClientClass clientClass = getSocketClient(socket);
                    LiXian(clientClass);

                }
            }
        }
    }


    //从文件中读出好友列表
    public void readFromAFileHaoYouList(){
        for(int i=0;i<GongGongZiYuan.clients.size();i++){
            String string2=io.inputFile(GongGongZiYuan.clients.get(i).getZhanghao()+"的好友.txt");
            if(!string2.equals("")){
                String[] strings2= string2.split("/n");
                for(int j=0;j<strings2.length;j++){
                    GongGongZiYuan.clients.get(i).haoyouList.add(GongGongZiYuan.clients.get(getClientPostion(strings2[j])));
                }
            }
        }
    }

    public void readOneClientFromAFileHaoYouList(ClientClass clientClass){
        for(int i=clientClass.haoyouList.size()-1;i>=0;i--){
            clientClass.haoyouList.remove(i);
        }
        String string2=io.inputFile(clientClass.getZhanghao()+"的好友.txt");
        if(!string2.equals("")){
            String[] strings2= string2.split("/n");
            for(int j=0;j<strings2.length;j++){
                clientClass.haoyouList.add(GongGongZiYuan.clients.get(getClientPostion(strings2[j])));
            }
        }
    }

    public void readFromAFileAllClient(){
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

            }
        }
    }

    //单向删除好友
    public void deleteFriend(ClientClass myClient,String adverseZhangHao){
        myClient.haoyouList.remove(clients.get(getClientPostion(adverseZhangHao)));
        io.deleteFile(myClient.getZhanghao()+"的好友.txt");
        for(int i= myClient.haoyouList.size()-1;i>=0;i--){
            io.outputFile(myClient.getZhanghao()+"的好友.txt",myClient.haoyouList.get(i).getZhanghao()+"/n",true);
        }
    }

    public void initializeDatingAndOnLienClient(){
        for(int i=0;i<8;i++){
            onLineClients.add(i,new ArrayList<ClientClass>());
            atDatingOutOfRoom.add(i,new ArrayList<ClientClass>());
        }
    }
    public void callClientMyselfHaoYouList(OutputStream os, ClientClass myClientClass){
        try {

            os.write(("setHaoYouList:/n"+getClientString(beingClientTiTheFront(myClientClass.haoyouList))+"_").getBytes());
        } catch (IOException e) {
            LiXian(myClientClass);
        }
    }

    //将在线用户放在列表前面
    public ArrayList<ClientClass> beingClientTiTheFront(ArrayList<ClientClass> clients){

        for(int i=clients.size()-1;i>=0;i--){
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
                io.outputFile(client.getName()+"的私信.txt",myName+"/n"+content+"/n"+time+"_",true);
            }
        }else{
            IOUtil io=new IOUtil();
            io.outputFile(client.getName()+"的私信.txt",myName+"/n"+content+"/n"+time+"_",true);
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
                    System.out.println("send私信="+string);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        io.deleteFile(myName+"的私信.txt");
    }

    //clientClass进入room
    public void jinruRoom(Room room,ClientClass clientClass){
        int seat=room.getSeat();
        clientClass.setSeat(seat);
        System.out.println("seat="+seat);
        room.clientClasses.add(clientClass);
    }

    //获得对手玩家的ClientClass
    public ClientClass getRivalClient(ClientClass myClientClass){
        ClientClass rivalClient = null;
       for(ClientClass clientClass:myClientClass.getAtRoom().clientClasses){
           if(clientClass.getTeam().equals(myClientClass.getTeam())&&clientClass.getSeat()!=myClientClass.getSeat()){
               rivalClient=clientClass;
               break;
           }
       }
       return rivalClient;
    }

    //重新选择房主
    private void resetFangZhu(Room room){
       for (int j = 0; j < 6; j++) {
           for (int i = 0; i < room.clientClasses.size(); i++) {
               if (room.clientClasses.get(i).getSeat() == j) {
                   room.setFangZhu(room.clientClasses.get(i).getSeat());
                   room.clientClasses.get(i).setClientState("房主");
                   return;
               }
           }
       }

    }

    public void setFangZhu(Room room){
        for(ClientClass clientClass:room.clientClasses){
            if(clientClass.clientState.equals("房主")){
                room.setFangZhu(clientClass.getSeat());
            }
        }
    }

    //告诉所有房间内的玩家自己的角色发生的变化
    public void resetRoomClientState(Room room){
        for(int i=0;i<room.clientClasses.size();i++){
            try {
                sendOne(room.clientClasses.get(i),"ServerClientState:/n"+room.clientClasses.get(i).clientState+"_");
                System.out.println(room.clientClasses.get(i).getName()+"state:"+room.clientClasses.get(i).clientState);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //已准备的玩家人数
    public int getReserveNumber(Room room){
        int number=1;
        for(ClientClass clientClass:room.clientClasses){
            if(clientClass.clientState.equals("已准备")){
                number++;
            }
        }
        return number;
    }


    public Boolean isCanStateGame(Room room) {
        boolean canStateGame=false;
        int red=0;
        int blue=0;
        int yellow=0;
        List<String> surplusTeam=room.getTeamSurplus();
        if(getReserveNumber(room)%2==0){
            for(String s:surplusTeam){
                switch (s){
                    case "红队":
                        red++;
                        break;
                    case "蓝队":
                        blue++;
                        break;
                    case "黄队":
                        yellow++;
                        break;
                }
            }

            if(red%2==0&&blue%2==0&&yellow%2==0){
                canStateGame=true;
            }
        }

        return canStateGame;
    }

    //主动更换队伍
    public void initiativeSetTeam(String s,ClientClass clientClass){
        Room room=clientClass.getAtRoom();
        String teamName=room.getTeam(s);
        System.out.println("teamName="+teamName);
        if(!teamName.equals("null")){
            room.giveBackTeam(clientClass.getTeam());
            clientClass.setTeam(teamName);
        }
    }

}
