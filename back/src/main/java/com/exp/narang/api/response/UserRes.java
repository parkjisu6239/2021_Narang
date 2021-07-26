package com.exp.narang.api.response;

import com.exp.narang.db.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 본인 정보 조회 API ([GET] /api/v1/users/me) 요청에 대한 응답값 정의.
 */
@Getter
@Setter
@Builder
@ApiModel("UserResponse")
public class UserRes{
	@ApiModelProperty(name="User ID")
	String email;
	String username;
	String thumbnailUrl;
	byte[] fileArray;

	public static UserRes of(User user) {
		return UserRes.builder()
				.email(user.getEmail())
				.username(user.getUsername())
				.thumbnailUrl(user.getThumbnailUrl()).build();
	}
}
