import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientClass {
    private String name,zhanghao,xinbie,admin;
    int image;
    Boolean onLine;
    ArrayList<ClientClass> haoyouList=new ArrayList<>();

    String location=null;

    //玩家在房间内的状态
    String clientState=null;

    String team=null;

    private Room atRoom;

    int atRoomPlace;

    int nowAtHall=-1;

    private int seat=-1;
    Socket socket;
    public ClientClass(String zhanghao,String admin,String name,String xb,int image,Boolean onLine){

        this.zhanghao=zhanghao;
        this.admin=admin;
        this.name=name;
        this.xinbie=xb;
        this.image=image;
        this.onLine=onLine;
    }

    public void setName(String name){
        this.name=name;

    }

    public String getAdmin(){
        return admin;
    }



    public void setImage(int i){
        this.image=i;
    }

    public void setLocation(String location){
        this.location=location;
    }

    public String getLocation(){
        return location;
    }

    public void setAdmin(String s){
        this.admin=s;
    }
    public String getZhanghao(){
        return zhanghao;
    }

    public String getXinbie(){
        return xinbie;
    }

    public String getName(){
        return name;
    }

    public int getImage(){
        return image;
    }

    public void setOnLine(Boolean onLine) {
        this.onLine = onLine;
    }

    public void setNowAtHall(int nowAtHall){
        this.nowAtHall=nowAtHall;
    }

    public String getNowAtHall(){
        if(nowAtHall==-1){
            return "null";
        }
        return nowAtHall+"";
    }

    public void setAtRoom(Room room){
        this.atRoom=room;

    }

    public Room getAtRoom(){
        return atRoom;
    }
    public String getRoomName(){
        if(atRoom==null){
            return "null";
        }
        return atRoom.getRoomName();
    }

    public String getRoomType(){
        if(atRoom==null){
            return "null";
        }
        return atRoom.getRoomType();
    }

    public String isRoomAdmin(){
        if(atRoom==null){
            return "null";
        }
        return !atRoom.getRoomAdmin().equals("/?")+"";
    }

    public String getRoomHaoMa(){
        if(atRoom==null){
            return "null";
        }
        return atRoom.getRoomHaoMa()+"";
    }

    public void setSeat(int i){
        this.seat=i;
    }

    public int getSeat(){
        return seat;
    }

    public void setClientState(String clientState){
        this.clientState=clientState;
    }

    public String getClientState(){
        return clientState;
    }

    public void setTeam(String s){
        if(!s.equals("null")){
            this.team=s;
        }
    }
    public void clearTeam(){
        this.team=null;
    }

    public String getTeam(){
        return this.team;
    }
}
