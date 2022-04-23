package com.OS3.iscorebridge

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HandConstructorFragment : DialogFragment() {

    private lateinit var background : Drawable
    var deal = Deal()

    private var currentCardinality = NORTH
    private var currentSuit = Suit(CLUBS)
    private lateinit var currentView : View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deal.setupForFragment(this, {}, {
            colourCards(currentView)
            deal.display(currentView)
        }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_hand_constructor, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun resetSuitBackgrounds(view : View){
        view.findViewById<ImageButton>(R.id.card_button_club).background = background
        view.findViewById<ImageButton>(R.id.card_button_heart).background = background
        view.findViewById<ImageButton>(R.id.card_button_diamond).background = background
        view.findViewById<ImageButton>(R.id.card_button_spade).background = background
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun resetCardBackgrounds(view : View){
        view.findViewById<Button>(R.id.card_button_2).background = background
        view.findViewById<Button>(R.id.card_button_3).background = background
        view.findViewById<Button>(R.id.card_button_4).background = background
        view.findViewById<Button>(R.id.card_button_5).background = background
        view.findViewById<Button>(R.id.card_button_6).background = background
        view.findViewById<Button>(R.id.card_button_7).background = background
        view.findViewById<Button>(R.id.card_button_8).background = background
        view.findViewById<Button>(R.id.card_button_9).background = background
        view.findViewById<Button>(R.id.card_button_10).background = background
        view.findViewById<Button>(R.id.card_button_J).background = background
        view.findViewById<Button>(R.id.card_button_Q).background = background
        view.findViewById<Button>(R.id.card_button_K).background = background
        view.findViewById<Button>(R.id.card_button_A).background = background

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun resetCardinalityBackgrounds(view: View){
        view.findViewById<TextView>(R.id.northTitle).setBackgroundColor(Color.TRANSPARENT)
        view.findViewById<TextView>(R.id.eastTitle).setBackgroundColor(Color.TRANSPARENT)
        view.findViewById<TextView>(R.id.southTitle).setBackgroundColor(Color.TRANSPARENT)
        view.findViewById<TextView>(R.id.westTitle).setBackgroundColor(Color.TRANSPARENT)
    }


    fun cardToButtonId(card : Card) : Int{
        return when(card.value){
            TWO -> R.id.card_button_2
            THREE -> R.id.card_button_3
            FOUR -> R.id.card_button_4
            FIVE -> R.id.card_button_5
            SIX -> R.id.card_button_6
            SEVEN -> R.id.card_button_7
            EIGHT -> R.id.card_button_8
            NINE -> R.id.card_button_9
            TEN -> R.id.card_button_10
            JACK -> R.id.card_button_J
            QUEEN -> R.id.card_button_Q
            KING -> R.id.card_button_K
            ACE -> R.id.card_button_A
            else -> 0
        }
    }

    fun cardinalityToTextId(cardinality: Cardinality) : Int{
        return when(cardinality){
            NORTH -> R.id.northTitle
            EAST -> R.id.eastTitle
            SOUTH -> R.id.southTitle
            WEST -> R.id.westTitle
            else -> 0
        }
    }

    fun suitToButtonId(suit : Suit) : Int{
        return when(suit){
            CLUBS -> R.id.card_button_club
            DIAMONDS -> R.id.card_button_diamond
            HEARTS -> R.id.card_button_heart
            SPADES -> R.id.card_button_spade
            else -> 0
        }
    }

    fun colourCard(view: View, card : Card, color : Int){
        view.findViewById<Button>(cardToButtonId(card)).setBackgroundColor(color)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun colourCards(view : View){
        resetCardBackgrounds(view)
        var usedCards = deal.getCardsUsedBySuit()[currentSuit]
        usedCards!!.forEach{
            colourCard(view, it, Color.DKGRAY)
        }
        var cards = deal.getHand(currentCardinality).getBySuit()[currentSuit]
        cards!!.forEach {
            colourCard(view, it, Color.CYAN)
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun suitPressed(view : View, suit : Suit, button : View){
        resetSuitBackgrounds(view)
        currentSuit = Suit(suit)
        button.setBackgroundColor(Color.CYAN)
        colourCards(view)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cardPressed(view: View, card: CardValue){
        var pressed = Card(currentSuit, card)
        if(view.findViewById<Button>(cardToButtonId(pressed)).background == background){
            if(deal.containsCard(pressed) || !deal.getHand(currentCardinality).addCard(pressed)) {
                return
            }
        }
        else{
            deal.getHand(currentCardinality).removeCard(pressed)
        }
        colourCards(view)
        deal.display(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun cardinalityPressed(view : View, cardinality : Cardinality, textView : View){
        resetCardinalityBackgrounds(view)
        currentCardinality = cardinality
        textView.setBackgroundColor(Color.CYAN)
        colourCards(view)
    }




    private fun load(){
        deal.import()
    }

    var onCancel = {
        findNavController().navigate(R.id.handConstructorToHome)
    }

    var onCreate = {
        findNavController().navigate(R.id.handConstructorToHome)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        background = view.findViewById<ImageButton>(R.id.card_button_club).background
        deal.display(view)
        suitPressed(view, CLUBS, view.findViewById(R.id.card_button_club))
        cardinalityPressed(view, NORTH, view.findViewById(R.id.northTitle))

        view.findViewById<ImageButton>(R.id.card_button_club).setOnClickListener {
            suitPressed(view, CLUBS, it)
        }
        view.findViewById<ImageButton>(R.id.card_button_diamond).setOnClickListener {
            suitPressed(view, DIAMONDS, it)
        }
        view.findViewById<ImageButton>(R.id.card_button_heart).setOnClickListener {
            suitPressed(view, HEARTS, it)
        }
        view.findViewById<ImageButton>(R.id.card_button_spade).setOnClickListener {
            suitPressed(view, SPADES, it)
        }

        view.findViewById<Button>(R.id.card_button_2).setOnClickListener {
            cardPressed(view, TWO)
        }
        view.findViewById<Button>(R.id.card_button_3).setOnClickListener {
            cardPressed(view, THREE)
        }
        view.findViewById<Button>(R.id.card_button_4).setOnClickListener {
            cardPressed(view, FOUR)
        }
        view.findViewById<Button>(R.id.card_button_5).setOnClickListener {
            cardPressed(view, FIVE)
        }
        view.findViewById<Button>(R.id.card_button_6).setOnClickListener {
            cardPressed(view, SIX)
        }
        view.findViewById<Button>(R.id.card_button_7).setOnClickListener {
            cardPressed(view, SEVEN)
        }
        view.findViewById<Button>(R.id.card_button_8).setOnClickListener {
            cardPressed(view, EIGHT)
        }
        view.findViewById<Button>(R.id.card_button_9).setOnClickListener {
            cardPressed(view, NINE)
        }
        view.findViewById<Button>(R.id.card_button_10).setOnClickListener {
            cardPressed(view, TEN)
        }
        view.findViewById<Button>(R.id.card_button_J).setOnClickListener {
            cardPressed(view, JACK)
        }
        view.findViewById<Button>(R.id.card_button_Q).setOnClickListener {
            cardPressed(view, QUEEN)
        }
        view.findViewById<Button>(R.id.card_button_K).setOnClickListener {
            cardPressed(view, KING)
        }
        view.findViewById<Button>(R.id.card_button_A).setOnClickListener {
            cardPressed(view, ACE)
        }
        view.findViewById<TextView>(R.id.northTitle).setOnClickListener {
            cardinalityPressed(view, NORTH, it)
        }
        view.findViewById<TextView>(R.id.eastTitle).setOnClickListener {
            cardinalityPressed(view, EAST, it)
        }
        view.findViewById<TextView>(R.id.southTitle).setOnClickListener {
            cardinalityPressed(view, SOUTH, it)
        }
        view.findViewById<TextView>(R.id.westTitle).setOnClickListener {
            cardinalityPressed(view, WEST, it)
        }
        view.findViewById<View>(R.id.northView).setOnClickListener {
            cardinalityPressed(view, NORTH, view.findViewById<TextView>(R.id.northTitle))
        }
        view.findViewById<View>(R.id.eastView).setOnClickListener {
            cardinalityPressed(view, EAST, view.findViewById<TextView>(R.id.eastTitle))
        }
        view.findViewById<View>(R.id.southView).setOnClickListener {
            cardinalityPressed(view, SOUTH, view.findViewById<TextView>(R.id.southTitle))
        }
        view.findViewById<View>(R.id.westView).setOnClickListener {
            cardinalityPressed(view, WEST, view.findViewById<TextView>(R.id.westTitle))
        }

        view.findViewById<FloatingActionButton>(R.id.loadDealButton).setOnClickListener {
            this.currentView = view
            load()
        }
        view.findViewById<FloatingActionButton>(R.id.createDealButton).setOnClickListener{
            onCreate()
        }
        view.findViewById<FloatingActionButton>(R.id.cancelButton).setOnClickListener {
            onCancel()
        }
        view.findViewById<Button>(R.id.shuffleButton).setOnClickListener {
            deal.random()
            deal.display(view)
            colourCards(view)
        }

    }
}

fun showHandConstructorDiag(fragmentManager: FragmentManager, onSuccess : (deal : Deal)->Boolean) {
    HandConstructorFragment().also{
        it.show(fragmentManager, "dialog")
        it.onCancel = {
            it.dismiss()
        }
        it.onCreate = {
            if(onSuccess(it.deal)) {
                it.dismiss()
            }
        }
    }


}