package com.example.iscorebridge

class Card {
    var suit: Char
    var value: Char
    constructor(suit: Char, value: Char){
        this.suit = suit
        this.value = value
    }
    constructor(card:String){
        this.suit = card[1]
        this.value = card[0]
    }
    override fun toString() : String{
        return this.value.toString() + this.suit.toString()
    }
}