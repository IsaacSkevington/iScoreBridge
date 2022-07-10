package com.OS3.iscorebridge

import android.app.AlertDialog
import android.graphics.Color
import android.os.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@Volatile var amHost : Boolean = false

@RequiresApi(Build.VERSION_CODES.O)
fun encodeID(ID : String) : String{
    var out = ""
    for(char in ID){
        out += char.code.toString()
    }
    return if(out.length > 7){
        out.substring(out.length - 7, out.length)
    }
    else{
        out
    }
}
class HostEnterTableDetailsFragment(var onNext : () -> Unit) : EnterTableDetailsFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.submitTableDataButton).also{
            it.setImageDrawable(requireActivity().getDrawable(R.drawable.outline_done_24))
            it.rotation = 0f
        }
        view.findViewById<TextView>(R.id.enterTableDetailsPrompt).text = "Please enter your table's details before starting the game"
    }

    override fun next(){
        Toast.makeText(context, "Details Confirmed", Toast.LENGTH_LONG).show()
        onNext()
    }
}

class StartGameScreen : Fragment() {


    var boardsUploaded : ArrayList<Board>? = null
    var tables : Int? = null
    var boards : Int? = null
    var PBN : PBNFile? = null
    var gameMode = PAIRS
    var movement : MovementSkeleton? = null
    var detailsSet = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_start_game_screen, container, false)
    }


    private fun tablesCheck(view : View) : Boolean{
        val tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString()
        if(tables == ""){
            view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error = "Tables must be specified"
            return false
        }
        try{
            val x = tables.toInt()
            if(x < 1){
                view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error = "Tables must be greater than 0"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error = "Tables must be a number"
            return false
        }
        return true
    }

    private fun boardsCheck(view : View) : Boolean{
        val boards = view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString()
        if(boards == ""){
            view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).error = "Boards must be specified"
            return false
        }
        try{
            val x = boards.toInt()
            if(x < 1){
                view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).error = "Boards must be greater than 0"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).error = "Boards must be a number"
            return false
        }
        return true
    }


    private fun errorCheck(view : View) : Boolean{
        view.findViewById<TextInputLayout>(R.id.numberTablesLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).isErrorEnabled = false
        var ret = boardsCheck(view)
        return tablesCheck(view) && ret
    }
    


    override fun onPause(){
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy(){
        super.onDestroy()
        amHost = false
    }

    fun updateClientTable(view : View){
        var table = view.findViewById<TableLayout>(R.id.playersTable)
        table.removeAllViews()
        var currentRowItems = 0

        var currentRow = TableRow(view.context)
        var textView = TextView(view.context)
        textView.textSize = 30f
        textView.text = "Tables"
        currentRow.addView(textView)
        table.addView(currentRow)
        currentRow = TableRow(view.context)
        if(wifiHost.getTables().size == 0){
            var textView = TextView(view.context)
            textView.text = "No Tables Have Joined"
            currentRow.addView(textView)
            table.addView(currentRow)
            return
        }
        wifiHost.getTables().forEach {
            var textView = TextView(view.context)
            textView.text = it.toString()
            if(++currentRowItems == 3){
                table.addView(currentRow)
                currentRow = TableRow(view.context)
                currentRowItems = 0
            }
        }

    }

    fun onBack(){
        amHost = false
        wifiHost.kill()
        findNavController().popBackStack()
    }


    fun switchImport(view : View, PBN : PBNFile?){
        if(boardsUploaded == null){
            view.findViewById<TextView>(R.id.dealSelectedView).text = PBN!!.importFilename
            view.findViewById<FloatingActionButton>(R.id.addDealsButton).also{
                it.setImageDrawable(requireActivity().getDrawable(R.drawable.outline_add_24))
                it.rotation = 45f
            }
            var boardsUploaded = ArrayList<Board>()
            PBN.games.forEach {
                var board = Board(it.boardNumber)
                board.deal = it.deal
                boardsUploaded.add(board)
            }
            this.boardsUploaded = boardsUploaded
            AlertDialog.Builder(requireContext())
                .setTitle("Import Success")
                .setMessage("Imported ${boardsUploaded.size} boards")
                .setPositiveButton("Ok"){_, _ ->}
                .create()
                .show()
        }
        else{
            view.findViewById<TextView>(R.id.dealSelectedView).text = "No deal uploaded"
            view.findViewById<FloatingActionButton>(R.id.addDealsButton).also{
                it.setImageDrawable(requireActivity().getDrawable(R.drawable.outline_card_black))
                it.rotation = 0f
            }
            boardsUploaded = null
        }
    }

    fun PBNImportSuccess(view : View, PBN : PBNFile){
        view.findViewById<TextView>(R.id.dealSelectedView).text = PBN.importFilename
        switchImport(view, PBN)
        this.PBN = PBN
    }



    fun movementSelector(view : View){
        var tableParent = layoutInflater.inflate(R.layout.movements_display_table, null)
        var tableLayout = tableParent.findViewById<TableLayout>(R.id.tableView).also {
            it.isStretchAllColumns = true
        }
        var radioGrouper = RadioGrouper()
        var movements = MovementCreator().findMovements(requireContext(), tables!!, boards!!, gameMode)
        if(movements.size == 0){
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage("No movements available for your parameters")
                .setPositiveButton("Ok"){_, _ ->}
                .create()
                .show()
            return
        }
        var rows = ArrayList<TableRow>()
        movements.forEach {
            var tableRow = TableRow(view.context)
            radioGrouper.addButton(view.context){
                rows.forEach { row->
                    row.setBackgroundColor(Color.TRANSPARENT)
                }
                tableRow.setBackgroundColor(resources.getColor(R.color.selected, requireActivity().theme))
            }.also {
                it.setPadding(10, 10, 10, 10)
                tableRow.addView(it)
            }

            populateMovementsOverviewRow(tableRow, it)
            tableRow.setPadding(10, 10, 10, 10)
            tableRow.gravity = Gravity.CENTER_VERTICAL
            tableLayout.addView(tableRow)
            rows.add(tableRow)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Select a movement")
            .setView(tableParent)
            .setPositiveButton("Done"){_, _ ->
                var selected = radioGrouper.getSelected()
                if(selected != -1){
                    movement = movements[selected]
                    view.findViewById<TextView>(R.id.movementSelectedView).text = movement!!.getSummary()
                    Toast.makeText(requireContext(), "Movement Set Successfully", Toast.LENGTH_LONG)
                }
                else{
                    movement = null
                    view.findViewById<TextView>(R.id.movementSelectedView).text = "No Movement Selected"
                    Toast.makeText(requireContext(), "No Movement Was Selected", Toast.LENGTH_LONG)
                }
            }
            .setNegativeButton("Cancel"){_, _ ->}
            .create()
            .show()
    }

    fun center(tv : TextView){
        tv.width = ViewGroup.LayoutParams.MATCH_PARENT
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
    }

    fun populateMovementsOverviewRow(tableRow: TableRow, movement: MovementSkeleton){
        var c = requireContext()
        tableRow.addView(TextView(c).also{it.text = movement.movementType.toString();center(it)})
        tableRow.addView(TextView(c).also{it.text = movement.rounds.size.toString();center(it)})
        tableRow.addView(TextView(c).also{it.text = movement.getTotalBoards().toString();center(it)})
        tableRow.addView(TextView(c).also{it.text = movement.boardsPerRound.toString();center(it)})
        tableRow.addView(TextView(c).also{it.text = if(movement.twoWinner) "2" else "1";center(it)})
        tableRow.addView(TextView(c).also{it.text = if(movement.arrowSwitch) "✓" else "x";center(it)})
        tableRow.addView(TextView(c).also{it.text = if(movement.movementType == MovementType.Howell) "x" else "✓";center(it)})


        var fabView = layoutInflater.inflate(R.layout.view_detail_movement_button, null)
        fabView.findViewById<FloatingActionButton>(R.id.viewDetailMovement).setOnClickListener {
            var v = layoutInflater.inflate(R.layout.movement_overview_table_display, null)
            movement.display(v.findViewById(R.id.movementOverviewTable), v.findViewById(R.id.outerLayout))
            AlertDialog.Builder(requireContext())
                .setTitle("${movement.movementType} movement for ${movement.getTotalTables()} tables")
                .setPositiveButton("Ok"){_, _ ->}
                .setView(v)
                .create()
                .show()
        }
        tableRow.addView(fabView)


    }

    fun start(view : View){
        when {
            wifiHost.clients.size + 1 < tables!! -> {
                view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error =
                    "Too few people in game (${wifiHost.clients.size + 1})"
            }
            else -> {
                val gameMode =
                    if (view.findViewById<Switch>(R.id.modeSwitch).isChecked) GAMEMODE_PAIRS
                    else GAMEMODE_TEAMS

                var picker = TimePicker(requireContext()).also{
                    it.hour = 8
                    it.minute = 0
                    it.setIs24HourView(true)
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Time Per Board (Minutes)")
                    .setPositiveButton("Ok"){_, _ ->
                        if(picker.hour != 0 || picker.minute != 0){
                            val roundTime = Time(0, picker.hour, picker.minute)
                            gameInfo =
                                GameInfo(
                                    wifiHost.getSkeletonTables(),
                                    gameMode,
                                    wifiHost.getClientAddresses(),
                                    roundTime,
                                    movement!!
                                )
                            boardsUploaded?.forEach {
                                gameInfo.match.boards[it.boardNumber] = it
                            }
                            wifiHost.startGame()
                        }
                    }
                    .setNegativeButton("Cancel"){_, _ ->}
                    .setView(picker)
                    .create()
                    .show()
            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amHost = true
        view.findViewById<TextView>(R.id.idTextView).text = encodeID(myInfo.deviceName)
        view.findViewById<FrameLayout>(R.id.enterDetailsWindow).also{
            var trans = childFragmentManager.beginTransaction()
            trans.add(R.id.enterDetailsWindow, HostEnterTableDetailsFragment{
                it.visibility = GONE
                detailsSet = true
            })
            trans.commit()
        }





        val handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_CONNECTED_HOST -> {}
                    MESSAGE_START -> {
                        if(!gameStarted) {
                            infoTag.mainActivity.matchHandler.obtainMessage(MESSAGE_START).sendToTarget()
                            gameStarted = true
                            myInfo.setup(requireContext())
                            findNavController().navigate(StartGameScreenDirections.startGameToScore(
                                START_BOARDNUMBER))
                        }
                    }
                    MESSAGE_CLIENT_CONNECTED ->
                        Toast.makeText(view.context, (msg.obj as ClientInfo).deviceName + " joined", Toast.LENGTH_LONG).show()
                    MESSAGE_UPDATE_CLIENT -> updateClientTable(view)
                    MESSAGE_DEVICE_ID_CHANGED -> {
                        Toast.makeText(view.context, "Device ID Changed!", Toast.LENGTH_LONG).show()
                        view.findViewById<TextView>(R.id.idTextView).text = encodeID(myInfo.deviceName)
                    }
                }
            }
        }
        wifiService.createGroup()
        wifiService.parentHandler = handler
        wifiHost = WifiHost(handler)
        wifiHostInitialised = true
        updateClientTable(view)

        var PBN = PBNFile()
        PBN.setupForFragment(this, {PBNImportSuccess(view, PBN)}, {})
        view.findViewById<FloatingActionButton>(R.id.addDealsButton).setOnClickListener{
            if(boardsUploaded == null) PBN.import()
            else switchImport(view, null)
        }
        view.findViewById<Switch>(R.id.modeSwitch).setOnClickListener{
            gameMode = if(view.findViewById<Switch>(R.id.modeSwitch).isChecked) TEAMS
                       else PAIRS
        }
        view.findViewById<FloatingActionButton>(R.id.startGameBackButton).setOnClickListener { onBack() }

        view.findViewById<FloatingActionButton>(R.id.selectMovementButton).setOnClickListener {
            if(errorCheck(view)){
                tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString().toInt()
                boards = view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString().toInt()
                movementSelector(view)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.StartPlayingButton).setOnClickListener {
            if(!detailsSet) {
                AlertDialog.Builder(requireContext())
                    .setTitle("No Details Provided")
                    .setMessage("Please provide the details for your table")
                    .setPositiveButton("Ok"){_, _ ->}
                    .create()
                    .show()
                return@setOnClickListener
            }
            if(movement != null || errorCheck(view)) {
                if(movement == null){
                    tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString().toInt()
                    boards = view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString().toInt()
                    movement = MovementCreator().none(tables!!, boards!!, gameMode)
                }
                else{
                    if(!errorCheck(view)) {
                        return@setOnClickListener
                    }
                    if(view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString().toInt() != tables ||
                        view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString().toInt() != boards ||
                            movement!!.gameMode != gameMode){
                                if(movement!!.movementType == MovementType.None){
                                    tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString().toInt()
                                    boards = view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString().toInt()
                                    movement = MovementCreator().none(tables!!, boards!!, gameMode)
                                }
                                else {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Error")
                                        .setMessage("You changed a parameter since last searching for a movement, please update the movement")
                                        .setPositiveButton("Ok") { _, _ -> }
                                        .create()
                                        .show()
                                    return@setOnClickListener
                                }
                    }
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Start")
                    .setMessage("Would you like to start a game with $tables tables, with the movement \"${movement!!.getSummary()}\"")
                    .setPositiveButton("Yes") { _, _ ->
                        start(view)
                    }
                    .setNegativeButton("No"){_, _ ->}
                    .create()
                    .show()

            }
        }

    }

}