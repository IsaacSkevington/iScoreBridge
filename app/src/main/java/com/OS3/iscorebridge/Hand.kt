package com.OS3.iscorebridge

import android.view.View
import android.widget.TextView

class Hand {

    var position : String
    var cards : ArrayList<Card>
    val dlm = "|"

    constructor(position : String, cards : ArrayList<Card>){
        this.position = position
        this.cards = cards
    }

    constructor(s: String){
        val hand = s.split(dlm)
        this.position = hand[0]
        this.cards = ArrayList()
        try {
            val c = hand[1].split(", ")
            for (card in c) {
                cards.add(Card(card))
            }
        }
        catch(e : IndexOutOfBoundsException){

        }
    }

    fun removeCard(card : Card){
        this.cards.remove(card)
    }

    fun addCard(card : Card) : Boolean{
        return if(cards.size == 13){
            false
        } else{
            cards.add(card)
            true
        }
    }

    fun getBySuit() : MutableMap<Char, ArrayList<Card>>{
        var map = HashMap<Char, ArrayList<Card>>()
        map['S'] = ArrayList()
        map['H'] = ArrayList()
        map['D'] = ArrayList()
        map['C'] = ArrayList()

        cards.forEach {
            map[it.suit]!!.add(it)
        }
        return map
    }

    fun display(view : View){

        var suits = getBySuit()
        view.findViewById<TextView>(R.id.spadeView).text = cardListToString(suits['S']!!)
        view.findViewById<TextView>(R.id.heartView).text = cardListToString(suits['H']!!)
        view.findViewById<TextView>(R.id.diamondView).text = cardListToString(suits['D']!!)
        view.findViewById<TextView>(R.id.clubView).text = cardListToString(suits['C']!!)
    }

    override fun toString(): String {
        var cardListString = cards.toString()
        cardListString = cardListString.substring(1, cardListString.length - 1)
        return this.position + dlm + cardListString
    }

}