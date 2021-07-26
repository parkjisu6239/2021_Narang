package com.exp.narang.api.response;

import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.Room;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 유저 로그인 API ([POST] /api/v1/auth) 요청에 대한 응답값 정의.
 */
@Getter
@Setter
@ApiModel("RoomRegistPostResponse")
public class RoomListRes extends BaseResponseBody {
	@ApiModelProperty(name="게임방 번호(PK)", example="1")
	List<Room> roomList;
	
	public static RoomListRes of(Integer statusCode, String message, List<Room> roomList) {
		RoomListRes res = new RoomListRes();
		res.setStatusCode(statusCode);
		res.setMessage(message);
		res.setRoomList(roomList);
		return res;
	}
}
