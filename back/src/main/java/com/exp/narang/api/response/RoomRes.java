package com.exp.narang.api.response;

import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.Room;
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
public class RoomRes extends BaseResponseBody {
	@ApiModelProperty(name="게임방 번호(PK)", example="1")
	Room room;
	
	public static RoomRes of(Integer statusCode, String message, Room room) {
		RoomRes res = new RoomRes();
		res.setStatusCode(statusCode);
		res.setMessage(message);
		res.setRoom(room);
		return res;
	}
}
