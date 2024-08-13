import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyListArray extends ArrayList {
    OutputStream os;
    GongGongZiYuan gongGongZiYuan=new GongGongZiYuan();
    public MyListArray(OutputStream os){
        this.os=os;
    }

    @Override
    public boolean add(Object o) {
        return super.add(o);

    }


}
