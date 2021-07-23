package com.exp.narang.api.controller;

import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.api.response.RoomListRes;
import com.exp.narang.api.response.RoomRegisterPostRes;
import com.exp.narang.api.response.RoomRes;
import com.exp.narang.api.service.RoomService;
import com.exp.narang.api.service.UserService;
import com.exp.narang.common.auth.UserDetails;
import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.Room;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(value = "방 API", tags = {"Conference"})
@RestController
@RequestMapping("/api/v1/room")
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
    public ResponseEntity<? extends BaseResponseBody> register(@ApiIgnore Authentication authentication,
            @RequestBody @ApiParam(value="방생성 정보", required = true) RoomRegisterPostReq roomReqInfo) {

        UserDetails userDetails = (UserDetails)authentication.getDetails();
        String email = userDetails.getUsername();
        roomReqInfo.setOwnerId(userService.getUserByEmail("admin").getUserId());
        Long roomId = roomService.createRoom(roomReqInfo);

        return ResponseEntity.status(200).body(RoomRegisterPostRes.of(200, "Success", roomId));
    }

    @GetMapping()
    @ApiOperation(value = "방 전체 목록", notes = "방 전체 목록을 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })

    public ResponseEntity<? extends BaseResponseBody> readAll(@ApiIgnore Authentication authentication) {
        List<Room> roomList = roomService.findAll();
        return ResponseEntity.status(200).body(RoomListRes.of(200, "Success", roomList));
    }

    @GetMapping("/{roomId}")
    @ApiOperation(value = "방 상세 조회", notes = "roomId로 방 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })

    public ResponseEntity<? extends BaseResponseBody> readRoom(@ApiIgnore Authentication authentication, @PathVariable String roomId) {
        Room room = roomService.findById(Long.parseLong(roomId));
        return ResponseEntity.status(200).body(RoomRes.of(200, "Success", room));
    }

    @GetMapping("/title/{title}")
    @ApiOperation(value = "방 제목 검색", notes = "<strong>방 제목으로</strong>방 목록을 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> readTitle(@ApiIgnore Authentication authentication, @PathVariable("title") String title) {
        List<Room> roomList = roomService.findByTitle(title);
        return ResponseEntity.status(200).body(RoomListRes.of(200, "Success", roomList));
    }

    @GetMapping("/game/{game}")
    @ApiOperation(value = "방 게임 종류 검색", notes = "<strong>게임 이름으로</strong>방 목록을 조회한다 (mafia, callme)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> readGame(@ApiIgnore Authentication authentication, @PathVariable("game") String game) {
        List<Room> roomList = roomService.findByGame(game);
        return ResponseEntity.status(200).body(RoomListRes.of(200, "Success", roomList));
    }
}
