package com.OS3.iscorebridge

import android.graphics.pdf.PdfDocument
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader


class Deal(var n : Hand = Hand(Cardinality(NORTH), ArrayList()),
           var e : Hand = Hand(Cardinality(EAST), ArrayList()),
           var s : Hand = Hand(Cardinality(SOUTH), ArrayList()),
           var w : Hand = Hand(Cardinality(WEST), ArrayList()),
           var number: Int = 0) : Exportable("game", ".deal") {


    val DECK : Array<Card> = getDeck()


    fun getDeck() : Array<Card>{
        var array = ArrayList<Card>()

        for(suit in SUITS){
            for(value in CARDVALUES){
                array.add(Card(suit, value))
            }
        }
        return array.toTypedArray()
    }

    val dlm = "$"


    constructor(str : String) : this(){
        var params = str.split(dlm)
        n = Hand(params[0])
        e = Hand(params[1])
        s = Hand(params[2])
        w = Hand(params[3])
        number = params[4].toInt()
    }

    fun fromString(str : String){
        var params = str.split(dlm)
        n = Hand(params[0])
        e = Hand(params[1])
        s = Hand(params[2])
        w = Hand(params[3])
    }



    fun getHand(cardinality : Cardinality) : Hand{
        return when (cardinality) {
            NORTH -> n
            EAST -> e
            SOUTH -> s
            WEST -> w
            else -> n
        }
    }

    fun clear(){
        n.clear()
        e.clear()
        s.clear()
        w.clear()
    }

    fun random(){
        clear()
        var shuffledDeck =  DECK.copyOf()
        shuffledDeck.shuffle()
        var c = 0
        for(cardinality in CARDINALITIES){
            for(i in 0 until 13){
                getHand(cardinality).addCard(shuffledDeck[c++])
            }
        }
    }

    fun validate() : Boolean{
        var used = getCardsUsedBySuit()
        var out = true
        SUITS.forEach {
            out = out && used[it]!!.size == 13
        }
        return out
    }

    fun containsCard(card : Card): Boolean {
        var used = getCardsUsedBySuit()
        return used[card.suit]!!.contains(card)
    }

    fun getCardsUsedBySuit() : MutableMap<Suit, ArrayList<Card>>{
        var out : MutableMap<Suit, ArrayList<Card>> = HashMap()
        var nCards = n.getBySuit()
        var eCards = e.getBySuit()
        var wCards = w.getBySuit()
        var sCards = s.getBySuit()
        SUITS.forEach {
            out[it] = ArrayList()
            out[it]!!.addAll(nCards[it]!!)
            out[it]!!.addAll(eCards[it]!!)
            out[it]!!.addAll(sCards[it]!!)
            out[it]!!.addAll(wCards[it]!!)
        }
        return out
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun display(view : View){
        n.display(view.findViewById(R.id.northView))
        e.display(view.findViewById(R.id.eastView))
        s.display(view.findViewById(R.id.southView))
        w.display(view.findViewById(R.id.westView))
        view.findViewById<FloatingActionButton>(R.id.saveDealButton).setOnClickListener {
            export()
        }
    }

    override fun read(fileInputStream : FileInputStream) : Boolean{
        var isr = InputStreamReader(fileInputStream)
        return try {
            isr.readLines().forEach() {
                fromString(it)
            }
            true
        } catch (e : Exception){
            false
        }
    }

    override fun write(fileOutputStream : FileOutputStream){
        fileOutputStream.write(toString().toByteArray())
    }

    fun toPDF(page : PdfDocument.Page, x : Float, y : Float){
        n.toPDF(page, x, y)
        e.toPDF(page, x+100, y + 100)
        s.toPDF(page, x, y+200)
        w.toPDF(page, x-100, y+100)
    }

    override fun toString(): String {
        return n.toString() + dlm + e.toString() + dlm + s.toString() + dlm + w.toString() + dlm + number.toString()
    }

}
