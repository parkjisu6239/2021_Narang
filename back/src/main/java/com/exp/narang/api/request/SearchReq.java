package com.exp.narang.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchReq {
    String title;
    String game;
    Boolean isActivate;

    public static SearchReq of(String title, String game, Boolean isActivate) {
        SearchReq req = new SearchReq();
        req.setGame(game);
        req.setTitle(title);
        req.setIsActivate(isActivate);
        return req;
    }
}