package com.exp.narang.api.response;

import com.exp.narang.api.model.RoomDto;
import com.exp.narang.common.model.response.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**유저 로그인 API ([POST] /api/v1/auth) 요청에 대한 응답값 정의. */
@Getter @Setter @ApiModel("RoomRegistPostResponse")
public class RoomListRes extends BaseResponseBody {
	@ApiModelProperty(name="게임방 번호(PK)", example="1") Page<RoomDto> roomList;

	public static RoomListRes of(Integer statusCode, String message, Page<RoomDto> roomList) {
	RoomListRes res = new RoomListRes();
	res.setStatusCode(statusCode);
	res.setMessage(message);
	res.setRoomList(roomList);
	return res;
} }
