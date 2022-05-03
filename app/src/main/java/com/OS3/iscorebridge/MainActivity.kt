package com.OS3.iscorebridge

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {

    val openRotate : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_open)}
    val closeRotate : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_close)}
    val menuDown : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.menu_down)}
    val menuUp : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.menu_up)}
    val fadeInCover : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.fade_in_cover)}
    val fadeOutCover : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.fade_out_cover)}
    var clicked = false
    lateinit var menuButton : FloatingActionButton


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!playerList.load(PLAYERLISTFILE, this)){
            playerList.save(PLAYERLISTFILE, this)
        }
        playerList.setupForActivity(this, {}, {playerList.save(PLAYERLISTFILE, this.applicationContext)})

        setContentView(R.layout.activity_main)
        findViewById<LinearLayout>(R.id.menuLayout).visibility = View.INVISIBLE
        menuButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            if(clicked) closeMenu()
            else openMenu()
        }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { settings() }
        findViewById<Button>(R.id.addPlayer).setOnClickListener { addPlayer() }
        findViewById<Button>(R.id.findPlayer).setOnClickListener { searchPlayers() }
        findViewById<Button>(R.id.aboutButton).setOnClickListener { about() }
        findViewById<Button>(R.id.exportPlayers).setOnClickListener { playerList.export() }
        findViewById<Button>(R.id.importPlayers).setOnClickListener { playerList.import() }
        findViewById<FrameLayout>(R.id.cover).setOnClickListener {
            if(clicked) closeMenu()
        }
        findViewById<ImageView>(R.id.helpCenterButton).setOnClickListener {
            openHelp()
        }
    }

    fun openHelp(){
        findViewById<ImageView>(R.id.helpCenterButton).visibility = INVISIBLE
        findViewById<FrameLayout>(R.id.mainLayout).visibility = INVISIBLE
        findViewById<FrameLayout>(R.id.helpLayout).also{
            it.visibility = VISIBLE
            it.removeAllViews()
        }
        supportFragmentManager.beginTransaction()
        .add(R.id.helpLayout, HelpFragment.newInstance { closeHelp() })
        .commit()
    }

    fun closeHelp(){
        findViewById<ImageView>(R.id.helpCenterButton).visibility = VISIBLE
        findViewById<FrameLayout>(R.id.mainLayout).visibility = VISIBLE
        findViewById<FrameLayout>(R.id.helpLayout).also{
            it.visibility = INVISIBLE
            it.removeAllViews()
        }

    }
    fun validatePlayer(view : View) : Boolean{
        view.findViewById<TextInputLayout>(R.id.idLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.firstNameLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.lastNameLayout).isErrorEnabled = false

        var ret = true
        try {
            if (!playerList.checkID(
                    view.findViewById<TextView>(R.id.idEdit).text.toString().toInt()
                )
            ) {
                view.findViewById<TextInputLayout>(R.id.idLayout).error = "ID already in use"
                ret = false
            }
        }
        catch(e:Exception){
            view.findViewById<TextInputLayout>(R.id.idLayout).error = "ID must be a number"
            ret = false
        }
        if(view.findViewById<TextView>(R.id.firstNameEdit).text.toString() == ""){
            view.findViewById<TextInputLayout>(R.id.firstNameLayout).error = "Must have a first name"
            ret = false
        }
        if(view.findViewById<TextView>(R.id.lastNameEdit).text.toString() == ""){
            view.findViewById<TextInputLayout>(R.id.lastNameLayout).error = "Must have a first name"
            ret = false
        }
        return ret
    }

    fun addPlayer(){
        val builder = AlertDialog.Builder(this)

        var view = layoutInflater.inflate(R.layout.add_player_layout, null)
        builder.setMessage("Add Player")
            .setPositiveButton("Add"
            ) { _, _ ->

            }
            .setNegativeButton("Cancel"
            ) { _, _ ->

            }
            .setView(view)

        var dialog = builder.create()
        dialog.show()
        view.findViewById<TextView>(R.id.idEdit).text = playerList.getFirstAvailableID().toString()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if(validatePlayer(view)){
                playerList.add(view.findViewById<TextView>(R.id.idEdit).text.toString().toInt(),
                    view.findViewById<TextView>(R.id.firstNameEdit).text.toString() + " " +
                            view.findViewById<TextView>(R.id.lastNameEdit).text.toString()
                )
                playerList.save(PLAYERLISTFILE, this)
                dialog.dismiss()
            }
        }
    }

    fun search(view : View){
        var id = -1
        try {
            id = view.findViewById<TextView>(R.id.searchIDEntry).text.toString().toInt()
        }
        catch(e : Exception){

        }
        var layout = view.findViewById<TableLayout>(R.id.searchDisplayLayout)
        layout.removeAllViews()
        var name = view.findViewById<TextView>(R.id.searchNameEntry).text.toString()
        var players : ArrayList<Player> = playerList.find(id, name)
        if(players.size == 0){
            var row = TableRow(view.context)
            var noPlayers = TextView(view.context)
            noPlayers.textSize = 30f
            noPlayers.text = "No Matching Players"
            row.addView(noPlayers)
            layout.addView(row)
        }
        else{
            playerList.display(players, layout)
        }
    }

    fun searchPlayers(){
        val builder = AlertDialog.Builder(this)

        var view = layoutInflater.inflate(R.layout.find_player_layout, null)
        view.findViewById<FloatingActionButton>(R.id.searchButton).setOnClickListener {
            search(view)
        }
        builder.setMessage("Find Player")
            .setPositiveButton("Ok"
            ) { _, _ ->

            }
            .setView(view)

        var dialog = builder.create()
        dialog.show()
    }

    fun settings(){
        Toast.makeText(this, "Settings not yet implemented", Toast.LENGTH_LONG).show()
    }



    fun about(){
        val builder = AlertDialog.Builder(this)


        var view = layoutInflater.inflate(R.layout.about_layout, null)
        VERSIONS.last().display(this, view.findViewById(R.id.aboutLinearLayout))
        builder.setMessage("About")
            .setPositiveButton("Ok"
            ) { _, _ ->

            }
            .setView(view)

        var dialog = builder.create()
        dialog.show()
    }

    fun animateColour(layout : ConstraintLayout, from : Int, to : Int, duration : Long){
        val colorFrom = resources.getColor(from, theme)
        val colorTo = resources.getColor(to, theme)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = duration

        colorAnimation.addUpdateListener { animator -> layout.foreground = ColorDrawable(animator.animatedValue as Int) }
        colorAnimation.start()
    }

    fun openMenu(){
        menuButton.startAnimation(openRotate)
        findViewById<LinearLayout>(R.id.menuLayout).startAnimation(menuDown)
        findViewById<FrameLayout>(R.id.cover).startAnimation(fadeInCover)
        findViewById<Button>(R.id.settingsButton).isClickable = true
        findViewById<Button>(R.id.addPlayer).isClickable = true
        findViewById<Button>(R.id.aboutButton).isClickable = true
        findViewById<Button>(R.id.exportPlayers).isClickable = true
        findViewById<Button>(R.id.importPlayers).isClickable = true
        findViewById<Button>(R.id.findPlayer).isClickable = true
        clicked = true
    }

    fun closeMenu(){
        menuButton.startAnimation(closeRotate)
        findViewById<LinearLayout>(R.id.menuLayout).startAnimation(menuUp)
        findViewById<FrameLayout>(R.id.cover).startAnimation(fadeOutCover)
        findViewById<Button>(R.id.settingsButton).isClickable = false
        findViewById<Button>(R.id.addPlayer).isClickable = false
        findViewById<Button>(R.id.aboutButton).isClickable = false
        findViewById<Button>(R.id.exportPlayers).isClickable = false
        findViewById<Button>(R.id.importPlayers).isClickable = false
        findViewById<Button>(R.id.findPlayer).isClickable = false
        clicked = false
    }


    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Cannot go back", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            wifiService.kill()
        }
        catch(e:Exception){}
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}