package com.exp.narang.webrtc.controller;

import com.exp.narang.common.auth.UserDetails;
import com.exp.narang.common.model.response.BaseResponseBody;
import com.exp.narang.db.entity.User;
import com.exp.narang.webrtc.response.SessionTokenResponseBody;
import io.openvidu.java.client.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private OpenVidu openVidu;

    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();

    private Map<String, Map<String, OpenViduRole>> mapSessionTitlesTokens = new ConcurrentHashMap<>();

    private String OPENVIDU_URL;

    private String SECRET;

    public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    @PostMapping("/get-token")
    @ApiOperation(value = "실제 세션 생성", notes = "화상 채팅을 위한 방을 생성한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> getToken(@ApiIgnore Authentication authentication,
                                                               @RequestBody @ApiParam(value="세션(방) 이름", required = true) String title){
        // 토큰 없이 요청하면 인증 실패
        if(authentication == null)
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));

        UserDetails userDetails = (UserDetails)authentication.getDetails();
        User user = userDetails.getUser();

        // 참여자는 전부 실시간 영상 송신
        OpenViduRole role = OpenViduRole.PUBLISHER;
        String serverData = "{\"serverData\": \"" + user.getEmail() + "\"}";

        // Build connectionProperties object with the serverData and the role
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).role(role).build();

        String token = "";
        // 최근 생성된 connectionsProperties 객체로 토큰 생성
        try {
            token = mapSessions.get(title).createConnection(connectionProperties).getToken();
            this.mapSessionTitlesTokens.get(title).put(token, role);
            // 방제목에 해당하는 방이 없음
            if (mapSessions.get(title) == null) {
                // 새로운 세션 생성
                Session session = this.openVidu.createSession();

                // 생성한 사용자 토큰과 역할 저장
                this.mapSessions.put(title, session);
                this.mapSessionTitlesTokens.put(title, new ConcurrentHashMap<>());
                mapSessionTitlesTokens.get(title).put(token, role);
            }
        }catch (OpenViduJavaClientException e) {
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "서버 오류"));
        }catch (OpenViduHttpException e) {
            if (404 == e.getStatus()) {
                // 사용자가 예기치않게 떠나면 세션은 더이상 유효하지 않다
                mapSessions.remove(title);
                mapSessionTitlesTokens.remove(title);
                return ResponseEntity.status(404).body(BaseResponseBody.of(404, "잘못된 요청"));
            }
        }
        return ResponseEntity.status(200).body(SessionTokenResponseBody.of(200, "성공", token));
    }

    // 방 나갈 때 호출
    @PostMapping(value = "/leave-session")
    @ApiOperation(value = "세션 ", notes = "화상 채팅을 위한 방을 생성한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 404, message = "잘못된 요청"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> removeUser(@ApiIgnore Authentication authentication,
            @RequestParam(name = "session-name") String sessionName,
            @RequestParam(name = "token") String token) throws Exception {
        // 토큰 없이 요청하면 인증 실패
        if(authentication == null)
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "인증 실패"));

//        UserDetails userDetails = (UserDetails)authentication.getDetails();
//        User user = userDetails.getUser();

        // 세션(방)이 있고 세션 이름에 해당하는 mapSessionNamesTokens 객체가 있음
        if (this.mapSessions.get(sessionName) != null && this.mapSessionTitlesTokens.get(sessionName) != null) {

            // 사용자 토큰 있음
            if (this.mapSessionTitlesTokens.get(sessionName).remove(token) != null) {
                // 세션 이름에 해당하는 mapSessionNamesTokens 객체가 비었음 = 세션에 사용자 없음
                if (this.mapSessionTitlesTokens.get(sessionName).isEmpty()) {
                    // 세션 삭제
                    this.mapSessions.remove(sessionName);
                }
                return ResponseEntity.status(200).body(SessionTokenResponseBody.of(200, "성공", token));
            } else {
                // 토큰이 유효하지 않음
                //System.out.println("Problems in the app server: the TOKEN wasn't valid");
                return ResponseEntity.status(404).body(BaseResponseBody.of(404, "잘못된 요청"));
            }
        } else {
            // 세션 없음
            //System.out.println("Problems in the app server: the SESSION does not exist");
            return ResponseEntity.status(404).body(BaseResponseBody.of(404, "잘못된 요청"));
        }
    }
}