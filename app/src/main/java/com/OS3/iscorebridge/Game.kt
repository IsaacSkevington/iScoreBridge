package com.OS3.iscorebridge

class Game {
    var contract: Contract
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
        this.score = calculateScore(vulnerability)
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
    }

    override fun toString(): String {
        return this.boardNumber.toString() + dlm +this.contract.toString() + dlm + this.pairNS.toString() + dlm + this.pairEW.toString() + dlm + this.tricks.toString() + dlm + this.lead.toString() + dlm + this.score.toString()
    }

    private fun goingDown(tricksDown : Int, vulnerability: Vulnerability) : Int{
        var penality: Int
        if(vulnerability.isVulnerable(contract.declarer)){
            when {
                contract.doubled -> {
                    penality = 200
                    for(i in 0 until (tricksDown - 1)){
                        penality += 300
                    }
                }
                contract.redoubled -> {
                    penality = 400
                    for(i in 0 until (tricksDown - 1)){
                        penality += 600
                    }
                }
                else -> {
                    penality = 100 * tricksDown
                }
            }
        }
        else{
            when {
                contract.doubled -> {
                    penality = 100
                    if(tricksDown - 1 != 0){
                        penality += 200
                    }
                    for(i in 0 until (tricksDown - 2)){
                        penality += 300
                    }
                }
                contract.redoubled -> {
                    penality = 200
                    if(tricksDown - 1 != 0){
                        penality += 400
                    }
                    for(i in 0 until (tricksDown - 2)){
                        penality += 300
                    }
                }
                else -> {
                    penality = 50 * tricksDown
                }
            }
        }
        return penality
    }

    private fun overTricks(tricksOver : Int, vulnerability: Vulnerability) : Int{
        if(!contract.doubled && !contract.redoubled){
            return if(contract.isMinor()) 20 * tricksOver
            else 30 * tricksOver
        }
        else {

            if (vulnerability.isVulnerable(contract.declarer)) {
                if (contract.doubled) {
                    return tricksOver * 200
                } else if (contract.redoubled) {
                    return tricksOver * 400
                }
            } else {
                if (contract.doubled) {
                    return tricksOver * 100
                } else if (contract.redoubled) {
                    return tricksOver * 200
                }
            }
        }
        return 0
    }

    private fun contractPoints() : Int{
        val contractTricks = contract.number
        if(contract.doubled){
            if(contract.isMinor()){
                return contractTricks * 40 + 100
            }
            if(contract.isMajor()){
                return contractTricks * 60 + 100
            }
            if(contract.isNoTrump()){
                return 80 + 60*(contractTricks-1) + 100
            }
        }
        else if(contract.redoubled){
            if(contract.isMinor()){
                return contractTricks * 80 + 50
            }
            if(contract.isMajor()){
                return contractTricks * 120 + 50
            }
            if(contract.isNoTrump()){
                return 160 + 120*(contractTricks-1) + 50
            }
        }
        else{
            if(contract.isMinor()){
                return contractTricks * 20
            }
            if(contract.isMajor()){
                return contractTricks * 30
            }
            if(contract.isNoTrump()){
                return 40 + 30*(contractTricks-1)
            }
        }
        return 0
    }

    private fun calculateScore(vulnerability: Vulnerability) : Int{
        var score = 0
        val tricksNeeded = this.contract.number + 6
        if(tricksNeeded > tricks){
            score = -1 * goingDown(tricksNeeded - tricks, vulnerability)
        }
        else{
            score += contractPoints()
            score += if(score < 100) 50
            else{
                if(vulnerability.isVulnerable(contract.declarer)) 500
                else 300
            }
            if(contract.number == 6){
                score += if(vulnerability.isVulnerable(contract.declarer)) 750
                else 500
            }
            if(contract.number == 7){
                score += if(vulnerability.isVulnerable(contract.declarer)) 1500
                else 1000
            }
            score += overTricks(tricks - tricksNeeded, vulnerability)
        }
        if(contract.declarer == 'E' || contract.declarer == 'W'){
            score *= -1
        }
        return score
    }

    fun toArray() : ArrayList<String>{
        return arrayListOf(pairNS.toString(), pairEW.toString(), contract.toDisplayString(), lead.toString(), tricks.toString(), score.toString())
    }
}