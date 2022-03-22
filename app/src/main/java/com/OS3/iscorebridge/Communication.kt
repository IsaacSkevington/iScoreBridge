package com.OS3.iscorebridge



class ClientAssignment{
    val dlm = "!!"

    var port : Int
    var myNumber : Int

    constructor(s : String){
        var parsed = s.split(dlm)
        port = parsed[0].toInt()
        myNumber = parsed[1].toInt()
    }
    constructor(port : Int, num : Int){
        this.port = port
        myNumber = num
    }

    override fun toString(): String {
        return port.toString() + dlm + myNumber.toString()
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