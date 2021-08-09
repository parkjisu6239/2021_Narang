package com.exp.narang.websocket.callmyname.model.manager;

import com.exp.narang.websocket.callmyname.request.NameReq;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameManager {
    private final Map<Long, String> nameMap;

    public GameManager(){
        nameMap = new ConcurrentHashMap<>();
    }

    // 이름 정하기
    public void setIdentity(NameReq req){
        nameMap.put(req.getUserId(), req.getName());
    }

    // 이름 맞히기
    public boolean guess(NameReq req){
        return nameMap.get(req.getUserId()).equals(req.getName());
    }
}