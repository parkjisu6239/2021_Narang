package com.exp.narang.api.response;

import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.User;
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
@ApiModel("Search Room Join UserList")
public class RoomJoinUserListRes extends BaseResponseBody {
	@ApiModelProperty(name="게임방 번호(PK)", example="1")
	 List<User> userList;
	
	public static RoomJoinUserListRes of(Integer statusCode, String message, List<User> userList) {
		RoomJoinUserListRes res = new RoomJoinUserListRes();
		res.setStatusCode(statusCode);
		res.setMessage(message);
		res.setUserList(userList);
		return res;
	}
}
