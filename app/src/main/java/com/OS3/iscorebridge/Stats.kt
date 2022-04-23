package com.OS3.iscorebridge

import android.view.View
import android.widget.TextView
import kotlin.math.roundToInt


class Stats(val number : Int, val match : Match) {


    var boardsPlayed : Int = 0
    var totalTimeTaken : Int = 0
    lateinit var contractsDeclared : ArrayList<Game>
    lateinit var allBoards : ArrayList<Game>
    lateinit var contractsDeclaredByType : MutableMap<ContractType, ArrayList<Game>>
    lateinit var scores : MutableMap<Int, Int?>



    lateinit var contractsDefended : ArrayList<Game>
    lateinit var contractsDefendedByType : MutableMap<ContractType, ArrayList<Game>>




    init{

        populate(match)
    }

    fun splitGamesByNumberDeclaration(games : ArrayList<Game>, number : Int) : Pair<ArrayList<Game>, ArrayList<Game>>{
        var declared = ArrayList<Game>()
        var defended = ArrayList<Game>()
        games.forEach {
            if(it.declaredBy(number)){
                declared.add(it)
            }
            else{
                defended.add(it)
            }
        }
        return Pair(declared, defended)
    }

    fun splitGamesByContractType(games : ArrayList<Game>) : MutableMap<ContractType, ArrayList<Game>>{
        var map = HashMap<ContractType, ArrayList<Game>>()
        map[ContractType.PARTSCORE] = ArrayList()
        map[ContractType.GAME] = ArrayList()
        map[ContractType.SLAM] = ArrayList()
        games.forEach {
            map[it.contract.type]!!.add(it)
        }
        return map
    }


    private fun populate(match: Match){
        allBoards = match.getGames(number)
        scores = HashMap()
        match.boards.forEach {
            var boardScores = it.value!!.calculateScores(gameInfo.gameMode)
            if(boardScores[number] != null){
                scores[it.value!!.boardNumber] = boardScores[number]
            }
        }
        boardsPlayed = allBoards.size
        totalTimeTaken = 0
        var splitContracts = splitGamesByNumberDeclaration(allBoards, number)
        contractsDeclared = splitContracts.first
        contractsDefended = splitContracts.second
        contractsDeclaredByType = splitGamesByContractType(splitContracts.first)
        contractsDefendedByType = splitGamesByContractType(splitContracts.second)
    }

    fun refresh(){
        populate(match)
    }

    fun getContractsMade(games : ArrayList<Game>):Int{
        var made = 0
        games.forEach {
            if(it.made()){
                made++
            }
        }
        return made
    }

    fun getContractsDown(games : ArrayList<Game>) : Int{
        return games.size - getContractsMade(games)
    }

    fun percentageOfString(numerator : Int, denominator : Int) : String{

        var fraction = numerator.toDouble() / denominator.toDouble()
        if(fraction.isNaN()){
            return "-"
        }
        var percentage = fraction * 100f
        return percentage.roundToInt().toString()
    }

    fun getPositiveScore(games : ArrayList<Game>) : Int{
        var positive = 0
        games.forEach {
            if(scores[it.boardNumber]!! > 0){
                positive++
            }
        }
        return positive
    }

