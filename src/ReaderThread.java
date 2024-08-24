import java.io.*;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReaderThread implements Runnable{
    Socket socket;
    ClientClass clientClass;

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
                sb.append(clientClass.name+"/n"+clientClass.zhanghao+"/n"+clientClass.xinbie+"/n"+clientClass.image+"/n"+clientClass.onLine);
                break;
            }
            sb.append(clientClass.name+"/n"+clientClass.zhanghao+"/n"+clientClass.xinbie+"/n"+clientClass.image+"/n"+clientClass.onLine+"/n");
        }
        return sb.toString();
    }
    private String roomListString(ArrayList<Room> list){
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
                        clientClass=GongGongZiYuan.clients.get(postion);
                        if(!GongGongZiYuan.isLogin.contains(clientClass)){
                            for(ClientClass clientClass1:GongGongZiYuan.isLogin){
                                System.out.println("已登录"+clientClass1.zhanghao);
                            }
                            clientClass.setOnLine(true);
                            clientClass.setLocation("还未进入选择大厅");
                            GongGongZiYuan.isLogin.add(clientClass);
                            clientClass.socket=this.socket;

                            gongGongZiYuan.setHaoYouList(clientClass.haoyouList);
                            System.out.println("onLine="+clientClass.onLine);
                            os.write(("setClient:/n"+clientClass.name+"/n"+clientClass.zhanghao+"/n"+clientClass.xinbie+"/n"+clientClass.image+"/n"+clientClass.onLine+"_").getBytes());
                            os.write(("denglu:/n欢迎回来，"+clientClass.name+"!_").getBytes());
                            os.write("OK_".getBytes());

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

                    clientClass.setLocation("在选择大厅");
                    os.write(("dating:/n" + DatinString() + "_").getBytes());
                    os.write(("ziliao:/n" + "_").getBytes());

                    os.write(("setHaoYouList:/n"+getClientString(gongGongZiYuan.beingChlentTiTheFront(clientClass.haoyouList))+"_").getBytes());
                    gongGongZiYuan.setHaoYouList(clientClass.haoyouList);
                    gongGongZiYuan.sendTSSiXin(clientClass.name);
                    break;
                case "jinrudating:":
                    System.out.println("Server:------"+"jinrudating:/n"+strings[1]+"_");
                    nowAtHall = Integer.parseInt(strings[1]);
                    clientClass.setNowAtHall(nowAtHall);
                    GongGongZiYuan.dating[nowAtHall] = GongGongZiYuan.dating[nowAtHall] + 1;
                    GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall).add(clientClass);
                    os.write(("jinrudating:/n"+nowAtHall+"_").getBytes());
                    System.out.println("Server:"+"jinrudating:/n"+nowAtHall+"_");
                    os.write(("dating:/n" + DatinString()+ "_").getBytes());
                    System.out.println(clientClass.name+"进来了");
                    clientClass.setLocation("在大厅"+nowAtHall);
                    break;

                case "4-5:":
                    GongGongZiYuan.onLineClients.get(nowAtHall).add(clientClass);
                    gongGongZiYuan.allSocketSend("datingClient:/n"+getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_",
                            GongGongZiYuan.onLineClients.get(nowAtHall));
                    System.out.println(GongGongZiYuan.onLineClients.get(nowAtHall).size()+":"+"datingClient:/n"+
                            getClientString(GongGongZiYuan.onLineClients.get(nowAtHall))+"_");
                    gongGongZiYuan.allSocketSend("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));
                    System.out.println("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_"+GongGongZiYuan.datingListRoomList.get(nowAtHall).size());

                    os.write(("setHaoYouList:/n"+getClientString(gongGongZiYuan.beingChlentTiTheFront(clientClass.haoyouList))+"_").getBytes());
                    gongGongZiYuan.setHaoYouList(clientClass.haoyouList);
                    gongGongZiYuan.allSocketSend("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));
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
                    gongGongZiYuan.outDating(clientClass);
                    break;
                case "liaotianxiaoxi:":
                    System.out.println(s);
                    if(strings[1].substring(0,1).equals("@")){
                        //data=InTheRoomliaotianxiaoxi:/n"+"@name:data+"/n0"+"_";
                        String[] strings1=strings[1].split(":");
                        if(strings1.length==2){
                            System.out.println("name="+strings1[0].substring(1));
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"InTheRoomliaotianxiaoxi:/n"+"[私]"+clientClass.name+":"+strings1[1]+"/n"+strings[2]+"_");
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"dating"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))).nowAtHall+":/n"+"[私]"+clientClass.name+":"+strings1[1]+"/n"+strings[2]+"_");

                            os.write(("dating"+nowAtHall+":/n"+"[私]"+clientClass.name+":"+strings1[0]+":"+strings1[1]+"/n"+strings[2]+"_").getBytes());

                        }else{
                            gongGongZiYuan.allSocketSend("dating"+nowAtHall+":"+"/n[公]"+clientClass.name+":"+strings[1]+"/n"+strings[2]+"_",room.clientClasses);
                        }

                    }else {
                        gongGongZiYuan.allSocketSend("dating" + nowAtHall + ":" + "/n[公]" + clientClass.name + ":" + strings[1] + "/n" + strings[2] + "_", GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall));
                        System.out.println("dating" + nowAtHall + ":" + "/n" + clientClass.name + ":" + strings[1] + "/n" + strings[2] + "_");
                    }
                    break;

                case "createRoom:":
                    System.out.println("createRoom");
                    createRoom(strings);
                    gongGongZiYuan.allSocketSend("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));

                    os.write(("5-6:/n"+"_").getBytes());
                    System.out.println("5-6");
                    break;

                case "buttonJinru:":
                    System.out.println(s+"="+strings.length);
                    os.write(("5-6:/n"+"_").getBytes());
                    room=getHaoMaRoom(Integer.parseInt(strings[1]));

                    break;

                case"ClientFollowRoom:":
                    os.write(("5-6:/n"+"_").getBytes());
                    room=getHaoMaRoom(Integer.parseInt(strings[1]));
                    break;
                case "jinruRoom:":
                    room.clientClasses.add(clientClass);
                    clientClass.atRoom=room;
                    GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall).remove(clientClass);
                    //                                                                                                         房间号
                    gongGongZiYuan.allSocketSend("setInTheRoomClient:/n"+getClientString(room.clientClasses)+"_",room.clientClasses);
                    gongGongZiYuan.allSocketSend("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));
                    os.write(("setRoom:/n"+room.roomName+"/n"+room.roomAdmin+"/n"+room.roomType+"/n"+room.roomHaoMa+"_").getBytes());
                    clientClass.setLocation("在大厅"+nowAtHall+"中的["+room.roomHaoMa+"]"+room.roomName);

                    os.write(("setHaoYouList:/n"+getClientString(gongGongZiYuan.beingChlentTiTheFront(clientClass.haoyouList))+"_").getBytes());
                    gongGongZiYuan.setHaoYouList(clientClass.haoyouList);
                    break;

                case "InTheRoomliaotianxiaoxi:":

                    System.out.println("InTheRoomliaotianxiaoxi:data="+s);
                    if(strings[1].substring(0,1).equals("@")){
                        //data=InTheRoomliaotianxiaoxi:/n"+"@name:data+"/n0"+"_";
                        String[] strings1=strings[1].split(":");
                        if(strings1.length==2){
                            System.out.println("name="+strings1[0].substring(1));
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"InTheRoomliaotianxiaoxi:/n"+"[私]"+clientClass.name+":"+strings1[1]+"/n"+strings[2]+"_");
                            gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))),"dating"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings1[0].substring(1))).nowAtHall+":/n"+"[私]"+clientClass.name+":"+strings1[1]+"/n"+strings[2]+"_");

                            os.write(("InTheRoomliaotianxiaoxi:/n"+"[私]"+clientClass.name+":"+strings1[0]+":"+strings1[1]+"/n"+strings[2]+"_").getBytes());

                        }else{
                            gongGongZiYuan.allSocketSend("InTheRoomliaotianxiaoxi:/n[公]"+clientClass.name+":"+strings[1]+"/n"+strings[2]+"_",room.clientClasses);
                                                }
                    }else {
                        gongGongZiYuan.allSocketSend("InTheRoomliaotianxiaoxi:/n[公]"+clientClass.name+":"+strings[1]+"/n"+strings[2]+"_",room.clientClasses);
                    }
                    break;

                case "tuichuRoom:":
