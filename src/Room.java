import java.util.ArrayList;

public class Room {
    String roomName,roomType,roomAdmin;
    int roomHaoMa;
    ArrayList<ClientClass> clientClasses=new ArrayList();
    public Room(String name,String type,String admin,int roomHaoMa){
        this.roomAdmin=admin;
        this.roomName=name;
        this.roomType=type;
        this.roomHaoMa=roomHaoMa;
    }

    public String getRoomName(){
        return roomName;
    }
    public String getRoomType(){
        return roomType;
    }

    public String getRoomAdmin(){
        return roomAdmin;
    }

    public int getRoomHaoMa(){
        return roomHaoMa;
    }
    
}
