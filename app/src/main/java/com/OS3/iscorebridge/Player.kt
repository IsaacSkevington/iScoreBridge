package com.OS3.iscorebridge

class Player{

    var name : String
    var id : Int

    val dlm = "$**"

    constructor(){
        name = "???"
        id = 0
    }

    constructor(name: String, id: Int){
        this.name = name
        this.id = id
    }

    constructor(s : String){
        var params = s.split(dlm)
        this.name = params[0]
        this.id = params[1].toInt()
    }

    override fun toString(): String {
        return name + dlm + id
    }




}