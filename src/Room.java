import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {
    private String roomName,roomType,roomAdmin;
    private int roomHaoMa,fangZhu;

    private List<Integer> seats;
    private List<String> team=new ArrayList<>();
    ArrayList<ClientClass> clientClasses=new ArrayList();
    public Room(String name,String type,String admin,int roomHaoMa){
        this.roomAdmin=admin;
        this.roomName=name;
        this.roomType=type;
        this.roomHaoMa=roomHaoMa;
        Team();
        setSeats();
    }

    private void setSeats(){
        seats=new ArrayList<>(6);
        for(int i=0;i<6;i++){
            seats.add(i);
        }
    }

    private void Team(){
        for(int i=0;i<2;i++){
            team.add("红队");
        }
        for(int i=0;i<2;i++){
            team.add("黄队");
        }
        for(int i=0;i<2;i++){
            team.add("蓝队");
        }
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

    public void setFangZhu(int i){
        this.fangZhu=i;
    }

    public int getFangZhu(){
        return this.fangZhu;
    }

    public String getTeam(){
        System.out.println("team.size="+team.size());
        return team.remove(0);
    }

    public String getTeam(String s){
        for(int i=0;i<team.size();i++){
            if(s.equals(team.get(i))){
                return team.remove(i);
            }
        }
        return "null";
    }
    public void giveBackTeam(String s){
        team.add(s);
    }

    //返回Team列表剩余元素列表
    public List<String> getTeamSurplus(){
        return team;
    }

    public int getSeat(){
        Collections.sort(seats);
        System.out.println("seats.size="+seats.size());
        return seats.remove(0);
    }

    public void  giveBackSeat(int i){
        seats.add(i);
    }

    public int closeSeat(int seat){
        int closeSeat=-1;
        for(int i=0;i<seats.size();i++){
            if(seat==seats.get(i)){
                System.out.println("seats.size="+seats.size());
                System.out.println("closeSeat="+i);
                closeSeat= seats.remove(i);
                break;
            }
        }
        return closeSeat;
    }
}