    fun display(view : View){
        view.findViewById<TextView>(R.id.boardsPlayedView).text = boardsPlayed.toString()
        view.findViewById<TextView>(R.id.timePerBoardView).text = totalTimeTaken.toString()


        //Declaring

        //Part Scores
        var partScores = contractsDeclaredByType[ContractType.PARTSCORE]!!.size
        var partScoresMade = getContractsMade(contractsDeclaredByType[ContractType.PARTSCORE]!!)
        var partScoresPositiveScore = getPositiveScore(contractsDeclaredByType[ContractType.PARTSCORE]!!)
        view.findViewById<TextView>(R.id.partScoresBid).text = partScores.toString()
        view.findViewById<TextView>(R.id.partScoresMade).text =
            percentageOfString(partScoresMade, partScores)
        view.findViewById<TextView>(R.id.partScoresSuccess).text =
            percentageOfString(partScoresPositiveScore, partScores)

        //Games
        var games = contractsDeclaredByType[ContractType.GAME]!!.size
        var gamesMade = getContractsMade(contractsDeclaredByType[ContractType.GAME]!!)
        var gamesPositiveScore = getPositiveScore(contractsDeclaredByType[ContractType.GAME]!!)
        view.findViewById<TextView>(R.id.gamesBid).text = games.toString()
        view.findViewById<TextView>(R.id.gamesMade).text = percentageOfString(gamesMade, games)
        view.findViewById<TextView>(R.id.gamesSuccess).text =
            percentageOfString(gamesPositiveScore, games)

        //Slams
        var slams = contractsDeclaredByType[ContractType.SLAM]!!.size
        var slamsMade = getContractsMade(contractsDeclaredByType[ContractType.SLAM]!!)
        var slamsPositiveScore = getPositiveScore(contractsDeclaredByType[ContractType.SLAM]!!)
        view.findViewById<TextView>(R.id.slamsBid).text = slams.toString()
        view.findViewById<TextView>(R.id.slamsMade).text = percentageOfString(slamsMade, slams)
        view.findViewById<TextView>(R.id.slamsSuccess).text =
            percentageOfString(slamsPositiveScore, slams)

        //Total
        var total = boardsPlayed
        var totalMade = getContractsMade(allBoards)
        var totalPositiveScore = getPositiveScore(allBoards)
        view.findViewById<TextView>(R.id.totalBid).text = total.toString()
        view.findViewById<TextView>(R.id.totalMade).text = percentageOfString(totalMade, total)
        view.findViewById<TextView>(R.id.totalSuccess).text =
            percentageOfString(totalPositiveScore, total)

        //Defending

        //Part Scores
        var partScoresDefending = contractsDefendedByType[ContractType.PARTSCORE]!!.size
        var partScoresTakenDownDefending = getContractsDown(contractsDefendedByType[ContractType.PARTSCORE]!!)
        var partScoresPositiveScoreDefending = getPositiveScore(contractsDefendedByType[ContractType.PARTSCORE]!!)
        view.findViewById<TextView>(R.id.partScoresDefended).text = partScoresDefending.toString()
        view.findViewById<TextView>(R.id.partScoresTakenDown).text =
            percentageOfString(partScoresTakenDownDefending, partScoresDefending)
        view.findViewById<TextView>(R.id.partScoresDefenceSuccess).text =
            percentageOfString(partScoresPositiveScoreDefending, partScoresDefending)

        //Games
        var gamesDefending = contractsDefendedByType[ContractType.GAME]!!.size
        var gamesTakenDownDefending = getContractsDown(contractsDefendedByType[ContractType.GAME]!!)
        var gamesPositiveScoreDefending = getPositiveScore(contractsDefendedByType[ContractType.GAME]!!)
        view.findViewById<TextView>(R.id.gamesDefended).text = gamesDefending.toString()
        view.findViewById<TextView>(R.id.gamesTakenDown).text =
            percentageOfString(gamesTakenDownDefending, gamesDefending)
        view.findViewById<TextView>(R.id.gamesDefenceSuccess).text =
            percentageOfString(gamesPositiveScoreDefending, gamesDefending)

        //Slams
        var slamsDefending = contractsDefendedByType[ContractType.SLAM]!!.size
        var slamsTakenDownDefending = getContractsDown(contractsDefendedByType[ContractType.SLAM]!!)
        var slamsPositiveScoreDefending = getPositiveScore(contractsDefendedByType[ContractType.SLAM]!!)
        view.findViewById<TextView>(R.id.slamsDefended).text = slamsDefending.toString()
        view.findViewById<TextView>(R.id.slamsTakenDown).text =
            percentageOfString(slamsTakenDownDefending, slamsDefending)
        view.findViewById<TextView>(R.id.slamsDefenceSuccess).text =
            percentageOfString(slamsPositiveScoreDefending, slamsDefending)

        //Total
        var totalDefended = contractsDefended.size
        var totalMadeDefending = getContractsDown(contractsDefended)
        var totalPositiveScoreDefending = getPositiveScore(contractsDefended)
        view.findViewById<TextView>(R.id.totalDefended).text = totalDefended.toString()
        view.findViewById<TextView>(R.id.totalTakenDown).text =
            percentageOfString(totalMadeDefending, totalDefended)
        view.findViewById<TextView>(R.id.totalDefenceSuccess).text =
            percentageOfString(totalPositiveScoreDefending, totalDefended)
    }

}