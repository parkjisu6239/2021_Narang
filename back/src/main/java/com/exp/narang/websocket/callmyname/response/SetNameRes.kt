package com.exp.narang.websocket.callmyname.response

class SetNameRes (var nextUserId: Long, var setterUserId: Long){
    companion object {
        private val NO_VALUE_INSTANCE by lazy { SetNameRes(-1, -1) }
        fun getEndInstance() = NO_VALUE_INSTANCE

        private val NEXT_VALUE_INSTANCE by lazy { SetNameRes(-1, -1) }
        fun of(nextUserId: Long, setterUserId: Long) =
            NEXT_VALUE_INSTANCE.apply {
                this.nextUserId = nextUserId
                this.setterUserId = setterUserId
            }
    }
}