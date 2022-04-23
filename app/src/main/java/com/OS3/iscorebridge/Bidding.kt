package com.OS3.iscorebridge

import android.view.Gravity
import android.view.View
import android.view.View.*
import android.widget.*


fun getBid(button : Button) : Bid{
    button.text.toString().let {
        return Bid(it.substring(0, 1).toInt(), Suit(it[1]))
    }
}

fun getBid(view : View, id : Int) : Bid{
    return getBid(view.findViewById(id))
}

class Bidding{

    var bidButtonList = arrayListOf<Int>(
        R.id.bidding_1_club,
        R.id.bidding_2_club,
        R.id.bidding_3_club,
        R.id.bidding_4_club,
        R.id.bidding_5_club,
        R.id.bidding_6_club,
        R.id.bidding_7_club,
        R.id.bidding_1_diamond,
        R.id.bidding_2_diamond,
        R.id.bidding_3_diamond,
        R.id.bidding_4_diamond,
        R.id.bidding_5_diamond,
        R.id.bidding_6_diamond,
        R.id.bidding_7_diamond,
        R.id.bidding_1_heart,
        R.id.bidding_2_heart,
        R.id.bidding_3_heart,
        R.id.bidding_4_heart,
        R.id.bidding_5_heart,
        R.id.bidding_6_heart,
        R.id.bidding_7_heart,
        R.id.bidding_1_spade,
        R.id.bidding_2_spade,
        R.id.bidding_3_spade,
        R.id.bidding_4_spade,
        R.id.bidding_5_spade,
        R.id.bidding_6_spade,
        R.id.bidding_7_spade,
        R.id.bidding_1_NT,
        R.id.bidding_2_NT,
        R.id.bidding_3_NT,
        R.id.bidding_4_NT,
        R.id.bidding_5_NT,
        R.id.bidding_6_NT,
        R.id.bidding_7_NT,
    )

    val dlm = "^^^"
    var bids : ArrayList<Bid> = ArrayList()
    var dealer : Cardinality
    var double : Cardinality = Cardinality(EMPTYCARDINALITY)
    var redouble : Cardinality = Cardinality(EMPTYCARDINALITY)

    constructor(dealer : Cardinality){
        this.dealer = dealer
    }

    constructor(s : String){

        var params = s.split(dlm)
        this.dealer = Cardinality(params[0])
        try {
            if(params[1]!="") {
                val b = params[1].split(", ")
                for (bid in b) {
                    bids.add(Bid(bid))
                }
                double = Cardinality(params[2])
                redouble = Cardinality(params[3])
            }
        }
        catch(e : IndexOutOfBoundsException){

        }
    }

    override fun toString(): String {
        var bidsListString = bids.toString()
        bidsListString = bidsListString.substring(1, bidsListString.length - 1)
        return dealer.toString() + dlm + bidsListString + dlm + double.toString() + dlm + redouble.toString()
    }

    fun currentHighestBid() : Bid{
        var current = Bid(PASS)
        bids.forEach {
            if(it > current){
                current = it
            }
        }
        return current
    }

    fun currentDoubler() : Cardinality{
        for(i in bids.size - 1 downTo 0){
            when {
                bids[i] == DOUBLE -> {
                    return getCardinalityByBidNumber(i)
                }
                bids[i] == REDOUBLE -> {

                }
                else -> {
                    return Cardinality(EMPTYCARDINALITY)
                }
            }

        }
        return Cardinality(EMPTYCARDINALITY)
    }

    fun currentRedoubler() : Cardinality{
        for(i in bids.size - 1 downTo 0){
            when {
                bids[i] == REDOUBLE -> {
                    return getCardinalityByBidNumber(i)
                }
                bids[i] == PASS -> {

                }
                else -> {
                    return Cardinality(EMPTYCARDINALITY)
                }
            }

        }
        return Cardinality(EMPTYCARDINALITY)
    }

    fun currentHighestBidder() : Cardinality{
        var current = PASS
        var highestBidder = Cardinality(dealer)
        var currentBidder = Cardinality(dealer)
        bids.forEach {
            if(it > current){
                current = it
                highestBidder = Cardinality(currentBidder)
            }
            currentBidder.increment()
        }
        return highestBidder
    }

