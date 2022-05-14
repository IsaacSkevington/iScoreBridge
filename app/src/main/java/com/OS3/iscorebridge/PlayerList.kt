package com.OS3.iscorebridge


import android.app.AlertDialog
import android.content.Context
import android.widget.*
import java.io.*

var playerList : PlayerList = PlayerList()

class PlayerList : Exportable("players", ".playerData"){

    var map : MutableMap<Int, String> = HashMap()




    fun remove(id : Int){
        map.remove(id)
    }

    fun checkID(id : Int) : Boolean{
        return !map.containsKey(id)
    }

    fun add(id : Int, name : String) : Boolean{
        if(get(id) == null){
            map[id] = name
            return true
        }
        return false
    }

    override fun read(fileInputStream: FileInputStream) : Boolean{
        var isr = InputStreamReader(fileInputStream)
        map = HashMap()
        return try {
            isr.readLines().forEach() {
                var p = Player(it)
                map[p.id] = p.name
            }
            true
        } catch (e : Exception){
            false
        }
    }

    override fun write(fileOutputStream: FileOutputStream){
        map.forEach {
            var p = Player(it.value, it.key)
            fileOutputStream.write((p.toString() + "\n").toByteArray())
        }
    }

    fun find(id : Int) : ArrayList<Player>{
        var players = ArrayList<Player>()

        if(id == 0){
            map.forEach{
                players.add(Player(it.value, it.key))
            }
            return players
        }
        var player = get(id)
        if(player != null){
            players.add(Player(player, id))
        }
        return players
    }

    fun find(name : String) : ArrayList<Player>{
        var players = ArrayList<Player>()

        map.forEach {
            if(it.value.contains(name, true)){
                players.add(Player(it.value, it.key))
            }
        }
        return players
    }

    fun find(id : Int, name : String) : ArrayList<Player>{
        return if(id == -1 && name == "") ArrayList()
        else{
            when {
                id == -1 -> find(name)
                name == "" -> find(id)
                else -> {
                    var players = find(id)
                    when {
                        players.size == 0 -> ArrayList()
                        players[0].name.contains(name, true) -> players
                        else -> ArrayList()
                    }
                }
            }
        }
    }

    fun makeTitle(textIn : String, context: Context) : TextView{
        var text = TextView(context)
        text.text = textIn
        text.textSize = 20f
        return text
    }
    fun makeText(textIn : String, context: Context) : TextView{
        var text = TextView(context)
        text.text = textIn
        text.textSize = 12f
        return text
    }

    fun display(players : ArrayList<Player>, layout : TableLayout){
        var tableTitleRow = TableRow(layout.context)
        tableTitleRow.addView(makeTitle("ID", layout.context))
        tableTitleRow.addView(makeTitle("Name", layout.context))
        tableTitleRow.addView(makeTitle("Remove", layout.context))
        layout.addView(tableTitleRow)
        players.forEach {
            var tableRow = TableRow(layout.context)
            tableRow.addView(makeText(it.id.toString(), layout.context))
            tableRow.addView(makeText(it.name.toString(), layout.context))
            var removeButton = Button(layout.context)
            removeButton.text = "-"
            removeButton.setOnClickListener { _ ->
                val builder = AlertDialog.Builder(layout.context)
                builder.setMessage("Are you sure you want to remove ${it.name} (Id ${it.id})")
                    .setPositiveButton(
                        "Remove"
                    ) { _, _ ->
                        remove(it.id)
                        Toast.makeText(layout.context, "Player removed successfully", Toast.LENGTH_LONG).show()
                        layout.removeAllViews()
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { _, _ ->

                    }

                var dialog = builder.create()
                dialog.show()
            }
            tableRow.addView(removeButton)
            layout.addView(tableRow)
        }
    }

    fun getFirstAvailableID() : Int{
        var i = 0
        while(true){
            if(get(++i) == null){
                return i
            }
        }
    }

    override fun toString(): String {
        var text = ""
        map.forEach {
            var p = Player(it.value, it.key)
            text += p.toString() + "\n"
        }
        return text
    }

    fun save(fileName : String, context : Context){
        File(context.filesDir, fileName).delete()
        var file = File(context.filesDir, fileName)
        file.writeText(toString())
    }
    fun load(fileName : String, context : Context) : Boolean{
        return try {
            File(context.filesDir, fileName).forEachLine {
                var p = Player(it)
                map[p.id] = p.name
            }
            true
        } catch(e : FileNotFoundException){
            false
        }
    }

    fun populate(p : Player) : Boolean{
        if(p.id == 0){
            p.name = ""
            return true
        }
        var name = get(p.id)
        if(name == null){
            p.name = PLAYERNOTFOUND
            return false
        }
        p.name = name
        return true
    }

    fun get(id : Int) : String?{
        return map[id]
    }
}