package com.example.iscorebridge

class Game {
    lateinit var contract: Contract
    var pairNS: Int = 0
    var pairEW: Int = 0
    var tricks: Int = 0
    var score: Int = -1
    lateinit var lead : Card
    var dlm = "||"

    constructor(contract: Contract, pairNS: Int, pairEW: Int, tricks: Int, lead: Card, vulnerability: Vulnerability){
        this.contract = contract
        this.pairNS = pairNS
        this.pairEW = pairEW
        this.tricks = tricks
        this.lead = lead
        if(this.score == -1){
            calculateScore(vulnerability)
        }

    }

    constructor(game: String){
        var parametersAsString = game.split(dlm)
        this.score = parametersAsString[6].toInt()
        Game(Contract(parametersAsString[0]), parametersAsString[1].toInt(), parametersAsString[2].toInt(), parametersAsString[3].toInt(), Card(parametersAsString[4]), Vulnerability(parametersAsString[5]))
    }

    override fun toString(): String {
        return this.contract.toString() + dlm + this.pairNS.toString() + dlm + this.pairEW.toString() + dlm + this.tricks.toString() + dlm + this.lead.toString() + dlm + this.score.toString()
    }

    private fun goingDown(tricksDown : Int, vulnerability: Vulnerability) : Int{
        var penality: Int = 0
        if(vulnerability.isVulnerable(contract.declarer)){
            if(contract.doubled){
                penality = 200
                for(i in 0 until (tricksDown - 1)){
                    penality += 300
                }
            }
            else if(contract.redoubled){
                penality = 400
                for(i in 0 until (tricksDown - 1)){
                    penality += 600
                }
            }
            else{
                penality = 100 * tricksDown
            }
        }
        else{
            if(contract.doubled){
                penality = 100
                if(tricksDown - 1 != 0){
                    penality += 200
                }
                for(i in 0 until (tricksDown - 2)){
                    penality += 300
                }
            }
            else if(contract.redoubled){
                penality = 200
                if(tricksDown - 1 != 0){
                    penality += 400
                }
                for(i in 0 until (tricksDown - 2)){
                    penality += 300
                }
            }
            else{
                penality = 50 * tricksDown
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
        var contractTricks = contract.number
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

    fun calculateScore(vulnerability: Vulnerability){
        score = 0
        var tricksNeeded = this.contract.number + 6
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
    }
}