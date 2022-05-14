package com.OS3.iscorebridge

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.view.View
import android.widget.TextView

class Hand {

    var cardinality : Cardinality
    var cards : ArrayList<Card>
    val dlm = "|"

    private val characterWidth = 1.5f
    private val characterHeight = 1
    private val FONT_SIZE = 12f

    constructor(cardinality: Cardinality, cards : ArrayList<Card>){
        this.cardinality = cardinality
        this.cards = cards
    }

    fun copy(other : Hand){
        this.cardinality = other.cardinality
        this.cards = other.cards
    }

    constructor(s: String){
        val hand = s.split(dlm)
        this.cardinality = Cardinality(hand[0])
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

    fun clear(){
        this.cards = ArrayList()
    }

    fun getHCP() : Int{
        var points = 0
        cards.forEach {
            points += it.HCP()
        }
        return points
    }

    fun getDistributionPoints() : Int{
        var points = 0
        var cardsBySuit = getBySuit()
        cardsBySuit.forEach {
            points += getDistributionPoints(it.value)
        }
        return points
    }

    fun getDistributionPoints(cds : ArrayList<Card>) : Int{
        return when(cds.size){
            0 -> 5
            1 -> 3
            2 -> 1
            else -> 0
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

    fun getBySuit() : MutableMap<Suit, ArrayList<Card>>{
        var map = HashMap<Suit, ArrayList<Card>>()
        SUITS.forEach {
            map[it] = ArrayList()
        }

        cards.forEach {
            map[it.suit]!!.add(it)
        }
        return map
    }

    @SuppressLint("SetTextI18n")
    fun display(view : View){

        var suits = getBySuit()
        view.findViewById<TextView>(R.id.spadeView).text = cardListToString(suits[SPADES]!!)
        view.findViewById<TextView>(R.id.heartView).text = cardListToString(suits[HEARTS]!!)
        view.findViewById<TextView>(R.id.diamondView).text = cardListToString(suits[DIAMONDS]!!)
        view.findViewById<TextView>(R.id.clubView).text = cardListToString(suits[CLUBS]!!)
        view.findViewById<TextView>(R.id.statsView).text = "(HCP: ${getHCP().toString()} Dist: ${getDistributionPoints().toString()})"
    }

    override fun toString(): String {
        var cardListString = cards.toString()
        cardListString = cardListString.substring(1, cardListString.length - 1)
        return this.cardinality.toString() + dlm + cardListString
    }

    private fun getCharacterWidth(fontSize : Float) : Float{
        return characterWidth * fontSize
    }
    private fun getCharacterHeight(fontSize : Float) : Float{
        return characterHeight * fontSize
    }

    fun getMaxSuitLength() : Int{
        var suits = getBySuit()
        var sizes = ArrayList<Int>()
        suits.forEach{
            sizes.add(it.value.size)
        }
        sizes.sortDescending()
        return sizes.last()
    }

    fun toPDF(page: PdfDocument.Page, x: Float, y: Float) {
        var tPaint = Paint().also { it.textSize = FONT_SIZE }
        var start = x - (getMaxSuitLength()/2 * getCharacterWidth(FONT_SIZE))
        var suits = getBySuit()
        var line = 1
        SUITS.forEach {
            page.canvas.drawText(it.toSymbol(), start, y + getCharacterWidth(FONT_SIZE) * line, it.getPaint(FONT_SIZE))
            var text = try {
                suits[it].toString().substring(1, suits[it].toString().length - 1)
            }
            catch(e : Exception){
                "-"
            }
            page.canvas.drawText(
                text,
                start,
                y + getCharacterWidth(FONT_SIZE) * line,
                tPaint
            )
        }
    }

}