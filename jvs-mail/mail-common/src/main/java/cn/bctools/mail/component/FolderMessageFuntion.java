package cn.bctools.mail.component;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * @author admin
 * @ClassName: FolderMessageFuntion
 * @Description: 查询回调接口
 */
public interface FolderMessageFuntion {
    /**
     * 搜索条件
     *
     * @param folder 查询条件
     * @return Message[] 邮件对象
     */
    Message[] search(Folder folder) throws MessagingException;
}
