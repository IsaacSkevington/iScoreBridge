package com.OS3.iscorebridge


val cardMap = mapOf<Char, Int>(
    '2' to 2,
    '3' to 3,
    '4' to 4,
    '5' to 5,
    '6' to 6,
    '7' to 7,
    '8' to 8,
    '9' to 9,
    'T' to 10,
    'J' to 11,
    'Q' to 12,
    'K' to 13,
    'A' to 14
)
class CardComparator : Comparator<Card>{
    override fun compare(c0: Card?, c1: Card?): Int {
        var val0 = cardMap[c0!!.value]!!
        var val1 = cardMap[c1!!.value]!!
        return val1 - val0
    }

}

fun sortCardsArray(list : ArrayList<Card>) : ArrayList<Card>{
    list.sortWith(CardComparator())
    return list
}

fun cardListToString(list : ArrayList<Card>) : String{
    var out = ""
    var newList = sortCardsArray(list)
    newList.forEach {
        out += it.value.toString() + " "
    }
    return if(out != "") out.substring(0, out.length - 1)
    else "-"
}


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

class Card (var suit : Char, var value : Char){

    constructor(card:String) : this(card[1], card[0])

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