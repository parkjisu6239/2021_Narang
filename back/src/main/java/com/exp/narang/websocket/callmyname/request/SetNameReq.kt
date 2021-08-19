package com.exp.narang.websocket.callmyname.request

class SetNameReq (val userId: Long, // 투표자의 userId (send 요청을 보낸 장본인)
                  val targetId: Long, // 타겟의 userId
                  val content: String, // 제시어
                  val vote: Int, // 투표 추가 시 1, 투표 철회 시 -1, 제시어 입력 시 0
                  val isFinished: Boolean // 제한시간 끝나면 true, 아니면 false
                  )