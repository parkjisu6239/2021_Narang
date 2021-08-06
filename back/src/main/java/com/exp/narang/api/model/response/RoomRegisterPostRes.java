package com.exp.narang.api.model.response;

import com.exp.narang.common.model.response.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 로그인 API ([POST] /api/v1/auth) 요청에 대한 응답값 정의.
 */
@Getter
@Setter
@ApiModel("RoomRegistPostResponse")
public class RoomRegisterPostRes extends BaseResponseBody {
	@ApiModelProperty(name="게임방 번호(PK)", example="1")
	Long roomId;
	
	public static RoomRegisterPostRes of(Integer statusCode, String message, Long roomId) {
		RoomRegisterPostRes res = new RoomRegisterPostRes();
		res.setStatusCode(statusCode);
		res.setMessage(message);
		res.setRoomId(roomId);
		return res;
	}
}
