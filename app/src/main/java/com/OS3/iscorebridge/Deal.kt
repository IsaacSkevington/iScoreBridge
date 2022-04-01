package com.OS3.iscorebridge;

import android.view.View
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class Deal {

    var n : Hand
    var e : Hand
    var s : Hand
    var w : Hand

    val dlm = "$"

    constructor() : this(Hand("North", ArrayList()),
                        Hand("East", ArrayList()),
                        Hand("South", ArrayList()),
                        Hand("West", ArrayList())
    )

    constructor(n : Hand, e : Hand, s : Hand,  w : Hand){
        this.n = n
        this.e = e
        this.s = s
        this.w = w
    }

    constructor(str : String){
        var params = str.split(dlm)
        n = Hand(params[0])
        e = Hand(params[1])
        s = Hand(params[2])
        w = Hand(params[3])
    }

    fun fromString(str : String){
        var params = str.split(dlm)
        n = Hand(params[0])
        e = Hand(params[1])
        s = Hand(params[2])
        w = Hand(params[3])
    }

    fun getHand(cardinality : Char) : Hand{
        return when (cardinality) {
            'N' -> n
            'E' -> e
            'S' -> s
            'W' -> w
            else -> n
        }
    }

    fun containsCard(card : Card): Boolean {
        var used = getCardsUsedBySuit()
        return used[card.suit]!!.contains(card)
    }

    fun getCardsUsedBySuit() : MutableMap<Char, ArrayList<Card>>{
        var out : MutableMap<Char, ArrayList<Card>> = HashMap<Char, ArrayList<Card>>()
        var nCards = n.getBySuit()
        var eCards = e.getBySuit()
        var wCards = w.getBySuit()
        var sCards = s.getBySuit()
        arrayOf('C', 'D', 'H', 'S').forEach {
            out[it] = ArrayList()
            out[it]!!.addAll(nCards[it]!!)
            out[it]!!.addAll(eCards[it]!!)
            out[it]!!.addAll(sCards[it]!!)
            out[it]!!.addAll(wCards[it]!!)
        }
        return out
    }

    fun display(view : View){
        n.display(view.findViewById(R.id.northView))
        e.display(view.findViewById(R.id.eastView))
        s.display(view.findViewById(R.id.southView))
        w.display(view.findViewById(R.id.westView))
    }

    fun load(fis : FileInputStream){
        var isr = InputStreamReader(fis)
        isr.readLines().forEach(){
            fromString(it)
        }
    }

    fun save(fos : FileOutputStream){
        fos.write(toString().toByteArray())
    }

    override fun toString(): String {
        return n.toString() + dlm + e.toString() + dlm + s.toString() + dlm + w.toString()
    }

}
