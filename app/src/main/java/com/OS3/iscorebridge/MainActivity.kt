package com.OS3.iscorebridge

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    val openRotate : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_open)}
    val closeRotate : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.rotate_close)}
    val menuDown : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.menu_down)}
    val menuUp : Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.menu_up)}

    var clicked = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!playerList.load(PLAYERLISTFILE, this)){
            playerList.save(PLAYERLISTFILE, this)
        }

        setContentView(R.layout.activity_main)
        findViewById<LinearLayout>(R.id.menuLayout).visibility = View.INVISIBLE
        menuButton.setOnClickListener {
            if(clicked) closeMenu()
            else openMenu()
            clicked = !clicked
        }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { settings() }
        findViewById<Button>(R.id.addPlayer).setOnClickListener { addPlayer() }
        findViewById<Button>(R.id.findPlayer).setOnClickListener { searchPlayers() }
        findViewById<Button>(R.id.aboutButton).setOnClickListener { about() }
        findViewById<Button>(R.id.exportPlayers).setOnClickListener { exportPlayers() }
        findViewById<Button>(R.id.importPlayers).setOnClickListener { importPlayers() }
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
        view.findViewById<Button>(R.id.searchButton).setOnClickListener {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                try {
                    val descriptor = contentResolver.openFileDescriptor(uri, "w")
                    FileOutputStream(descriptor?.fileDescriptor).use {
                        playerList.export(it)
                    }
                    Toast.makeText(this, "Export successful", Toast.LENGTH_LONG).show()
                } catch (e: FileNotFoundException) {
                    Toast.makeText(this, "Export failed", Toast.LENGTH_LONG).show()
                }
            }

        }
        if (requestCode == OPEN_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                try {
                    val descriptor = contentResolver.openFileDescriptor(uri, "r")
                    FileInputStream(descriptor?.fileDescriptor).use {
                        playerList.import(it)
                        playerList.save(PLAYERLISTFILE, this)
                    }
                    Toast.makeText(this, "Import successful", Toast.LENGTH_LONG).show()
                } catch (e: FileNotFoundException) {
                    Toast.makeText(this, "Import failed", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun exportPlayers(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/txt"
            val d = LocalDate.now()
            d.dayOfMonth.toString() + d.month.toString() + d.year.toString()
            putExtra(Intent.EXTRA_TITLE, "players" + LocalDate.now().toString() + ".playerData")
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    fun importPlayers(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/txt"
        }
        startActivityForResult(intent, OPEN_FILE)
    }

    fun about(){
        val builder = AlertDialog.Builder(this)


        var view = layoutInflater.inflate(R.layout.about_layout, null)
        VERSIONS.last().display(this, view.findViewById<LinearLayout>(R.id.aboutLinearLayout))
        builder.setMessage("About")
            .setPositiveButton("Ok"
            ) { _, _ ->

            }
            .setView(view)

        var dialog = builder.create()
        dialog.show()
    }


    fun openMenu(){
        menuButton.startAnimation(openRotate)
        findViewById<LinearLayout>(R.id.menuLayout).startAnimation(menuDown)
        findViewById<Button>(R.id.settingsButton).isClickable = true
        findViewById<Button>(R.id.addPlayer).isClickable = true
    }

    fun closeMenu(){
        menuButton.startAnimation(closeRotate)
        findViewById<LinearLayout>(R.id.menuLayout).startAnimation(menuUp)
        findViewById<Button>(R.id.settingsButton).isClickable = false
        findViewById<Button>(R.id.addPlayer).isClickable = false
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