    fun updateBiddingBox(box : View){
        if(bids.size > 0) {
            bidButtonList.forEach {
                if(getBid(box, it) > currentHighestBid()){
                    box.findViewById<Button>(it).visibility = VISIBLE
                    box.findViewById<Button>(it).isClickable = true
                }
                else{
                    if(getBid(box, it).value != currentHighestBid().value || currentHighestBid().suit == NOTRUMPS ){
                        box.findViewById<Button>(it).visibility = GONE
                        box.findViewById<Button>(it).isClickable = false
                    }
                    else {
                        box.findViewById<Button>(it).visibility = INVISIBLE
                        box.findViewById<Button>(it).isClickable = false
                    }
                }
            }
        }
        else{
            bidButtonList.forEach {
                box.findViewById<Button>(it).visibility = VISIBLE
                box.findViewById<Button>(it).isClickable = true
            }
        }
        box.findViewById<ScrollView>(R.id.biddingBoxScrollView).post{
            box.findViewById<ScrollView>(R.id.biddingBoxScrollView).fullScroll(FOCUS_UP)
        }
    }

    fun canBid() : Boolean{
        return (getCurrentPasses() < 3) || (getCurrentPasses() == 3 && bids.size == 3)
    }



    fun add(bid : Bid){
        if(canBid()){
            if(bid == DOUBLE){
                var myCardinality = getCurrentCardinality()
                if(double == EMPTYCARDINALITY && currentHighestBid() != PASS && myCardinality.isOpposition(currentHighestBidder())){
                    bids.add(bid)
                    double = myCardinality
                }
            }
            else if(bid == REDOUBLE){
                var myCardinality = getCurrentCardinality()
                if(double != EMPTYCARDINALITY && redouble == EMPTYCARDINALITY && double.isOpposition(myCardinality)){
                    bids.add(bid)
                    redouble = myCardinality
                }
            }
            else{
                if(bid != PASS){
                    double = Cardinality(EMPTYCARDINALITY)
                    redouble = Cardinality(EMPTYCARDINALITY)
                }
                bids.add(bid)
            }
        }
    }

    fun getCardinalityByBidNumber(num : Int) : Cardinality{
        return Cardinality(dealer).also { it.add(num) }
    }

    fun getCurrentCardinality() : Cardinality{
        return getCardinalityByBidNumber(bids.size)
    }


    fun pop(){
        if(bids.size > 0){
            bids.removeLast()
            double = currentDoubler()
            redouble = currentRedoubler()
        }
    }

    fun getCurrentPasses() : Int{
        var currentPasses = 0
        for(i in (bids.size - 1) downTo  0){
            if(bids[i] == PASS){
                currentPasses++
            }
            else{
                break
            }
        }
        return currentPasses
    }

    fun allPass(){
        var numberNeeded = 3 - getCurrentPasses()
        for(i in 0 until numberNeeded){
            this.bids.add(Bid(PASS))
        }
        if(this.bids.size == 3){
            this.bids.add(Bid(PASS))
        }
    }

    fun updateBiddingTable(view : View){

        var table = view.findViewById<TableLayout>(R.id.biddingView)
        var titleRow = table.findViewById<TableRow>(R.id.biddingTitleRow)
        table.removeAllViews()
        table.addView(titleRow)
        var currentCardinality = Cardinality(dealer)
        var currentRow = TableRow(view.context)
        var blankColumns = currentCardinality.differenceBetween(NORTH)
        for(i in 0 until blankColumns){
            currentRow.addView(TextView(view.context))
        }
        bids.forEach {
            it.display(currentRow)
            currentCardinality.increment()
            if(currentCardinality == NORTH){
                table.addView(currentRow)
                currentRow = TableRow(view.context)
            }
        }
        if(canBid()) {
            var text = TextView(view.context)
            text.gravity = Gravity.CENTER
            text.text = "?"
            text.textSize = 20f
            currentRow.addView(text)
        }
        table.addView(currentRow)
        try {
            view.findViewById<ScrollView>(R.id.biddingScrollView).post {
                view.findViewById<ScrollView>(R.id.biddingScrollView).fullScroll(FOCUS_DOWN)
            }
        }
        catch(e : NullPointerException){}

    }

    fun display(view : View){
        updateBiddingTable(view)
        updateBiddingBox(view)

    }

}