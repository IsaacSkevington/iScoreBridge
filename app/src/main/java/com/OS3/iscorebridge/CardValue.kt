package com.OS3.iscorebridge


val ACE = CardValue('A')
val KING = CardValue('K')
val QUEEN = CardValue('Q')
val JACK = CardValue('J')
val TEN = CardValue('T')
val NINE = CardValue('9')
val EIGHT = CardValue('8')
val SEVEN = CardValue('7')
val SIX = CardValue('6')
val FIVE = CardValue('5')
val FOUR = CardValue('4')
val THREE = CardValue('3')
val TWO = CardValue('2')

val CARDVALUES = arrayOf(TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE)




class IncorrectValueException(value : Char) : IllegalArgumentException("The value ${value.toString()} is not a valid card value")

class CardValue : Comparable<CardValue>{

    val charToVal = mapOf<Char, Int>(
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

    private var valToChar = charToVal.entries.associate{(k,v)-> v to k}

    var intValue : Int

    constructor(char : Char) {
        try {
            intValue = charToVal[char]!!
        }
        catch(e : NullPointerException){
            throw IncorrectValueException(char)
        }
    }

    constructor(cv : CardValue){
        this.intValue = cv.intValue
    }

    override fun toString(): String {
        return valToChar[intValue].toString()
    }

    override fun hashCode(): Int {
        return intValue
    }

    override fun equals(other: Any?): Boolean {
        return try{
            var otherCV = other as CardValue
            this.intValue == otherCV.intValue
        } catch(e : ClassCastException){
            false
        }
    }

    fun getHCP() : Int{
        var HCP = intValue - 10
        return if(HCP < 0){
            0
        }
        else{
            HCP
        }

    }

    override fun compareTo(other: CardValue): Int {
        return this.intValue.compareTo(other.intValue)
    }

}