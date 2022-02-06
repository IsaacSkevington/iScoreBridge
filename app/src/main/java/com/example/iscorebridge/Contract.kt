package com.example.iscorebridge

class Contract {

    var suit: Char = ' '
    var number: Int = 0
    var doubled: Boolean = false
    var redoubled: Boolean = false
    var declarer: Char = ' '
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
        Contract(parametersAsString[0][0], parametersAsString[1].toInt(), parametersAsString[2][0], parametersAsString[3].toBoolean(), parametersAsString[4].toBoolean())
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

    override fun toString(): String {
        return this.suit.toString() + dlm + this.number.toString() + dlm + this.declarer.toString() + dlm + this.doubled.toString() + dlm + this.redoubled.toString()
    }

}