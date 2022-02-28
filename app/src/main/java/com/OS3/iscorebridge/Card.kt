package com.OS3.iscorebridge


public fun isCard(card : String) : Boolean{
    if(card.length != 2){
        return false
    }
    var number = card[0]
    try{
        var valueInt = number.toString().toInt()
        if(valueInt < 2 || valueInt > 9){
            return false
        }
    }
    catch(e : Exception){
        if(!(number == 'A' || number == 'K' || number == 'Q' || number == 'J' || number == 'T')){
            return false
        }
    }
    var suit = card[1]
    return suit == 'C' || suit == 'D' || suit == 'H' || suit == 'S'

}

class Card (var suit : Char, var value : Char){

    constructor(card:String) : this(card[1], card[0]){
    }
    override fun toString() : String{
        return this.value.toString() + this.suit.toString()
    }
}