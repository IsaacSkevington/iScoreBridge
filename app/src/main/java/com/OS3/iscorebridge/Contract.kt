package com.OS3.iscorebridge

class Contract {

    var suit: Char
    var number: Int
    var doubled: Boolean
    var redoubled: Boolean
    var declarer: Char
    var dlm = "|"


    constructor(suit: Char, number: Int, declarer: Char, doubled: Boolean = false, redoubled: Boolean = false){
        this.suit = suit
        this.number = number
        this.doubled = doubled
        this.redoubled = redoubled
        this.declarer = declarer
    }

    constructor(contract: String){
        var parametersAsString = contract.split(dlm)
        this.suit = parametersAsString[0][0]
        this.number = parametersAsString[1].toInt()
        this.declarer = parametersAsString[2][0]
        this.doubled = parametersAsString[3].toBoolean()
        this.redoubled = parametersAsString[4].toBoolean()
    }

    public fun isMinor() : Boolean{
        return suit == 'C' || suit == 'D'
    }
    public fun isMajor() : Boolean{
        return suit == 'H' || suit == 'S'
    }
    public fun isNoTrump() : Boolean{
        return suit == 'N'
    }

    public fun toDisplayString() : String{
        var out = this.number.toString() + this.suit.toString()
        if(doubled){
            out += "X"
        }
        else if(redoubled){
            out += "XX"
        }
        return out
    }

    override fun toString(): String {
        return this.suit.toString() + dlm + this.number.toString() + dlm + this.declarer.toString() + dlm + this.doubled.toString() + dlm + this.redoubled.toString()
    }

}