package com.OS3.iscorebridge

import android.graphics.Color
import android.graphics.Paint


val CLUBS = Suit('C')
val DIAMONDS = Suit('D')
val HEARTS = Suit('H')
val SPADES = Suit('S')
val NOTRUMPS = Suit('N')
val SUIT_NONE = Suit('X')

val SUITS = arrayOf(CLUBS, DIAMONDS, HEARTS, SPADES)




class Suit : Comparable<Suit>{

    var value : Int

    var suitValueMap = mapOf(
        "C" to 0,
        "D" to 1,
        "H" to 2,
        "S" to 3,
        "NT" to 4,
        "X" to 5
    )

    var valueToSymbolMap = mapOf(
        0 to "♣",
        1 to "♦",
        2 to "♥",
        3 to "♠",
        4 to "NT",
        5 to "X"

    )
    var symbolToValueMap = valueToSymbolMap.entries.associate { (k, v) -> v[0] to k }

    var valueToColorMap = mapOf(
        0 to Color.BLACK,
        1 to Color.RED,
        2 to Color.RED,
        3 to Color.BLACK,
        4 to Color.BLACK,
        5 to Color.BLACK

    )

    var suitValueMapChar = mapOf(
        'C' to 0,
        'D' to 1,
        'H' to 2,
        'S' to 3,
        'N' to 4,
        'X' to 5
    )

    var valueSuitMap = suitValueMap.entries.associate{(k,v)-> v to k}



    constructor(suit : Char) {
        this.value = suitValueMapChar[suit] ?: symbolToValueMap[suit]!!
    }

    constructor(string : String){
      this.value = suitValueMap[string]!!
    }

    constructor(other : Suit){
        this.value = other.value
    }
    constructor(value : Int){
        this.value = value
    }


    override fun toString(): String {
        return valueSuitMap[value]!!
    }

    override fun equals(other: Any?): Boolean {
        return try{
            var otherSuit = other as Suit? ?: return false
            this.value == otherSuit.value
        } catch (e : ClassCastException){
            false
        }
    }

    override fun hashCode(): Int {
        return this.value
    }

    override fun compareTo(other: Suit): Int {
        return this.value.compareTo(other.value)
    }


    fun toSymbol(): String {
        return valueToSymbolMap[value]!!
    }

    fun getPaint(fontSize : Float) : Paint{
        return Paint().also{
            it.color = valueToColorMap[value]!!
            it.textSize = fontSize
        }
    }
}