package com.exp.narang.api.controller;

import com.exp.narang.api.service.RoomService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Api(value = "방 API", tags = {"Conference"})
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostConstruct
    public void init(){
//        ConferenceCategory conferenceCategory = new ConferenceCategory();
//        conferenceCategory.setName("업무");
//        ConferenceCategory conferenceCategory1 = new ConferenceCategory();
//        conferenceCategory1.setName("교육");
//        ConferenceCategory conferenceCategory2 = new ConferenceCategory();
//        conferenceCategory2.setName("기타");
//        conferenceService.addConferenceCategory(conferenceCategory);
//        conferenceService.addConferenceCategory(conferenceCategory1);
//        conferenceService.addConferenceCategory(conferenceCategory2);
    }
}
