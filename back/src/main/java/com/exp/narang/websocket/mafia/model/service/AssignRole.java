package com.exp.narang.websocket.mafia.model.service;

import com.exp.narang.websocket.mafia.model.role.Citizen;
import com.exp.narang.websocket.mafia.model.role.Mafia;
import com.exp.narang.websocket.mafia.model.role.Role;
import java.util.List;

import java.util.ArrayList;

public class AssignRole {
    public int memberCount;
    public int mafiaCount;
    public int citizenCount;

    public AssignRole (int memberCount, int mafiaCount, int citizenCount) {
        this.mafiaCount = memberCount;
        this.mafiaCount = mafiaCount;
        this.citizenCount = citizenCount;
    }

    public List<Role> makeRole() {
        List<Role> list = new ArrayList<>();
        for(int i = 0; i < this.mafiaCount; i++) {
            list.add(new Mafia());
        }
        for(int i = 0; i < this.citizenCount; i++) {
            list.add(new Citizen());
        }
        return  list;
    }
}