//                    room.clientClasses.remove(clientClass);
//                    clientClass.atRoom=null;
//                    if(room.clientClasses.isEmpty()){
//                        GongGongZiYuan.datingListRoomList.get(nowAtHall).remove(room);
//                    }
//                    gongGongZiYuan.allSocketSend("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_",GongGongZiYuan.onLineClients.get(nowAtHall));
//                    System.out.println("setRoomList:/n"+roomListString(GongGongZiYuan.datingListRoomList.get(nowAtHall))+"_"+GongGongZiYuan.datingListRoomList.get(nowAtHall).size());
//                    gongGongZiYuan.allSocketSend("setInTheRoomClient:/n"+getClientString(room.clientClasses)+"_",room.clientClasses);
//                    GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall).add(clientClass);
//                    gongGongZiYuan.allSocketSend("setyaoqingList:/n"+getClientString(GongGongZiYuan.atDatingOutOfRoom.get(nowAtHall))+"_",room.clientClasses);
//                    clientClass.setLocation("在大厅"+nowAtHall);
                    gongGongZiYuan.outRoom(clientClass);
                    break;

                case "wanquantuichu:":
                    clientClass.onLine=false;
                    System.out.println("完全退出！");
                    os.write("tuichuyouxi:/n退出游戏_".getBytes());
                    GongGongZiYuan.isLogin.remove(clientClass);
                    for(ClientClass clientClass1:GongGongZiYuan.isLogin){
                        System.out.println("已经登陆"+clientClass1.zhanghao);
                    }
                    GongGongZiYuan.clientSockets.remove(socket);
                    for(Socket socket1:GongGongZiYuan.clientSockets){
                        System.out.println("现有socket:"+socket1);
                    }
                    System.out.println("现存Socket:"+GongGongZiYuan.clientSockets.size());
                    socket.close();
                    break;
                case"ClientAddFriend:":
                    gongGongZiYuan.sendOne(clientClass.atRoom.clientClasses.get(Integer.parseInt(strings[1])),
                            "serverAddFriend:/n"+clientClass.name+"/n"+clientClass.zhanghao+"_");
                    break;

                case"ClientRogerTwoAgree:":
                    System.out.println("oneRoger:"+clientClass.zhanghao+"/+"+strings[1]);
                    io.outputFile(clientClass.zhanghao+"的好友.txt",strings[1]+"/n",true);
                    clientClass.haoyouList.add(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])));

                    os.write(("setHaoYouList:/n"+getClientString(gongGongZiYuan.beingChlentTiTheFront(clientClass.haoyouList))+"_").getBytes());

                    break;

                case"ClientTwoAgree:":
                    System.out.println("twoAgree:"+clientClass.zhanghao+"/+"+strings[1]);
                    io.outputFile(clientClass.zhanghao+"的好友.txt",strings[1]+"/n",true);
                    clientClass.haoyouList.add(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])));
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),
                            "ServerTwoAgree:/n"+clientClass.getZhanghao()+"_");

                    os.write(("setHaoYouList:/n"+getClientString(gongGongZiYuan.beingChlentTiTheFront(clientClass.haoyouList))+"_").getBytes());
                    break;


                case"ClientTwoRefuse:":
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),
                            "ServerTwoRefuse:"+"_");
                    break;

                case"ClientZiLiao:": {
                    ClientClass client = GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1]));
                    os.write(("ServerZiLiao:/n" + client.name + "/n" + client.onLine + "/n" + client.getNowAtHall() + "/n"
                            + client.getRoomHaoMa() + "/n" + client.getRoomName() + "/n" + client.getRoomType() + "/n" + client.isRoomAdmin() + "/n" + nowAtHall + "_").getBytes());

                }break;

                case "ClientSiLiao:":
                    os.write(("ServerSiLiao:/n"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])).name+"_").getBytes());
                    break;

                case"ClientYaoQin:":
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])),"ServerYaoQin:/n"+
                            clientClass.name+"/n"+clientClass.atRoom.roomHaoMa+"/n"+clientClass.atRoom.roomName+"_");
                    break;

                case "ClientTwoRefuseYaoQin:":
                    gongGongZiYuan.sendOne(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings[1])),
                            "ServerTwoRefuseYaoQin:/n"+clientClass.name+"_");
                    break;
                case"ClientSiXin:":
                    os.write(("ServerSiXin:/n"+GongGongZiYuan.clients.get(gongGongZiYuan.getClientPostion(strings[1])).name+"/n_").getBytes());
                    break;
                case"ClientSendSiXin:":
                    gongGongZiYuan.sendSiXin(GongGongZiYuan.clients.get(gongGongZiYuan.getClientNamePostion(strings[1])),clientClass.name,strings[2],strings[3]);
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
            if(clientClass.zhanghao.equals(zhanghao)&&clientClass.admin.equals(admin)){
                postion=i;
                break;
            }
        }
        return postion;
    }

    private boolean isRepetitionZhanghao(String newZhanghao){
        boolean b=false;
        for(int i=0;i<GongGongZiYuan.clients.size();i++){
            if(GongGongZiYuan.clients.get(i).zhanghao.equals(newZhanghao)){
                b=true;
                break;
            }
        }
        return b;
    }

    private Room getHaoMaRoom(int HaoMa){
        for (Room room1:GongGongZiYuan.datingListRoomList.get(nowAtHall)){
            if(room1.roomHaoMa==HaoMa){
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
