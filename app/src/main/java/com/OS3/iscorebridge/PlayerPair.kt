package com.OS3.iscorebridge

class PlayerPair{


    var p1 : Player
    var p2 : Player
    var delim = "??"
    var displayNumber : Int
    var scoringNumber : Int

    constructor(i : Int) : this(i, Player(), Player())

    constructor() : this(0, Player(), Player())

    constructor(s : String){
        var params = s.split(delim)
        displayNumber = params[0].toInt()
        p1 = Player(params[1])
        p2 = Player(params[2])
        scoringNumber = params[3].toInt()
    }
    constructor(number : Int, p1 : Player, p2 : Player, team : Int){
        this.p1 = p1
        this.p2 = p2
        this.displayNumber = number
        this.scoringNumber = team
    }
    constructor(number : Int, p1 : Player, p2 : Player) : this(number, p1, p2, number)

    override fun toString(): String {
        return displayNumber.toString() + delim + p1.toString() + delim + p2.toString() + delim + scoringNumber.toString()
    }

    fun uniqueEquals(other : PlayerPair): Boolean {
        return displayNumber == other.displayNumber
    }
    override fun equals(other: Any?): Boolean {
        return try{
            var otherPlayerPair = other as PlayerPair
            this.scoringNumber == otherPlayerPair.scoringNumber
        } catch(e : ClassCastException){
            false
        }
    }

    override fun hashCode(): Int {
        return displayNumber.hashCode()
    }



}