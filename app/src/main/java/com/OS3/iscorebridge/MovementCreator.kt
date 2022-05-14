package com.OS3.iscorebridge

import android.content.Context
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.ceil


class MovementCreator{

    fun findMovements(context : Context, tables : Int, boards: Int, gameMode : Int, noMovement : Boolean = false) : ArrayList<MovementSkeleton>{
        var movements = ArrayList<MovementSkeleton>()
        var tablesList = ArrayList<Table>()
        for(i in 1..tables){
            tablesList.add(Table(i))
        }
        if(noMovement){
            movements.add(none(tablesList, boards))
            return movements
        }
        if(gameMode == TEAMS){
            movements.add(mitchellTeams(tablesList, boards))
            return movements
        }


        var sandr = mitchellPairs(ArrayList(tablesList), boards, arrowSwitch = false, shareAndRelay = false)
        var skip = mitchellPairs(ArrayList(tablesList), boards, arrowSwitch = false, shareAndRelay = true)
        var sandrDifference = boards - sandr.getTotalBoards()
        var skipDifference = boards - skip.getTotalBoards()
        var mitchell = if(tables == 2){fromFile(context, MovementType.Mitchell, tablesList, boards, 2)!!}
            else{
                if(sandrDifference <= skipDifference){
                    sandr
                }
                else{
                    skip
                }
            }
        movements.add(mitchell)
        var arrowSwitchMitchell = MovementSkeleton(mitchell).also{it.arrowSwitch()}
        movements.add(arrowSwitchMitchell)
        var howell = howell(context, ArrayList(tablesList), boards)
        if(howell != null) movements.add(howell)

        return movements

    }



    fun none(tables: ArrayList<Table>, boards: Int) : MovementSkeleton{
        var skeleton = MovementSkeleton()
        skeleton.rounds[1] = Round(1)
        tables.forEach {
            var allBoards = ArrayList<Int>()
            for(i in 1..boards){
                allBoards.add(i)
            }
            it.boards = allBoards
        }
        skeleton.rounds[1]!!.tables = tables.associate { table -> table.tableNumber to table }.toMutableMap()
        return skeleton
    }

    fun assignPairNumbers(tables : ArrayList<Table>, assignFun : (table : Table)->Unit){
        tables.forEach {
            assignFun(it)
        }
    }


    fun skipMitchell(tables : ArrayList<Table>, boards : Int) : MovementSkeleton{
        var ret = oddMitchell(tables, boards)
        var middleRound = ceil(ret.rounds.size/2f).toInt()

        for(i in middleRound + 1 until ret.rounds.size){
            ret.rounds[i]!!.copyTablesPairs(ret.rounds[i+1]!!.tables)
        }
        ret.rounds.remove(ret.rounds.size)
        return ret
    }

    fun shareAndRelayMitchell(tables : ArrayList<Table>, boards : Int) : MovementSkeleton{
        var halfWayRound = tables.size/2
        val boardsPerRound = boards/tables.size
        tables.add(halfWayRound-1, Table(halfWayRound))
        for(i in halfWayRound until tables.size){
            tables[i].tableNumber++
        }

        var ret = oddMitchell(tables, boards + boardsPerRound)
        var tableToRemove = ret.rounds[1]!!.tables[halfWayRound]!!


        //Remove extra boards
        var removeBoardIndicator = boards + boardsPerRound
        ret.rounds.forEach {
            var tempBoards : ArrayList<Int> = it.value.tables[it.value.tables.size]!!.boards
            for(i in it.value.tables.size downTo 1){
                if(it.value.tables[i]!!.boards.contains(removeBoardIndicator)){
                    if(i != tableToRemove.tableNumber) {
                        it.value.tables[i]!!.boards = tempBoards
                    }
                    break
                }
                var temp = it.value.tables[i]!!.boards
                it.value.tables[i]!!.boards = tempBoards
                tempBoards = temp
            }
            it.value.tables[tables.size]!!.boards = it.value.tables[1]!!.boards
        }



        //Remove extra EW pair
        ret.rounds.forEach {
            var lastPair = tableToRemove.pairEW
            for(i in 0 until it.value.tables.size){
                var table = i + tableToRemove.tableNumber
                if(table > it.value.tables.size){
                    table -= it.value.tables.size
                }
                if(it.value.tables[table]!!.pairEW == tableToRemove.pairEW){
                    if(table != tableToRemove.tableNumber) {
                        it.value.tables[table]!!.pairEW = lastPair
                    }
                    break
                }
                var temp = it.value.tables[table]!!.pairEW
                it.value.tables[table]!!.pairEW = lastPair
                lastPair = temp
            }
        }
        //Remove relay table
        ret.rounds.forEach {round ->
            round.value.tables.remove(tableToRemove.tableNumber)
            round.value.tables.forEach {
            }
            var newTables = HashMap<Int, Table>()
            round.value.tables.forEach {table->
                if(table.key > tableToRemove.tableNumber){
                    newTables[table.key-1] = table.value.also { it.tableNumber-- }
                }
                else{
                    newTables[table.key] = table.value
                }
                round.value.tables = newTables
            }
        }



        //Normalise pair numbers
        ret.rounds[1]!!.tables.forEach { table ->
            var minus = 0
            if(table.value.pairNS.displayNumber > tableToRemove.pairNS.displayNumber){
                table.value.pairNS.displayNumber--
                table.value.pairNS.scoringNumber--
                minus = 1
            }
            if(table.value.pairNS.displayNumber > tableToRemove.pairEW.displayNumber - minus){
                table.value.pairNS.displayNumber--
                table.value.pairNS.scoringNumber--
            }
            minus = 0
            if(table.value.pairEW.displayNumber > tableToRemove.pairNS.displayNumber){
                table.value.pairEW.displayNumber--
                table.value.pairEW.scoringNumber--
                minus = 1
            }
            if(table.value.pairEW.displayNumber > tableToRemove.pairEW.displayNumber - minus){
                table.value.pairEW.displayNumber--
                table.value.pairEW.scoringNumber--
            }

        }

        //Remove final round
        ret.rounds.remove(ret.rounds.size)
        return ret

    }

