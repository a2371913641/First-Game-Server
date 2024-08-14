import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyListArray<T> extends ArrayList<T> {
    OutputStream os;
    GongGongZiYuan gongGongZiYuan=new GongGongZiYuan();
    public MyListArray(){

    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);

    }

    private void sendMsg(String s){
        for(MyListListener listListener:GongGongZiYuan.listListeners){
            listListener.onReceive(s);
        }
    }

}
