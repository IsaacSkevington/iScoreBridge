package com.OS3.iscorebridge


fun isCard(card : String) : Boolean{
    if(card.length != 2){
        return false
    }
    val number = card[0]
    try{
        val valueInt = number.toString().toInt()
        if(valueInt < 2 || valueInt > 9){
            return false
        }
    }
    catch(e : Exception){
        if(!(number == 'A' || number == 'K' || number == 'Q' || number == 'J' || number == 'T')){
            return false
        }
    }
    val suit = card[1]
    return suit == 'C' || suit == 'D' || suit == 'H' || suit == 'S'

}

class Card (private var suit : Char, private var value : Char){

    constructor(card:String) : this(card[1], card[0])

    override fun toString() : String{
        return this.value.toString() + this.suit.toString()
    }
}