package com.OS3.iscorebridge



class ClientAssignment{
    val dlm = "!!"

    var connectionAddress : String
    var myNumber : Int

    constructor(s : String){
        var parsed = s.split(dlm)
        connectionAddress = parsed[0]
        myNumber = parsed[1].toInt()
    }
    constructor(addr : String, num : Int){
        connectionAddress = addr
        myNumber = num
    }

    override fun toString(): String {
        return connectionAddress + dlm + myNumber.toString()
    }
}

class Communication{

    val dlm = "$$"

    var deviceID : String
    var purpose : Int
    var msg : String

    constructor(deviceID: String, purpose: Int, msg: String){
        this.deviceID = deviceID
        this.purpose = purpose
        this.msg = msg
    }
    constructor(s : String){
        var parsed = s.split(dlm)
        deviceID = parsed[0]
        purpose = parsed[1].toInt()
        msg = parsed[2]
    }

    override fun toString() : String{
        return this.deviceID + dlm + this.purpose.toString() + dlm + this.msg
    }
}