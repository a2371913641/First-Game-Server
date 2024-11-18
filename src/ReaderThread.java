import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ReaderThread implements Runnable{
    Socket socket;

    ClientClass myClientClass;
    IOUtil io=new IOUtil();
    GongGongZiYuan gongGongZiYuan=new GongGongZiYuan();

    int nowAtHall=-1;

    Room room;
    public ReaderThread(Socket socket){
        super();
        this.socket=socket;
    }

    private String DatinString(){
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

    private String getClientString(ArrayList<ClientClass> clientClasses){
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
    private String roomListString(ArrayList<Room> list){
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
    @Override
    public void run() {
        try {
            InputStream is=socket.getInputStream();
            byte[] buff=new byte[1024];
            while (true){
                int len = is. read(buff);
                if(len>0){
                    String data=new String(buff, 0, len);
                    System.out.println("data="+data);
                    String[] ss=data.split("_");
                    for(String s:ss){
                        System.out.println("chulixiaoxi:"+s);
                        chulixiaoxi(s);
                    }

                } else if (len < 0) {
                    break;
                }

            }
        } catch (IOException e) {
            GongGongZiYuan.clientSockets.remove(socket);
        }

    }

    private void chulixiaoxi(String s)  {
        OutputStream os= null;
        try {
            os = socket.getOutputStream();
            System.out.println("s="+s);
            String[] strings=s.split("/n");
            switch (strings[0]) {
                case "login:":
                    System.out.println(s);//login:/n+zhanghao/n+admin
                    if(strings.length==3){
                    int postion=getClientPostion(strings[1],strings[2]);
                    if(postion!=-1) {
                        myClientClass=GongGongZiYuan.clients.get(postion);
                        if(!GongGongZiYuan.isLogin.contains(myClientClass)){
                            for(ClientClass clientClass1:GongGongZiYuan.isLogin){
                                System.out.println("已登录"+clientClass1.getZhanghao());
                            }
                            myClientClass.setOnLine(true);
                            myClientClass.setLocation("还未进入选择大厅");
                            GongGongZiYuan.isLogin.add(myClientClass);
                            myClientClass.socket=this.socket;

                            gongGongZiYuan.setClientsIsHaoYouList(myClientClass.haoyouList);
                            System.out.println("onLine="+myClientClass.onLine);
                            os.write(("setClient:/n"+myClientClass.getName()+"/n"+myClientClass.getZhanghao()+"/n"+myClientClass.getXinbie()+"/n"+myClientClass.image+"/n"+myClientClass.onLine+"_").getBytes());
                            os.write(("denglu:/n欢迎回来，"+myClientClass.getName()+"!_").getBytes());
                            os.write("OK_".getBytes());
                            for(Socket socket1:GongGongZiYuan.clientSockets){
                                System.out.println(socket1.hashCode());
                            }
                            System.out.println("Sockets="+GongGongZiYuan.clientSockets.size());
                            System.out.println("OK");
                        }else {
                            os.write(("Notdenglu:/n该玩家已在游戏内!_").getBytes());
                            GongGongZiYuan.clientSockets.remove(socket);
                            socket.close();
                        }
                    }else {
                        os.write(("Notdenglu:/n账号或密码错误!_").getBytes());
                        GongGongZiYuan.clientSockets.remove(socket);
                        socket.close();
                    }
                    }else {
                        os.write(("Notdenglu:/n账号或密码错误!_").getBytes());
                        GongGongZiYuan.clientSockets.remove(socket);
                        socket.close();
                    }
                    break;
                case "zhuce:":
                    System.out.println(s);
//                  zhuce/n姓名/n账号/n密码/n性别/nImage
                    if(isRepetitionZhanghao(strings[2])){
                        os.write(("zhucejieguo:/n"+"注册失败，该账号已存在!_").getBytes());
                    }else{
                        io.outputFile("AllClientZhanghao.txt",strings[2]+"/n",true);
                        //String zhanghao,String admin,String name,String xb
                        io.outputFile(strings[2]+"ziliao.txt",strings[2]+"/n"+strings[3]+"/n"+strings[1]+"/n"+strings[4]+"/n"+strings[5],false);
                        GongGongZiYuan.clients.add(new ClientClass(strings[2],strings[3],strings[1],strings[4],Integer.parseInt(strings[5]),false));
                        os.write(("zhucejieguo:/n"+"注册成功!_").getBytes());
                    }
                    GongGongZiYuan.clientSockets.remove(socket);
                    socket.close();
                    break;
                case "3-4":
                    System.out.println(s);
                    os.write("3-4_".getBytes());
                    System.out.println("send3-4");
                    break;
                case "fourlyActivityOK":
                    System.out.println(s);

                    myClientClass.setLocation("在选择大厅");
                    os.write(("dating:/n" + DatinString() + "_").getBytes());
                    os.write(("ziliao:/n" + "_").getBytes());

                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);
                    gongGongZiYuan.setClientsIsHaoYouList(myClientClass.haoyouList);
                    gongGongZiYuan.sendTSSiXin(myClientClass.getName());
                    break;
                case "jinrudating:":
                    System.out.println("Server:------"+"jinrudating:/n"+strings[1]+"_");
                    nowAtHall = Integer.parseInt(strings[1]);
                    myClientClass.setNowAtHall(nowAtHall);
                    GongGongZiYuan.dating[nowAtHall] = GongGongZiYuan.dating[nowAtHall] + 1;
                    GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall).add(myClientClass);
                    os.write(("jinrudating:/n"+nowAtHall+"_").getBytes());
                    System.out.println("Server:"+"jinrudating:/n"+nowAtHall+"_");
                    os.write(("dating:/n" + DatinString()+ "_").getBytes());
                    System.out.println(myClientClass.getName()+"进来了");
                    myClientClass.setLocation("在大厅"+nowAtHall);
                    break;

                case "4-5:":
                    GongGongZiYuan.onLineClients.get(nowAtHall).add(myClientClass);
                    gongGongZiYuan.allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_",
                            GongGongZiYuan.onLineClients.get(nowAtHall));
                    System.out.println(GongGongZiYuan.onLineClients.get(nowAtHall).size()+":"+"datingClient:/n"+
                            getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_");
                    gongGongZiYuan.resetDatingRoomList(nowAtHall);

                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);
                    gongGongZiYuan.setClientsIsHaoYouList(myClientClass.haoyouList);
                    gongGongZiYuan.resetYaoqingList(nowAtHall);
                    break;
                case "tuichudating:":
//                    GongGongZiYuan.dating[nowAtHall] = GongGongZiYuan.dating[nowAtHall] - 1;
//                    GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
//                    GongGongZiYuan.onLineClients.get(nowAtHall).remove(clientClass);
//                    gongGongZiYuan.allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_",
//                            GongGongZiYuan.onLineClients.get(nowAtHall));
//                    os.write(("dating:/n" + DatinString()+ "_").getBytes());
//                    nowAtHall=-1;
//                    clientClass.setNowAtHall(nowAtHall);
//                    clientClass.setLocation("在选择大厅");
                    gongGongZiYuan.outDating(myClientClass);
                    break;
                case "liaotianxiaoxi:":
                    System.out.println(s);
                    if(strings[1].substring(0,1).equals("@")){
                        //data=InTheRoomliaotianxiaoxi:/n"+"@name:data+"/n0"+"_";
                        String[] strings1=strings[1].split(":");
                        if(strings1.length==2){
                            System.out.println("name="+strings1[0].substring(1));
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"InTheRoomliaotianxiaoxi:/n"+"[私]"+myClientClass.getName()+":"+strings1[1]+"/n"+strings[2]+"_");
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"dating"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))).nowAtHall+":/n"+"[私]"+myClientClass.getName()+":"+strings1[1]+"/n"+strings[2]+"_");

                            os.write(("dating"+nowAtHall+":/n"+"[私]"+myClientClass.getName()+":"+strings1[0]+":"+strings1[1]+"/n"+strings[2]+"_").getBytes());

                        }else{
                            gongGongZiYuan.allSocketSend("dating"+nowAtHall+":"+"/n[公]"+myClientClass.getName()+":"+strings[1]+"/n"+strings[2]+"_",room.clientClasses);
                        }

                    }else {
                        gongGongZiYuan.allSocketSend("dating" + nowAtHall + ":" + "/n[公]" + myClientClass.getName() + ":" + strings[1] + "/n" + strings[2] + "_", GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall));
                        System.out.println("dating" + nowAtHall + ":" + "/n" + myClientClass.getName() + ":" + strings[1] + "/n" + strings[2] + "_");
                    }
                    break;

                case "createRoom:":
                    System.out.println("createRoom");
                    createRoom(strings);
                   gongGongZiYuan.resetDatingRoomList(nowAtHall);
                    os.write(("5-6:/n"+"_").getBytes());
                    System.out.println("5-6");
                    myClientClass.setClientState("房主");

                    break;

                case "buttonJinru:":
                    System.out.println(s+"="+strings.length);
                    os.write(("5-6:/n"+"_").getBytes());
                    room=getHaoMaRoom(Integer.parseInt(strings[1]));
                    myClientClass.setClientState("未准备");
                    break;

                case"ClientFollowRoom:":
                    os.write(("5-6:/n"+"_").getBytes());
                    room=getHaoMaRoom(Integer.parseInt(strings[1]));
                    myClientClass.setClientState("未准备");
                    break;
                case "jinruRoom:":
                    System.out.println("jinruRoom!!!!");
                    gongGongZiYuan.setFangZhu(room);
                    gongGongZiYuan.jinruRoom(room,myClientClass);
                    System.out.println("gongGongZiYuan.jinruRoom(room,myClientClass);OK");
                    myClientClass.setAtRoom(room);
                    String team=room.getTeam();
                    myClientClass.setTeam(team);
                    System.out.println("Team="+team);
                    GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall).remove(myClientClass);
                    gongGongZiYuan.resetRoomNumberOfPeople(room);
                    gongGongZiYuan.resetYaoqingList(nowAtHall);
                    os.write(("setRoom:/n"+room.getRoomName()+"/n"+room.getRoomAdmin()+"/n"+room.getRoomType()+"/n"+room.getRoomHaoMa()+"_").getBytes());
                    os.write(("ServerClientState:/n"+myClientClass.clientState+"_").getBytes());
                    myClientClass.setLocation("在大厅"+nowAtHall+"中的["+room.getRoomHaoMa()+"]"+room.getRoomName());
                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);
                    gongGongZiYuan.setClientsIsHaoYouList(myClientClass.haoyouList);
                    break;

                case "InTheRoomliaotianxiaoxi:":

                    System.out.println("InTheRoomliaotianxiaoxi:data="+s);
                    if(strings[1].substring(0,1).equals("@")){
                        //data=InTheRoomliaotianxiaoxi:/n"+"@name:data+"/n0"+"_";
                        String[] strings1=strings[1].split(":");
                        if(strings1.length==2){
                            System.out.println("name="+strings1[0].substring(1));
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"InTheRoomliaotianxiaoxi:/n"+"[私]"+myClientClass.getName()+":"+strings1[1]+"/n"+strings[2]+"_");
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"dating"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))).nowAtHall+":/n"+"[私]"+myClientClass.getName()+":"+strings1[1]+"/n"+strings[2]+"_");

                            os.write(("InTheRoomliaotianxiaoxi:/n"+"[私]"+myClientClass.getName()+":"+strings1[0]+":"+strings1[1]+"/n"+strings[2]+"_").getBytes());

                        }else{
                            gongGongZiYuan.allSocketSend("InTheRoomliaotianxiaoxi:/n[公]"+myClientClass.getName()+":"+strings[1]+"/n"+strings[2]+"_",room.clientClasses);
                                                }
                    }else {
                        gongGongZiYuan.allSocketSend("InTheRoomliaotianxiaoxi:/n[公]"+myClientClass.getName()+":"+strings[1]+"/n"+strings[2]+"_",room.clientClasses);
                    }
                    break;

                case "tuichuRoom:":
