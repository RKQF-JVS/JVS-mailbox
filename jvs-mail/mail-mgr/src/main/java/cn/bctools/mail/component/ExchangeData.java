package cn.bctools.mail.component;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author admin
 * @ClassName: ExchangeData
 * @Description: 交换数据
 */
@Data
@Accessors(chain = true)
public class ExchangeData {
    /**
     * 事件
     */
    private String type;
    /**
     * 数据(json)
     */
    private String data;
}
