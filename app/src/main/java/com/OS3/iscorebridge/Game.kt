package com.OS3.iscorebridge

class Game {
    var contract: Contract
    var bidding : Bidding
    var pairNS: PlayerPair
    var pairEW: PlayerPair
    var tricks: Int
    var score: Int
    var boardNumber : Int
    var lead : Card
    var dlm = "||"

    constructor(boardNumber : Int, pairNS: PlayerPair, pairEW: PlayerPair, vulnerability: Vulnerability, contract: Contract = Contract(), tricks: Int = 0, lead: Card = Card()){
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
        this.pairNS = PlayerPair(parametersAsString[2])
        this.pairEW = PlayerPair(parametersAsString[3])
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


    fun declaredBy(myPair : PlayerPair) : Boolean{
        return contract.declaredBy(myPair, pairNS)
    }

    fun made():Boolean{
        return contract.number >= tricks
    }




    fun toArray() : ArrayList<String>{
        return arrayListOf(pairNS.displayNumber.toString(), pairEW.displayNumber.toString(), contract.toDisplayString(), lead.toString(), tricks.toString(), score.toString())
    }
}