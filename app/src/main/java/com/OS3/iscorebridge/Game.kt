package com.OS3.iscorebridge

class Game {
    var contract: Contract
    var bidding : Bidding
    var pairNS: Int
    var pairEW: Int
    var tricks: Int
    var score: Int
    var boardNumber : Int
    var lead : Card
    var dlm = "||"

    constructor(boardNumber : Int, contract: Contract, pairNS: Int, pairEW: Int, tricks: Int, lead: Card, vulnerability: Vulnerability){
        this.boardNumber = boardNumber
        this.contract = contract
        this.pairNS = pairNS
        this.pairEW = pairEW
        this.tricks = tricks
        this.lead = lead
        this.score = contract.calculateScore(tricks, vulnerability)
        this.bidding = Bidding(getDealer(boardNumber))
    }

    constructor(game: String){
        val parametersAsString = game.split(dlm)
        this.boardNumber = parametersAsString[0].toInt()
        this.contract = Contract(parametersAsString[1])
        this.pairNS = parametersAsString[2].toInt()
        this.pairEW = parametersAsString[3].toInt()
        this.tricks = parametersAsString[4].toInt()
        this.lead = Card(parametersAsString[5])
        this.score = parametersAsString[6].toInt()
        this.bidding = Bidding(parametersAsString[7])
    }

    override fun toString(): String {
        return this.boardNumber.toString() + dlm +this.contract.toString() + dlm + this.pairNS.toString() + dlm + this.pairEW.toString() + dlm + this.tricks.toString() + dlm + this.lead.toString() + dlm + this.score.toString() + dlm + this.bidding.toString()
    }

    fun copy(other: Game){
        this.pairNS = other.pairNS
        this.pairEW = other.pairEW
        this.bidding = other.bidding
        this.score = other.score
        this.tricks = other.tricks
        this.lead = other.lead
        this.contract = other.contract
        this.boardNumber = other.boardNumber
    }


    fun declaredBy(myNumber : Int) : Boolean{
        return contract.declaredBy(myNumber, pairNS)
    }

    fun made():Boolean{
        return contract.number >= tricks
    }




    fun toArray() : ArrayList<String>{
        return arrayListOf(pairNS.toString(), pairEW.toString(), contract.toDisplayString(), lead.toString(), tricks.toString(), score.toString())
    }
}