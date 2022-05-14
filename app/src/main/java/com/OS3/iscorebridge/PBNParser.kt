package com.OS3.iscorebridge

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class PBNParser {

    fun isEmpty(line : String) : Boolean{
        line.forEach {
            if(it != '\t' && it != '\n' && it != ' '){
                return false
            }
        }
        return true
    }

    fun getTag(line : String) : String{
        var contents = line.split(" ")
        return contents[0]
    }

    fun parseHand(hand : String, cardinality: Cardinality) : Hand{
        var suits = hand.split(".")
        var cards = ArrayList<Card>()
        suits[0].forEach {
            cards.add(Card(it + "S" ))
        }
        suits[1].forEach {
            cards.add(Card(it + "H" ))
        }
        suits[2].forEach {
            cards.add(Card(it + "D" ))
        }
        suits[3].forEach {
            cards.add(Card(it + "C" ))
        }
        return Hand(cardinality, cards)
    }

    fun parseDeal(deal : String) : Deal{
        var currentHand = Cardinality(deal[0])
        var reducedDeal = deal.substring(2)
        var hands = reducedDeal.split(" ")
        var deal = Deal()
        hands.forEach {
            deal.getHand(currentHand).copy(parseHand(it, currentHand))
            currentHand.increment()
        }
        return deal

    }
    fun removeTag(line : String) : String{
        var tag = getTag(line)
        return line.substring(tag.length + 2, line.length - 2)
    }

    fun parseGame(lines : ArrayList<String>) : PBNGame{
        var deal = Deal()
        var dealer : Cardinality? = null
        var boardNumber = 0
        lines.forEach {
            when(getTag(it).lowercase()){
                "deal" -> deal = parseDeal(removeTag(it))
                "board" -> boardNumber = removeTag(it).toInt()
                "dealer"-> dealer = Cardinality(removeTag(it))
            }

        }
        return PBNGame(deal, boardNumber, dealer!!)
    }

    fun parse(stream : InputStreamReader, file : PBNFile){

        var lines : ArrayList<String>? = null
        stream.forEachLine {
            if(isEmpty(it)){
                if(lines != null){
                    file.games.add(parseGame(lines!!))
                }
                lines = ArrayList()
            }
            else{
                lines!!.add(it)
            }

        }

    }
}

class PBNFile : Exportable("hand", ".pbn"){


    var games : ArrayList<PBNGame> = ArrayList()


    override fun write(fileOutputStream: FileOutputStream) {
        TODO("Not yet implemented")
    }

    override fun read(fileInputStream: FileInputStream): Boolean {
        var fisr = InputStreamReader(fileInputStream)
        var parser = PBNParser()
        parser.parse(fisr, this)
        return true
    }

}

data class PBNGame(var deal : Deal, var boardNumber : Int, var dealer : Cardinality){

}


