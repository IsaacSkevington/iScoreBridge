package com.OS3.iscorebridge

import android.app.Activity
import android.content.Intent
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.LocalDate

class HandConstructorFragment : Fragment() {

    private lateinit var background : Drawable
    private var deal = Deal()

    private var currentCardinality = 'N'
    private var currentSuit = 'C'
    private lateinit var currentView : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


    fun cardToButtonId(card : Char) : Int{
        return when(card){
            '2' -> R.id.card_button_2
            '3' -> R.id.card_button_3
            '4' -> R.id.card_button_4
            '5' -> R.id.card_button_5
            '6' -> R.id.card_button_6
            '7' -> R.id.card_button_7
            '8' -> R.id.card_button_8
            '9' -> R.id.card_button_9
            'T' -> R.id.card_button_10
            'J' -> R.id.card_button_J
            'Q' -> R.id.card_button_Q
            'K' -> R.id.card_button_K
            'A' -> R.id.card_button_A
            else -> 0
        }
    }

    fun cardinalityToTextId(cardinality: Char) : Int{
        return when(cardinality){
            'N' -> R.id.northTitle
            'E' -> R.id.eastTitle
            'S' -> R.id.southTitle
            'W' -> R.id.westTitle
            else -> 0
        }
    }

    fun suitToButtonId(suit : Char) : Int{
        return when(suit){
            'C' -> R.id.card_button_club
            'D' -> R.id.card_button_diamond
            'H' -> R.id.card_button_heart
            'S' -> R.id.card_button_spade
            else -> 0
        }
    }

