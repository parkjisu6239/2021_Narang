package com.exp.narang.api.controller;

import com.exp.narang.api.model.dto.RoomDto;
import com.exp.narang.api.model.request.RoomEnterGetReq;
import com.exp.narang.api.model.request.RoomRegisterPostReq;
import com.exp.narang.api.model.request.RoomSearchGetReq;
import com.exp.narang.api.model.request.RoomUpdatePatchReq;
import com.exp.narang.api.model.response.*;
import com.exp.narang.api.model.service.RoomService;
import com.exp.narang.api.model.service.UserService;
import com.exp.narang.common.auth.UserDetails;
import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.api.model.db.entity.Room;
import com.exp.narang.api.model.db.entity.User;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
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

    @Transactional
    @PostMapping()
    @ApiOperation(value = "방 생성", notes = "<strong>RoomTitle, maxPlayer, password</strong>를 통해 방을 생성한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> register(@ApiIgnore Authentication authentication,
            @RequestBody @ApiParam(value="방 생성 정보", required = true) RoomRegisterPostReq roomRegisterPostReq) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
        UserDetails userDetails = (UserDetails)authentication.getDetails();//
        User user = userDetails.getUser();//
        Long roomId = roomService.createRoom(roomRegisterPostReq, user.getUserId());
        Room room = roomService.findById(roomId); // 들어가려는 방 정보
        System.out.println("유저 pk : "+user.getUserId() + "유저 이름 : "+user.getUsername());
        System.out.println("방제목 : "+room.getTitle());
        roomService.enterRoom(room, user);
        return ResponseEntity.status(200).body(RoomRegisterPostRes.of(200, "Success", roomId));
    }

    @Transactional
    @GetMapping()
    @ApiOperation(value = "방 목록 조회", notes = "방 목록을 조회한다. 전체 목록 조회시 파라미터 값 안 보내면 됨." + '\n' +
            "현재 page 번호 page, 개수 size" +'\n' +
            "first, last / true, false 로 처음 마지막 페이지 구분 가능")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> read(
                                                            @ApiIgnore Authentication authentication,
                                                            @RequestParam(name = "title", required = false) String title,
                                                            @RequestParam(name = "game", required = false) String game,
                                                            @RequestParam(name = "isActivate", required = false) Boolean isActivate,
                                                            @PageableDefault
                                                                    @SortDefault.SortDefaults({
                                                                            @SortDefault(sort = "isActivate", direction = Sort.Direction.DESC),
                                                                            @SortDefault(sort = "createdTime", direction = Sort.Direction.DESC)
                                                                    }) Pageable pageable) {
        if(authentication == null) return ResponseEntity.status(401).body(RoomListRes.of(401, "인증 실패", null));
        UserDetails userDetails = (UserDetails)authentication.getDetails();
        User currentUser = userDetails.getUser(); // 로그인 한 유저 정보 가져옴
        log.debug("목록 조회 하러 왔음");
        if(currentUser.getRoom() != null) {
            log.debug("현재 유저가 방을 가지고 있음. 방 제목:"+currentUser.getRoom().getTitle());
            roomService.deleteRoom(currentUser.getRoom(), currentUser);
            log.debug("현재 유저의 방 나가기 처리 하고 왔음");
        }
        Page<Room> origin = roomService.findBySearch(RoomSearchGetReq.of(title, game, isActivate) ,pageable);
        List<RoomDto> target = new ArrayList<>();
        log.debug("방 전체 목록 불러오는 중");
        for(Room room : origin) {
            RoomDto roomDto = new RoomDto();
//            BeanUtils.copyProperties(room, roomListDto);
            roomDto.setRoom(room);
            roomDto.setJoinUsers(roomService.findUserListByRoomId(room.getRoomId()));
            log.debug("현재 불러온 방 제목 : [" + room.getTitle() + "] 의 유저 수 : " + roomDto.getJoinUsers().size());
            // 유저가 아무도 없는데 방이 존재하는 경우 연쇄 삭제 처리
            if(roomDto.getJoinUsers().size() == 0) {
                log.debug("유저가 아무도 없는데 존재하는 방입니다. 삭제 처리 시작");
                List<User> userList = userService.findUserList(); // 모든 유저 리스트 가져옴
                for(User user : userList){
                    if(user.getRoom() != null && user.getRoom().getRoomId() == room.getRoomId()){ // 아직 방 정보가 남아 있는 유저들만
                        log.debug("이 방 정보를 가지고 있는 유저의 이름 : " + user.getUsername());
                        roomService.deleteRoomInUser(user); // 유저 정보에서 방 정보 삭제
                        log.debug(user.getUsername() + "의 방 정보 삭제 완료");
                    }
                }
                roomService.deleteById(room.getRoomId()); // 마지막으로 방도 삭제
                log.debug("마지막으로 방 정보도 삭제 완료");
            }
            else target.add(roomDto);
        }
        Page<RoomDto> pageList = new PageImpl<>(target, origin.getPageable(), origin.getTotalElements());
        log.debug("방 전체 목록 다 불러옴");
        return ResponseEntity.status(200).body(RoomListRes.of(200, "Success", pageList));
    }

    @PostMapping("/{roomId}")
    @ApiOperation(value = "방 입장", notes = "방장 아닌 사람들이 방에 입장한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> enterRoom(@ApiIgnore Authentication authentication, @PathVariable String roomId, @RequestBody RoomEnterGetReq roomEnterGetReq) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
        Room room = roomService.findById(Long.parseLong(roomId)); // 들어가려는 방 정보 가져옴

        int currentPlayer = roomService.findUserListByRoomId(Long.parseLong(roomId)).size(); // 들어가려는 방에 들어있는 인원 수
        if(currentPlayer >= room.getMaxPlayer()) return ResponseEntity.status(401).body(RoomRes.of(401, "인원이 꽉 차서 방에 들어갈 수 없음", room));

        int password = roomEnterGetReq.getPassword();
        if(room.getPassword() == 0 || password == room.getPassword()){ // 비밀번호가 없거나 일치하면 성공
            UserDetails userDetails = (UserDetails)authentication.getDetails();
            User user = userDetails.getUser(); // 로그인 한 유저 정보 가져옴
            roomService.enterRoom(room, user);
            return ResponseEntity.status(200).body(RoomRes.of(200, "Success", room));
        }
        return ResponseEntity.status(401).body(RoomRes.of(401, "방 비밀번호 인증 실패", room));
    }

    @PatchMapping("/{roomId}")
    @ApiOperation(value = "방 수정", notes = "game(mafia, callme)을 지정하면 방 정보가 바뀐다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> updateRoom(@ApiIgnore Authentication authentication, @PathVariable String roomId, @RequestBody RoomUpdatePatchReq roomUpdatePatchReq) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));//
        UserDetails userDetails = (UserDetails)authentication.getDetails();//
        User user = userDetails.getUser(); // 로그인 한 유저 정보 가져옴
        Room room = roomService.findById(Long.parseLong(roomId));
        if(user.getUserId() != room.getOwnerId()) return ResponseEntity.status(401).body(UserRes.of(401, "방장이 아니면 설정 바꿀 수 없음.", null));
        roomService.updateRoom(roomUpdatePatchReq, room);//
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }

    @DeleteMapping("/{roomId}")
    @ApiOperation(value = "방 나가기", notes = "일반 사람은 그냥 나가지고, 방장이 나가면 남은 사람 중 한 명이 방장이 된다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> deleteRoom(@ApiIgnore Authentication authentication, @PathVariable String roomId) {
        if(authentication == null) return ResponseEntity.status(401).body(UserRes.of(401, "인증 실패", null));
        log.debug("Leaving room STARTED.");
        UserDetails userDetails = (UserDetails)authentication.getDetails();//
        User user = userDetails.getUser(); // 로그인 한 유저 정보 가져옴
        Room room = roomService.findById(Long.parseLong(roomId));
        log.debug("Username who want to leave : " + user.getUsername());
        log.debug("RoomId that [" + user.getUsername() + "] wants to leave : " + room.getRoomId());
        roomService.deleteRoom(room, user);//
        log.debug("Username who left : " + user.getUsername());
        log.debug("Leaving room FINISHED.");
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }

    @GetMapping("/{roomId}")
    @ApiOperation(value = "현재 방 정보 조회", notes = "현재 방의 정보를 조회한다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends RoomRes> readRoom(@ApiIgnore Authentication authentication, @PathVariable Long roomId) {
        if(authentication == null) return ResponseEntity.status(401).body(RoomRes.of(401, "인증 실패", null));
        Room room = roomService.findById((roomId)); // 들어가려는 방 정보 가져옴
        return ResponseEntity.status(200).body(RoomRes.of(200, "성공", room));
    }

    @GetMapping("/userlist/{roomId}")
    @ApiOperation(value = "현재 방 참여 유저 정보", notes = "현재 방에 들어가있는 유저들 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends RoomJoinUserListRes> readRoomUserList(@PathVariable Long roomId) {
//        if(authentication == null) return ResponseEntity.status(401).body(RoomJoinUserListRes.of(401, "인증 실패", null));
        List<User> userList  = roomService.findUserListByRoomId((roomId)); // 들어가려는 방 정보 가져옴
        return ResponseEntity.status(200).body(RoomJoinUserListRes.of(200, "성공", userList));
    }
}