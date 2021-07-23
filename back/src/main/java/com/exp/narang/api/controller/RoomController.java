package com.exp.narang.api.controller;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.api.response.RoomRegisterPostRes;
import com.exp.narang.api.service.RoomService;
import com.exp.narang.api.service.UserService;
import com.exp.narang.common.model.response.BaseResponseBody;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "방 API", tags = {"Conference"})
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    @PostMapping()
    @ApiOperation(value = "방 생성", notes = "<strong>RoomTitle, maxPlayer</strong>를 통해 방을 생성한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> register(
            @RequestBody @ApiParam(value="방생성 정보", required = true) RoomRegisterPostReq roomReqInfo) {
//
//        UserDetails userDetails = (UserDetails)authentication.getDetails();
//        String email = userDetails.getUsername();
        roomReqInfo.setOwnerId(userService.getUserByEmail("admin").getUserId());
        Long roomId = roomService.createRoom(roomReqInfo);

        return ResponseEntity.status(200).body(RoomRegisterPostRes.of(200, "Success", roomId));
    }
}
