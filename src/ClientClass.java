import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientClass {
    String name,zhanghao,xinbie,admin;
    int image;
    Boolean onLine;
    List<ClientClass> haoyouList=new ArrayList<>();

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



    public void setImage(int i){
        this.image=i;
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
}
