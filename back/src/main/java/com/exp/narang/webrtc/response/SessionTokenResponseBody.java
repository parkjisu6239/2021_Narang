package com.exp.narang.webrtc.response;

import com.exp.narang.common.model.response.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("UserResponse")
public class SessionTokenResponseBody extends BaseResponseBody {
    @ApiModelProperty(name = "세션의 사용자 구분을 위한 토큰", example = "owqhepgo3wirg04liuho...")
    String token;

    public static SessionTokenResponseBody of(Integer statusCode, String message, String token){
        SessionTokenResponseBody res = new SessionTokenResponseBody();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setToken(token);
        return res;
    }
}
