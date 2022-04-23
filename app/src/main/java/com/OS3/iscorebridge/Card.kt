package com.OS3.iscorebridge



fun cardListToString(list : ArrayList<Card>) : String{
    var out = ""
    list.sortDescending()
    list.forEach {
        out += it.value.toString() + " "
    }
    return if(out != "") out.substring(0, out.length - 1)
    else "-"
}


fun isCard(card : String) : Boolean{
    if(card.length != 2){
        return false
    }
    try{
        CardValue(card[0])
    }
    catch(e : IncorrectValueException){
        return false
    }
    return SUITS.contains(Suit(card[1]))

}

class Card (var suit : Suit, var value : CardValue) : Comparable<Card>{

    constructor(card:String) : this(Suit(card[1]), CardValue(card[0]))

    override fun compareTo(other: Card): Int {
        return value.compareTo(other.value)
    }

    fun HCP() : Int{
        return value.getHCP()
    }

    override fun toString() : String{
        return this.value.toString() + this.suit.toString()
    }


    override fun equals(other: Any?): Boolean {
        return try{
            var o = other as Card
            o.value == this.value && o.suit == this.suit
        } catch(c : ClassCastException){
            false
        }
    }
}