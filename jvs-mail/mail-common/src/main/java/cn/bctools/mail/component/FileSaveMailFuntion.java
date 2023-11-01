package cn.bctools.mail.component;


import java.io.InputStream;

/**
 * @author admin
 * @ClassName: FileSaveMailFuntion
 * @Description: 文件保存回调

 */
public interface FileSaveMailFuntion {
    /**
     * 回调保存文件
     *
     * @param in       回调的流
     * @param fileName 文件名字

     **/
    void saveFile(String fileName, Integer type, InputStream in, String cid) throws Exception;
}
