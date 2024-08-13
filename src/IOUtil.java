

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class IOUtil {
    public void createFile(String FileName) {
        File file=new File(FileName);
        if(!file.exists()){
            try {
                if(file.createNewFile()){
                   System.out.println("创建文件成功");
                }else{
                    System.out.println("创建文件失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void deleteFile(String FileName) {
        File file = new File(FileName);
        if (file.delete()) {
           System.out.println("删除文件成功");
        } else {
           System.out.println("删除文件失败");
        }

    }

    public void renameFile(String oldName, String newName) {
        File oldFile = new File(oldName);
        File newFile = new File(newName);
        if (oldFile.renameTo(newFile)) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败 ");
        }

    }

    public void outputFile(String FileName, String content,boolean append) {
        System.out.println("fileName " + FileName);
        File file=new File(FileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fos=new FileOutputStream(FileName,append);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String inputFile(String FileName){
        File file=new File(FileName);
        if(!file.exists()){
            createFile(FileName);
        }

        StringBuilder sb=new StringBuilder();
        try {
            FileInputStream fis=new FileInputStream(FileName);
            byte[] bytes=new byte[1024];
            int hasRead;
            while ((hasRead=fis.read(bytes))>0){
                sb.append(new String(bytes,0,hasRead));
            }
            fis.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
