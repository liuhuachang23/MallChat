package com.lhc.mallchat.common.websocket.domain.vo.req;

import lombok.Data;

/**
 * @ClassName WSBaseReq
 * @Description TODO
 * @Author Administrator
 * @Date 2024/6/4 20:11
 * @Version 1.0
 */
@Data
public class WSBaseReq {

    /**
     * @see com.lhc.mallchat.common.websocket.domain.enums.WSReqTypeEnum
     */
    private Integer type;

    private String data;
}
