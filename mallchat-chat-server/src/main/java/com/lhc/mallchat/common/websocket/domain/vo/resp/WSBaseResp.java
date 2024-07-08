package com.lhc.mallchat.common.websocket.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName WSBaseResp
 * @Description TODO
 * @Author Administrator
 * @Date 2024/6/4 20:16
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {

    /**
     * @see com.lhc.mallchat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;

    private T data;
}
