package com.OS3.iscorebridge

enum class ContractType{
    PARTSCORE,
    GAME,
    SLAM
}


class Contract {

    var suit: Suit
    var number: Int
    var doubled: Boolean
    var redoubled: Boolean
    var declarer: Cardinality
    var dlm = "|"
    var type : ContractType


    constructor(suit: Suit, number: Int, declarer: Cardinality, doubled: Boolean = false, redoubled: Boolean = false){
        this.suit = suit
        this.number = number
        this.doubled = doubled
        this.redoubled = redoubled
        this.declarer = declarer
        this.type = _getType()
    }

    constructor(contract: String){
        val parametersAsString = contract.split(dlm)
        this.suit = Suit(parametersAsString[0][0])
        this.number = parametersAsString[1].toInt()
        this.declarer = Cardinality(parametersAsString[2])
        this.doubled = parametersAsString[3].toBoolean()
        this.redoubled = parametersAsString[4].toBoolean()
        this.type = _getType()
    }

    fun isMinor() : Boolean{
        return suit == CLUBS || suit == DIAMONDS
    }
    fun isMajor() : Boolean{
        return suit == HEARTS || suit == SPADES
    }
    fun isNoTrump() : Boolean{
        return suit == NOTRUMPS
    }

    fun toDisplayString(): String{
        return toDisplayString(true)
    }

    fun toDisplayString(displayDeclarer : Boolean) : String{
        var out = this.number.toString() + suit.toString()
        if(doubled){
            out += "X"
        }
        else if(redoubled){
            out += "XX"
        }
        if(displayDeclarer) out += " (${declarer.toDisplayString()})"
        return out
    }

    fun points() : Int{
        val contractTricks = number
        if(doubled){
            if(isMinor()){
                return contractTricks * 40 + 100
            }
            if(isMajor()){
                return contractTricks * 60 + 100
            }
            if(isNoTrump()){
                return 80 + 60*(contractTricks-1) + 100
            }
        }
        else if(redoubled){
            if(isMinor()){
                return contractTricks * 80 + 50
            }
            if(isMajor()){
                return contractTricks * 120 + 50
            }
            if(isNoTrump()){
                return 160 + 120*(contractTricks-1) + 50
            }
        }
        else{
            if(isMinor()){
                return contractTricks * 20
            }
            if(isMajor()){
                return contractTricks * 30
            }
            if(isNoTrump()){
                return 40 + 30*(contractTricks-1)
            }
        }
        return 0
    }

    private fun goingDown(tricksDown : Int, vulnerability: Vulnerability) : Int{
        var penality: Int
        if(vulnerability.isVulnerable(declarer)){
            when {
                doubled -> {
                    penality = 200
                    for(i in 0 until (tricksDown - 1)){
                        penality += 300
                    }
                }
                redoubled -> {
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
                doubled -> {
                    penality = 100
                    if(tricksDown - 1 != 0){
                        penality += 200
                    }
                    for(i in 0 until (tricksDown - 2)){
                        penality += 300
                    }
                }
                redoubled -> {
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

    fun overTricks(tricksOver : Int, vulnerability: Vulnerability) : Int{
        if(!doubled && !redoubled){
            return if(isMinor()) 20 * tricksOver
            else 30 * tricksOver
        }
        else {

            if (vulnerability.isVulnerable(declarer)) {
                if (doubled) {
                    return tricksOver * 200
                } else if (redoubled) {
                    return tricksOver * 400
                }
            } else {
                if (doubled) {
                    return tricksOver * 100
                } else if (redoubled) {
                    return tricksOver * 200
                }
            }
        }
        return 0
    }

    fun calculateScore(tricks : Int, vulnerability: Vulnerability) : Int{
        var score = 0
        val tricksNeeded = number + 6
        if(tricksNeeded > tricks){
            score = -1 * goingDown(tricksNeeded - tricks, vulnerability)
        }
        else{
            score += points()
            score += if(score < 100) 50
            else{
                if(vulnerability.isVulnerable(declarer)) 500
                else 300
            }
            if(number == 6){
                score += if(vulnerability.isVulnerable(declarer)) 750
                else 500
            }
            if(number == 7){
                score += if(vulnerability.isVulnerable(declarer)) 1500
                else 1000
            }
            score += overTricks(tricks - tricksNeeded, vulnerability)
        }
        if(declarer == EAST || declarer == WEST){
            score *= -1
        }
        return score
    }

    fun declaredBy(number : Int, pairNS : Int) : Boolean{
        return if(number == pairNS){
            declarer == NORTH || declarer == SOUTH
        } else{
            declarer == EAST || declarer == WEST
        }
    }



    private fun _getType() : ContractType{
        return when(number){
            6, 7 -> ContractType.SLAM
            5 -> ContractType.GAME
            4 -> {
                when(suit){
                    CLUBS, DIAMONDS -> ContractType.PARTSCORE
                    else -> ContractType.GAME
                }
            }
            3 -> {
                if(suit == NOTRUMPS){
                    ContractType.GAME
                }
                else{
                    ContractType.PARTSCORE
                }
            }
            else -> ContractType.PARTSCORE
        }
    }



    override fun toString(): String {
        return this.suit.toString() + dlm + this.number.toString() + dlm + this.declarer.toString() + dlm + this.doubled.toString() + dlm + this.redoubled.toString()
    }

}