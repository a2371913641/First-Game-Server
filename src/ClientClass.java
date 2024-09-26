import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientClass {
    private String name,zhanghao,xinbie,admin;
    int image;
    Boolean onLine;
    ArrayList<ClientClass> haoyouList=new ArrayList<>();

    String location=null;

    private Room atRoom;

    int atRoomPlace;

    int nowAtHall=-1;

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
        return atRoom.roomName;
    }

    public String getRoomType(){
        if(atRoom==null){
            return "null";
        }
        return atRoom.roomType;
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
        return atRoom.roomHaoMa+"";
    }

}
