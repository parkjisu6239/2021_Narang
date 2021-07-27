package com.exp.narang.api.controller;

import com.exp.narang.api.request.RoomReadGetReq;
import com.exp.narang.api.request.RoomRegisterPostReq;
import com.exp.narang.api.request.SearchReq;
import com.exp.narang.api.response.RoomListRes;
import com.exp.narang.api.response.RoomRegisterPostRes;
import com.exp.narang.api.response.RoomRes;
import com.exp.narang.api.response.UserRes;
import com.exp.narang.api.service.RoomService;
import com.exp.narang.api.service.UserService;
import com.exp.narang.common.auth.UserDetails;
import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.Room;
import com.exp.narang.db.entity.User;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Api(value = "방 API", tags = {"Room"})
@RestController
@RequestMapping("/api/v1/room")
@Slf4j
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    @PostMapping()
    @ApiOperation(value = "방 생성", notes = "<strong>RoomTitle, maxPlayer, password</strong>를 통해 방을 생성한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> register(@ApiIgnore Authentication authentication,
            @RequestBody @ApiParam(value="방 생성 정보", required = true) RoomRegisterPostReq roomReqInfo) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
        UserDetails userDetails = (UserDetails)authentication.getDetails();//
        User user = userDetails.getUser();//
//        User user = userService.getUserByEmail("a@aa.aa");//임시
        Long roomId = roomService.createRoom(roomReqInfo, user.getUserId());
        Room room = roomService.findById(roomId); // 들어가려는 방 정보
        System.out.println("유저 pk : "+user.getUserId() + "유저 이름 : "+user.getUsername());
        System.out.println("방제목 : "+room.getTitle());
        roomService.enterRoom(room, user);
        return ResponseEntity.status(200).body(RoomRegisterPostRes.of(200, "Success", roomId));
    }

    @GetMapping()
    @ApiOperation(value = "방 목록 조회", notes = "방 목록을 조회한다. 전체 목록 조회시 파라미터 값 안 보내면 됨." + '\n' +
            "현재 page 번호 page, 개수 size" +'\n' +
            "first, last / true, false 로 처음 마지막 페이지 구분 가능")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })

    public ResponseEntity<? extends BaseResponseBody> read( @RequestParam(name = "title", required = false) String title,
                                                            @RequestParam(name = "game", required = false) String game,
                                                            @RequestParam(name = "isActivate", required = false) Boolean isActivate,
                                                            @PageableDefault(sort = "createdTime") Pageable pageable) {
        Page<Room> roomList = roomService.findBySearch(SearchReq.of(title, game, isActivate) ,pageable);
        return ResponseEntity.status(200).body(RoomListRes.of(200, "Success", roomList));
    }
    @PostMapping("/{roomId}")
    @ApiOperation(value = "방 입장", notes = "방에 입장한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })

    public ResponseEntity<? extends BaseResponseBody> readRoom(@ApiIgnore Authentication authentication, @PathVariable String roomId, @RequestBody RoomReadGetReq roomReadGetReq) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
        Room room = roomService.findById(Long.parseLong(roomId)); // 들어가려는 방 정보 가져옴
        int password = roomReadGetReq.getPassword();
        if(room.getPassword() == 0 || password == room.getPassword()){ // 비밀번호가 없거나 일치하면 성공
            UserDetails userDetails = (UserDetails)authentication.getDetails();
            User user = userDetails.getUser(); // 로그인 한 유저 정보 가져옴
//          User user = userService.getUserByEmail("f@ff.ff");//임시
            roomService.enterRoom(room, user);
            return ResponseEntity.status(200).body(RoomRes.of(200, "Success", room));
        }
        return ResponseEntity.status(400).body(RoomRes.of(400, "인증 실패", room));
    }

    @DeleteMapping("/{roomId}")
    @ApiOperation(value = "방 나가기", notes = "방장이 나가면 방이 삭제된다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> deleteRoom(@ApiIgnore Authentication authentication, @PathVariable String roomId) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
        UserDetails userDetails = (UserDetails)authentication.getDetails();//
        User user = userDetails.getUser(); // 로그인 한 유저 정보 가져옴
        Room room = roomService.findById(Long.parseLong(roomId));
//        roomService.deleteRoom(room, userService.getUserByEmail("f@ff.ff"));//임시
        roomService.deleteRoom(room, user);//
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }

}
