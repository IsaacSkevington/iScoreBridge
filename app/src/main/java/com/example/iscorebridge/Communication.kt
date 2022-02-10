package com.example.iscorebridge




class Communication{

    val delim = "$$"

    var deviceID : String
    var purpose : Int
    var msg : String

    constructor(deviceID: String, purpose: Int, msg: String){
        this.deviceID = deviceID
        this.purpose = purpose
        this.msg = msg
    }
    constructor(s : String){
        var parsed = s.split(delim)
        deviceID = parsed[0]
        purpose = parsed[1].toInt()
        msg = parsed[2]
    }

    override fun toString() : String{
        return this.deviceID + delim + this.purpose.toString() + delim + this.msg
    }
}