package com.exp.narang.websocket.mafia.response;


import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.websocket.mafia.model.Player;
import com.exp.narang.websocket.mafia.model.role.Role;
import com.exp.narang.websocket.mafia.request.MafiaMessage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
@Getter
@Setter
// 게임 참가자들과 관련된 로직을 처리한다.
public class GamePlayers {
    private static final Logger log = LoggerFactory.getLogger(GamePlayers.class);

    private int countMafiaMissionResult, countMafiaMissionCompelte;

    private List<Player> players;

    private RoleResult roleResult;

    // 참가자 리스트 추가하는 생성자
    public GamePlayers(List<User> users) {
        this.players = new ArrayList<>();
        for (User user : users) {
            this.players.add(new Player(user));
        }
    }

    // players의 수를 가져온다.
    public int countOfPlayers() {
        return this.players.size();
    }
    public int countOfMafias() { return (int) this.players.stream().filter(player -> player.isMafia()).count(); }


    // players의 역할을 분배한다.
    public void setRole(List<Role> roles) {
        for (int i = 0; i < countOfPlayers(); i++) {
            this.players.get(i).setRole(roles.get(i));
        }
    }

    // 각 player의 역할과 미션 번호를 리턴한다.
    public RoleResult findRoleName(String username) {
        roleResult = new RoleResult();
        for (Player player : this.players) {
            if (player.getUser().getUsername().equals(username)) {
                roleResult.setRoleName(player.getRole().getRoleName()); // 역할 저장
                System.out.println("역할역할"+roleResult.getRoleName());
                if(player.getRole().isMafia()) roleResult.setMissionNumber((int)(Math.random() * 100) % 11); // 마피아는 미션 번호 저장(0~10 중 랜덤)
                else roleResult.setMissionNumber(-1); // 시민은 미션 번호에 -1 저장
                return roleResult;
            }
        }
        return null;
    }

    public Player getPlayer(String username) {
        for (Player player : this.players) {
            if (player.getUser().getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public int everyMafiaMissionComplete(MafiaMessage mafiaMessage){
        this.countMafiaMissionResult++; // 마피아 미션 수행 결과 카운트
        if(mafiaMessage.getIsMissionComplete()) this.countMafiaMissionCompelte++; // 미션 성공한 경우만 카운트
        if(this.countMafiaMissionResult == countOfMafias()){ // 모든 마피아의 결과를 가져온 경우 투표 가능 여부 판단
            System.out.println("미션 결과 받은 개수 : "+this.countMafiaMissionResult);
            System.out.println("미션 성공 개수 : "+this.countMafiaMissionCompelte);
            if (this.countMafiaMissionCompelte == this.countMafiaMissionResult){
                this.countMafiaMissionCompelte = this.countMafiaMissionResult = 0;
                return 1;
            }
            else{
                this.countMafiaMissionCompelte = this.countMafiaMissionResult = 0;
                return 0;
            }
//            return (this.countMafiaMissionCompelte == this.countMafiaMissionResult) ? 1 : 0; // 모두 성공 -> 1, 하나라도 실패 -> 0 반환
        }
        else return -1; // 아직 모든 마피아의 결과를 모으지 못 한 경우
    }

    public void removeDeadPlayer(Player player) {
        this.players.remove(player);
    }

    public GameResultType judgementPlayersCount() {
        int mafiaCount = this.countOfMafias();
        int citizenCount = (this.countOfPlayers() - mafiaCount);
        log.debug("judgementPlayersCount:: MafiaCount: {}, CitizenCount: {}", mafiaCount, citizenCount);
        if (mafiaCount == 0) {
            return GameResultType.CITIZEN_WIN;
        } else if (citizenCount <= mafiaCount) {
            return GameResultType.MAFIA_WIN;
        }
        return GameResultType.KEEP_GOING;
    }
    // 게임이 완전히 끝났을 경우 유저 이름과 유저의 역할을 String 형태로 반환해준다
    public String getRoleString() {
        StringBuffer roleString = new StringBuffer();
        for (Player p : players) {
            roleString.append(p.getUser().getUsername());
            roleString.append(":");
            roleString.append(p.getRole().getRoleName());
            roleString.append("&");
        }
        if (roleString.length() > 0) {
            return roleString.substring(0, roleString.length() - 1);
        }
        return roleString.toString();
    }
}
