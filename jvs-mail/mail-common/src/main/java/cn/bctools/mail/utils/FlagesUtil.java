package cn.bctools.mail.utils;


import cn.bctools.mail.api.dto.UpdateMailStatusDTO;

import javax.mail.Flags;

/**
 * @author admin
 * @ClassName: FlagesUtil
 * @Description: 邮件标识
 */
public class FlagesUtil {
    private FlagesUtil() {
    }

    /**
     * 获取邮件标识
     *
     * @param flag 标记
     * @return javax.mail.Flags.Flag

     **/
    public static Flags.Flag getFlags(Integer flag) {
        switch (flag) {
            case UpdateMailStatusDTO.ANSWERED_BIT:
                return Flags.Flag.ANSWERED;
            case UpdateMailStatusDTO.DELETED_BIT:
                return Flags.Flag.DELETED;
            case UpdateMailStatusDTO.DRAFT_BIT:
                return Flags.Flag.DRAFT;
            case UpdateMailStatusDTO.FLAGGED_BIT:
                return Flags.Flag.FLAGGED;
            case UpdateMailStatusDTO.RECENT_BIT:
                return Flags.Flag.RECENT;
            case UpdateMailStatusDTO.SEEN_BIT:
                return Flags.Flag.SEEN;
            case UpdateMailStatusDTO.USER_BIT:
                return Flags.Flag.USER;
            default:
                return null;
        }
    }
}