//
                    myClientClass.setClientState(null);
                    gongGongZiYuan.outRoom(myClientClass);
                    break;

                case "wanquantuichu:":
                    myClientClass.onLine=false;
                    System.out.println("完全退出！");
                    os.write("tuichuyouxi:/n退出游戏_".getBytes());
                    GongGongZiYuan.isLogin.remove(myClientClass);
                    for(ClientClass clientClass1:GongGongZiYuan.isLogin){
                        System.out.println("已经登陆"+clientClass1.getZhanghao());
                    }
                    GongGongZiYuan.clientSockets.remove(socket);
                    for(Socket socket1:GongGongZiYuan.clientSockets){
                        System.out.println("现有socket:"+socket1);
                    }
                    System.out.println("现存Socket:"+GongGongZiYuan.clientSockets.size());
                    socket.close();
                    break;
                case"ClientAddFriend:":
                    gongGongZiYuan.sendOne(gongGongZiYuan.getSeatClient(room,Integer.parseInt(strings[1])),
                            "serverAddFriend:/n"+myClientClass.getName()+"/n"+myClientClass.getZhanghao()+"_");
                    break;

                case"ClientDeleteFriend:":
                    gongGongZiYuan.deleteFriend(myClientClass,strings[1]);
                    gongGongZiYuan.deleteFriend(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),myClientClass.getZhanghao());
                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),"ReadFromFileHaoYouList:_");
                    break;
                case "ReadFromFileHaoYouList:":
                    gongGongZiYuan.readOneClientFromAFileHaoYouList(myClientClass);
                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);
                    System.out.println("ReadFromFileHaoYouList:"+myClientClass.haoyouList.size());
                    break;

                case"ClientRogerTwoAgree:":
                    System.out.println("oneRoger:"+myClientClass.getZhanghao()+"/+"+strings[1]);
                    io.outputFile(myClientClass.getZhanghao()+"的好友.txt",strings[1]+"/n",true);
                    myClientClass.haoyouList.add(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])));

                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);

                    break;

                case"ClientTwoAgree:":
                    System.out.println("twoAgree:"+myClientClass.getZhanghao()+"/+"+strings[1]);
                    io.outputFile(myClientClass.getZhanghao()+"的好友.txt",strings[1]+"/n",true);
                    myClientClass.haoyouList.add(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])));
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),
                            "ServerTwoAgree:/n"+myClientClass.getZhanghao()+"_");
                    gongGongZiYuan.callClientMyselfHaoYouList(os,myClientClass);
                    break;


                case"ClientTwoRefuse:":
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),
                            "ServerTwoRefuse:"+"_");
                    break;

                case"ClientZiLiao:":
                    ClientClass client = GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1]));
                    os.write(("ServerZiLiao:/n" + client.getName() + "/n" + client.onLine + "/n" + client.getNowAtHall() + "/n"
                            + client.getRoomHaoMa() + "/n" + client.getRoomName() + "/n" + client.getRoomType() + "/n" + client.isRoomAdmin() + "/n"+client.getClientState()+ "_").getBytes());
                    break;

                case "ClientSiLiao:":
                    os.write(("ServerSiLiao:/n"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])).getName()+"_").getBytes());
                    break;

                case"ClientYaoQin:":
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),"ServerYaoQin:/n"+
                            myClientClass.getName()+"/n"+myClientClass.getAtRoom().getRoomHaoMa()+"/n"+myClientClass.getAtRoom().getRoomName()+"_");
                    break;

                case "ClientTwoRefuseYaoQin:":
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings[1])),
                            "ServerTwoRefuseYaoQin:/n"+myClientClass.getName()+"_");
                    break;
                case"ClientSiXin:":
                    os.write(("ServerSiXin:/n"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])).getName()+"/n_").getBytes());
                    break;
                case"ClientSendSiXin:":
                    gongGongZiYuan.sendSiXin(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings[1])),myClientClass.getName(),strings[2],strings[3]);
                    break;

                case"ClientStartGame:":
                    if(gongGongZiYuan.isCanStateGame(room)){
                        for(ClientClass clientClass:room.clientClasses){
                            clientClass.setClientState("游戏中");
                        }
                        gongGongZiYuan.resetRoomClientState(room);
                        gongGongZiYuan.allSocketSend("ServerStartGame:_",myClientClass.getAtRoom().clientClasses);
                    }else{
                        os.write(("ServerStopGameFromStarting:/n游戏人数不满足_").getBytes());

                    }

                    break;

                case "ClientReturnRoom:":
                     System.out.println("myClientClass.getSeat()="+myClientClass.getSeat()+";room.getFangZhu()="+room.getFangZhu());
                     if(myClientClass.getSeat()!=room.getFangZhu()){
                         myClientClass.setClientState("未准备");
                     }else {
                         myClientClass.setClientState("房主");
                     }
                     gongGongZiYuan.resetRoomClientState(room);
                    break;
                case"ClientOnRoom:":
                    for(ClientClass clientClass:room.clientClasses){
                        if(clientClass.getSeat()==Integer.parseInt(strings[1])){
                            room.clientClasses.remove(clientClass);
                            break;
                        }
                    }
                    room.giveBackSeat(Integer.parseInt(strings[1]));
                    gongGongZiYuan.resetRoomNumberOfPeople(room);
                    break;

                case "ClientOffRoom:":
                    ClientClass nullClient=new ClientClass("null","null","null","off",0,false);
                    nullClient.setSeat(room.closeSeat(Integer.parseInt(strings[1])));
                    nullClient.setClientState("null");
                    room.clientClasses.add(nullClient);
                    gongGongZiYuan.resetRoomNumberOfPeople(room);
                    break;
                case"ClientResetRoomClientState:":
                    gongGongZiYuan.resetRoomClientState(room);
                    gongGongZiYuan.resetRoomNumberOfPeople(room);

                    System.out.println("ClientResetRoomClientState:+room.size="+room.clientClasses.size());
                    break;

                case"ClientCancelReserve:":
                    myClientClass.setClientState("未准备");
                    gongGongZiYuan.resetRoomNumberOfPeople(room);
                    gongGongZiYuan.resetRoomClientState(room);
                    break;

                case "ClientReserve:":
                    myClientClass.setClientState("已准备");
                    gongGongZiYuan.resetRoomNumberOfPeople(room);
                    gongGongZiYuan.resetRoomClientState(room);
                    break;
                case"ClientPlayChess:":
                    gongGongZiYuan.sendOne(gongGongZiYuan.getRivalClient(myClientClass),"ServerPlayChess:/n"+strings[1]+"/n"+strings[2]+"/n"+strings[3]+"_");
                    System.out.println(s);
                    break;
                case "ClientInitiativeSetTeam:":
                    gongGongZiYuan.initiativeSetTeam(strings[1],myClientClass);
                    gongGongZiYuan.resetRoomNumberOfPeople(room);
                    break;

                case"ClientGameOver:":
                    System.out.println(s);
                    gongGongZiYuan.sendOne(myClientClass,"ServerGameOver:/n"+strings[1]+"胜利！"+"_");
                    System.out.println("ServerGameOver:/n"+strings[1]+"胜利！"+"_");
                    gongGongZiYuan.sendOne(gongGongZiYuan.getRivalClient(myClientClass),"ServerGameOver:/n"+strings[1]+"胜利！"+"_");
                    break;

                default:
                    System.out.println(s);
            }

        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                GongGongZiYuan.clientSockets.remove(socket);
            }
        }


    }

    private int getClientPostion(String zhanghao,String admin){
        int postion=-1;
        for(int i=0;i<GongGongZiYuan.clients.size();i++){
            ClientClass clientClass=GongGongZiYuan.clients.get(i);
            if(clientClass.getZhanghao().equals(zhanghao)&&clientClass.getAdmin().equals(admin)){
                postion=i;
                break;
            }
        }
        return postion;
    }

    private boolean isRepetitionZhanghao(String newZhanghao){
        boolean b=false;
        for(int i=0;i<GongGongZiYuan.clients.size();i++){
            if(GongGongZiYuan.clients.get(i).getZhanghao().equals(newZhanghao)){
                b=true;
                break;
            }
        }
        return b;
    }

    private Room getHaoMaRoom(int HaoMa){
        for (Room room1:GongGongZiYuan.datingListRoomList.get(nowAtHall)){
            if(room1.getRoomHaoMa()==HaoMa){
                return room1;
            }
        }
        return null;
    }
    private void createRoom(String[] strings){
        room=new Room(strings[1],strings[2],strings[3],gongGongZiYuan.getRoomHaoMa(nowAtHall));
        GongGongZiYuan.datingListRoomList.get(nowAtHall).add(room);
    }





//    private void
}
