package com.OS3.iscorebridge


import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView


val PASS = Bid(0)
val DOUBLE = Bid(-1)
val REDOUBLE = Bid(-2)







class Bid : Comparable<Bid>{


    var specialBidMap = mapOf(
        0 to "Pass",
        -1 to "X",
        -2 to "XX"
    )

    val value : Int
    var suit : Suit?
    val dlm = "^*^"

    constructor(value : Int) : this(value, null)

    constructor(other : Bid) : this(other.value, other.suit)

    constructor(value : Int, suit : Suit?){
        this.value = value
        this.suit = suit
    }

    constructor(s : String){
        var params = s.split(dlm)
        this.value = params[0].toInt()
        try {
            this.suit = Suit(params[1])
        }
        catch (e : IndexOutOfBoundsException){
            this.suit = null
        }
    }


    override fun compareTo(other: Bid): Int {
        if(this.value == 0 && other.value == 0){
            return 0
        }
        val ret = this.value.compareTo(other.value)
        if(ret != 0){
            return ret
        }
        if(this.suit == null || other.suit == null)return 0
        return this.suit!!.compareTo(other.suit!!)
    }

    override fun toString(): String {
        if(suit == null) return value.toString()
        return value.toString() + dlm + suit
    }

    fun toDisplayString() : String{
        return if(suit == null){
            specialBidMap[value]!!
        }
        else{
            value.toString() + suit.toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        return try{
            var otherBid = other as Bid
            this.suit == otherBid.suit && this.value == otherBid.value
        } catch(e : ClassCastException){
            false
        }
    }

    fun display(view : TableRow){
        var text = TextView(view.context)
        text.textSize = 20f
        text.text = this.toDisplayString()
        text.gravity = Gravity.CENTER
        view.addView(text)
    }

}