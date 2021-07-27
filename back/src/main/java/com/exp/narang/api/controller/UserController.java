package com.exp.narang.api.controller;

import com.exp.narang.api.request.UserInfoUpdateReq;
import com.exp.narang.api.request.UserRegisterPostReq;
import com.exp.narang.api.response.UserRes;
import com.exp.narang.api.service.UserService;
import com.exp.narang.common.auth.UserDetails;
import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.User;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 유저 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "유저 API", tags = {"User"})
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
	
	@Autowired
	UserService userService;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@PostMapping()
	@ApiOperation(value = "회원 가입", notes = "<strong>아이디와 패스워드</strong>를 통해 회원가입 한다.") 
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 404, message = "사용자 없음"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
	public ResponseEntity<? extends BaseResponseBody> register(
			@RequestBody @ApiParam(value="회원가입 정보", required = true) UserRegisterPostReq registerInfo, HttpServletRequest req) {

		String path = req.getContextPath();
		log.debug(registerInfo.getEmail());
		log.debug(registerInfo.getUsername());
		log.debug(registerInfo.getPassword());
		//임의로 리턴된 User 인스턴스. 현재 코드는 회원 가입 성공 여부만 판단하기 때문에 굳이 Insert 된 유저 정보를 응답하지 않음.
		User user = userService.createUser(registerInfo);
		
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "성공"));
	}
	
	@GetMapping()
	@ApiOperation(value = "회원 본인 정보 조회", notes = "로그인한 회원 본인의 정보를 응답한다.") 
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 404, message = "사용자 없음"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
	public ResponseEntity<UserRes> getUserInfo(@ApiIgnore Authentication authentication) throws IOException {
		/**
		 * 요청 헤더 액세스 토큰이 포함된 경우에만 실행되는 인증 처리이후, 리턴되는 인증 정보 객체(authentication) 통해서 요청한 유저 식별.
		 * 액세스 토큰이 없이 요청하는 경우, 403 에러({"error": "Forbidden", "message": "Access Denied"}) 발생.
		 */
		if(authentication == null) {
			return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
		}
		UserDetails userDetails = (UserDetails)authentication.getDetails();
		String email = userDetails.getUsername();
		User user = userService.getUserByEmail(email);
		return ResponseEntity.status(200).body(UserRes.of(200, "성공", user));
	}
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Content-Type", defaultValue = "multipart/form-data", paramType = "header"),
	})
	@PatchMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = "회원 정보 수정", notes = "회원 정보 수정 내용과 프로필 이미지 사진 두 개의 객체로 로그인한 회원 본인의 정보를 수정한다.", produces = "multipart/form-data")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> updateUser(@ApiIgnore Authentication authentication,
																 @RequestBody @ApiParam(value="회원 프로필 정보 수정", required = true) @ModelAttribute UserInfoUpdateReq updateInfo) {

		if(authentication == null) {
			return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));
		}

		UserDetails userDetails = (UserDetails) authentication.getDetails();
		String email = userDetails.getUsername();
		User user = userService.getUserByEmail(email);
		if(updateInfo.getNewPassword() != null) {
			if(!passwordEncoder.matches(updateInfo.getCurrentPassword(), user.getPassword())) {
				return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));
			}
		}
		userService.updateUser(updateInfo, user);
		
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "성공"));
	}

	@GetMapping("/chkemail/{email}")
	@ApiOperation(value = "아이디 중복 확인", notes = "입력한 회원 아이디 중복을 체크한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 있음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> checkEmail(@PathVariable("email") @ApiParam(value="중복체크할 아이디", required = true) String email){
		if(!userService.idExists(email)) {
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "성공"));
		}
		return ResponseEntity.status(404).body(BaseResponseBody.of(404, "사용자 있음"));
	}

	@GetMapping("/chkusername/{username}")
	@ApiOperation(value = "이름 중복 확인", notes = "입력한 회원 이름 중복을 체크한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 있음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> checkUsername(@PathVariable("username") @ApiParam(value="중복체크할 이름", required = true) String username){
		if(!userService.userNameExists(username)) {
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "성공"));
		}
		return ResponseEntity.status(404).body(BaseResponseBody.of(404, "사용자 있음"));
	}

	@DeleteMapping()
	@ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴한다.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> userSecession(@ApiIgnore Authentication authentication){
		if(authentication == null) {
			return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));
		}
		UserDetails userDetails = (UserDetails)authentication.getDetails();
		String email = userDetails.getUsername();
		User user = userService.getUserByEmail(email);
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "성공"));
	}


	@DeleteMapping("/profile")
	@ApiOperation(value = "프로필 삭제", notes = "프로필을 삭제한다")
	@ApiResponses({
			@ApiResponse(code = 200, message = "성공"),
			@ApiResponse(code = 401, message = "인증 실패"),
			@ApiResponse(code = 404, message = "사용자 없음"),
			@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<? extends BaseResponseBody> profileDelete(@ApiIgnore Authentication authentication){
		if(authentication == null) {
			return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));
		}
		UserDetails userDetails = (UserDetails)authentication.getDetails();
		String email = userDetails.getUsername();
		User user = userService.getUserByEmail(email);
		userService.deleteProfile(user);

		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "성공"));
	}
}