    fun colourCard(view: View, value: Char, color : Int){
        view.findViewById<Button>(cardToButtonId(value)).setBackgroundColor(color)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun colourCards(view : View){
        resetCardBackgrounds(view)
        var usedCards = deal.getCardsUsedBySuit()[currentSuit]
        usedCards!!.forEach{
            colourCard(view, it.value, Color.DKGRAY)
        }
        var cards = deal.getHand(currentCardinality).getBySuit()[currentSuit]
        cards!!.forEach {
            colourCard(view, it.value, Color.CYAN)
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun suitPressed(view : View, suit : Char, button : View){
        resetSuitBackgrounds(view)
        currentSuit = suit
        button.setBackgroundColor(Color.CYAN)
        colourCards(view)

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun cardPressed(view : View, card: Char, button : View){
        if(view.findViewById<Button>(cardToButtonId(card)).background == background){
            if(deal.containsCard(Card(currentSuit, card)) || !deal.getHand(currentCardinality).addCard(Card(currentSuit, card))) {
                return
            }
        }
        else{
            deal.getHand(currentCardinality).removeCard(Card(currentSuit, card))
        }
        colourCards(view)
        deal.display(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun cardinalityPressed(view : View, cardinality : Char, textView : View){
        resetCardinalityBackgrounds(view)
        currentCardinality = cardinality
        textView.setBackgroundColor(Color.CYAN)
        colourCards(view)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val contentResolver = context!!.contentResolver
                try {
                    val descriptor = contentResolver.openFileDescriptor(uri, "w")
                    FileOutputStream(descriptor?.fileDescriptor).use {
                        deal.save(it)
                    }
                    Toast.makeText(context!!, "Save successful", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.handConstructorToHome)
                } catch (e: FileNotFoundException) {
                    Toast.makeText(context!!, "Save failed", Toast.LENGTH_LONG).show()
                }
            }
        }
        else if(requestCode == OPEN_FILE && resultCode == Activity.RESULT_OK){
            resultData?.data?.also { uri ->
                val contentResolver = context!!.contentResolver
                try {
                    val descriptor = contentResolver.openFileDescriptor(uri, "r")
                    FileInputStream(descriptor?.fileDescriptor).use {
                        deal.load(it)
                    }
                    Toast.makeText(context!!, "Load successful", Toast.LENGTH_LONG).show()
                    colourCards(currentView)
                    deal.display(currentView)
                } catch (e: FileNotFoundException) {
                    Toast.makeText(context!!, "Load failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun load(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/txt"
        }
        startActivityForResult(intent, OPEN_FILE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun save() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/txt"
            val d = LocalDate.now()
            d.dayOfMonth.toString() + d.month.toString() + d.year.toString()
            putExtra(Intent.EXTRA_TITLE, "game" + LocalDate.now().toString() + ".deal")
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        background = view.findViewById<ImageButton>(R.id.card_button_club).background
        deal.display(view)
        suitPressed(view, 'C', view.findViewById(R.id.card_button_club))
        cardinalityPressed(view, 'N', view.findViewById(R.id.northTitle))

        view.findViewById<ImageButton>(R.id.card_button_club).setOnClickListener {
            suitPressed(view, 'C', it)
        }
        view.findViewById<ImageButton>(R.id.card_button_diamond).setOnClickListener {
            suitPressed(view, 'D', it)
        }
        view.findViewById<ImageButton>(R.id.card_button_heart).setOnClickListener {
            suitPressed(view, 'H', it)
        }
        view.findViewById<ImageButton>(R.id.card_button_spade).setOnClickListener {
            suitPressed(view, 'S', it)
        }

        view.findViewById<Button>(R.id.card_button_2).setOnClickListener {
            cardPressed(view, '2', it)
        }
        view.findViewById<Button>(R.id.card_button_3).setOnClickListener {
            cardPressed(view, '3', it)
        }
        view.findViewById<Button>(R.id.card_button_4).setOnClickListener {
            cardPressed(view, '4', it)
        }
        view.findViewById<Button>(R.id.card_button_5).setOnClickListener {
            cardPressed(view, '5', it)
        }
        view.findViewById<Button>(R.id.card_button_6).setOnClickListener {
            cardPressed(view, '6', it)
        }
        view.findViewById<Button>(R.id.card_button_7).setOnClickListener {
            cardPressed(view, '7', it)
        }
        view.findViewById<Button>(R.id.card_button_8).setOnClickListener {
            cardPressed(view, '8', it)
        }
        view.findViewById<Button>(R.id.card_button_9).setOnClickListener {
            cardPressed(view, '9', it)
        }
        view.findViewById<Button>(R.id.card_button_10).setOnClickListener {
            cardPressed(view, 'T', it)
        }
        view.findViewById<Button>(R.id.card_button_J).setOnClickListener {
            cardPressed(view, 'J', it)
        }
        view.findViewById<Button>(R.id.card_button_Q).setOnClickListener {
            cardPressed(view, 'Q', it)
        }
        view.findViewById<Button>(R.id.card_button_K).setOnClickListener {
            cardPressed(view, 'K', it)
        }
        view.findViewById<Button>(R.id.card_button_A).setOnClickListener {
            cardPressed(view, 'A', it)
        }
        view.findViewById<TextView>(R.id.northTitle).setOnClickListener {
            cardinalityPressed(view, 'N', it)
        }
        view.findViewById<TextView>(R.id.eastTitle).setOnClickListener {
            cardinalityPressed(view, 'E', it)
        }
        view.findViewById<TextView>(R.id.southTitle).setOnClickListener {
            cardinalityPressed(view, 'S', it)
        }
        view.findViewById<TextView>(R.id.westTitle).setOnClickListener {
            cardinalityPressed(view, 'W', it)
        }
        view.findViewById<View>(R.id.northView).setOnClickListener {
            cardinalityPressed(view, 'N', view.findViewById<TextView>(R.id.northTitle))
        }
        view.findViewById<View>(R.id.eastView).setOnClickListener {
            cardinalityPressed(view, 'E', view.findViewById<TextView>(R.id.eastTitle))
        }
        view.findViewById<View>(R.id.southView).setOnClickListener {
            cardinalityPressed(view, 'S', view.findViewById<TextView>(R.id.southTitle))
        }
        view.findViewById<View>(R.id.westView).setOnClickListener {
            cardinalityPressed(view, 'W', view.findViewById<TextView>(R.id.westTitle))
        }
        view.findViewById<Button>(R.id.saveDealButton).setOnClickListener {
            this.currentView = view
            save()
        }
        view.findViewById<Button>(R.id.loadDealButton).setOnClickListener {
            this.currentView = view
            load()
        }
        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            findNavController().navigate(R.id.handConstructorToHome)
        }

    }
}