    fun evenMitchell(tables : ArrayList<Table>, boards : Int, shareAndRelay: Boolean) : MovementSkeleton{
        return if(shareAndRelay){
            shareAndRelayMitchell(tables, boards)
        } else{
            skipMitchell(tables,boards)
        }


    }

    fun oddMitchell(tables: ArrayList<Table>, boards: Int) : MovementSkeleton{
        assignPairNumbers(tables){
            it.pairNS.displayNumber = it.tableNumber
            it.pairNS.scoringNumber = it.tableNumber
            it.pairEW.displayNumber = it.tableNumber + tables.size
            it.pairEW.scoringNumber = it.tableNumber + tables.size
        }
        val boardsPerRound = boards/tables.size
        var skeleton = MovementSkeleton()
        skeleton.boardsPerRound = boardsPerRound
        for(i in 0 until tables.size){
            skeleton.rounds[i + 1] = Round(i+1)
        }

        for(tableNumber in 1..tables.size){
            for(roundNumber in 1..tables.size){
                var pairEW = (tables.size - roundNumber) + tableNumber + 1
                if(pairEW <= tables.size){
                    pairEW += tables.size
                }
                var startBoard = boardsPerRound * (tableNumber + roundNumber - 2) + 1
                if(startBoard > boards){
                    startBoard-= boards
                }
                var roundBoards = ArrayList<Int>()
                for(k in startBoard until startBoard+boardsPerRound){
                    roundBoards.add(k)
                }
                var table = Table(tableNumber, roundBoards, getPlayerPair(tables, tableNumber)!!, getPlayerPair(tables, pairEW)!!)
                skeleton.rounds[roundNumber]!!.tables[tableNumber] = table
            }
        }
        return skeleton
    }



    fun mitchellPairs(tables : ArrayList<Table>, boards : Int, arrowSwitch: Boolean, shareAndRelay: Boolean) : MovementSkeleton{
        var ret = if(tables.size % 2 == 0){
            evenMitchell(tables, boards, shareAndRelay)
        }
        else{
            oddMitchell(tables, boards)
        }
        if(arrowSwitch){
            ret.arrowSwitch()
        }
        ret.movementType = MovementType.Mitchell
        return ret
    }
    fun mitchellTeams(tables:ArrayList<Table>, boards: Int) : MovementSkeleton{
        return MovementSkeleton()
    }

    fun howell(context: Context, tables: ArrayList<Table>, boards: Int) : MovementSkeleton?{
        var movement : MovementSkeleton? = null
        for(i in 0..tables.size){
            var rounds = (tables.size * 2 - 1) - i
            if(boards % rounds != 0) continue
            for(j in 0..48) {
                var boardsUpscaled = boards + (j * (boards/rounds))
                movement = fromFile(context, MovementType.Howell, tables, boardsUpscaled, rounds)
                if(movement != null){
                    movement.scaleDown((movement.boardsPerRound.toFloat() * (boards.toFloat()/boardsUpscaled.toFloat())).toInt())
                    break
                }

            }
            if(movement != null) break

        }
        return movement
    }


    fun fromFile(context: Context, movementType: MovementType, tables : ArrayList<Table>, boards : Int, rounds : Int) : MovementSkeleton? {

        var file: InputStream
        var n = boards
        var boardsPerRound = boards / rounds
        try {
            file =
                context.resources.assets.open("$movementType${tables.size}$rounds$boardsPerRound")
        } catch (e: Exception) {
            try {
                file =
                    context.resources.assets.open("$movementType${tables.size}")
            } catch (e: Exception) {
                return null
            }
        }
        var reader = BufferedReader(InputStreamReader(file))
        return MovementSkeleton().also{it.fromFile(reader, movementType, n)}
    }

}