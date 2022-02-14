package com.example.iscorebridge

import kotlin.math.abs

val TEAMS : Int = 0
val PAIRS : Int = 1
val IMPCONVERSION : Array<Int> = arrayOf(0, 20, 50, 90, 130, 170, 220, 270, 320, 370, 430, 500,
    600, 750, 900, 1100, 1300, 1500, 1750, 2000, 2250, 2500, 3000, 3500, 4000)

class Board {
    var boardNumber: Int
    var games: ArrayList<Game> = ArrayList<Game>()
    var vulnerability: Vulnerability
    var dlm = "|||"

    constructor(boardNumber: Int){
        this.boardNumber = boardNumber
        this.vulnerability = calculateVulnerability(boardNumber)
    }

    constructor(board:String){
        var parametersAsString = board.split(dlm + "$")
        var gamesAsString = parametersAsString[1].split(dlm)
        for(game in gamesAsString){
            games.add(Game(game))
        }
        this.boardNumber = parametersAsString[0].toInt()
        this.vulnerability = Vulnerability(parametersAsString[2])
    }

    override fun toString():String{
        var out = boardNumber.toString() + dlm + "$"
        if(games.size > 0){
            out += games[0].toString()
            for(i in 1..games.size){
                out += dlm + games.toString()
            }
        }
        out += dlm + "$" + vulnerability.toString()
        return out

    }

    fun sortGamesByScore() : ArrayList<Game>{
        var sortedList = ArrayList<Game>()
        for(i in 0 until games.size){
            var added = false
            for(j in 0 until sortedList.size){
                if(games[i].score > sortedList[j].score){
                    sortedList.add(j, games[i])
                    added = true
                    break
                }
            }
            if(!added){
                sortedList.add(games[i])
            }
        }
        return sortedList


    }

    fun hasGame(pairNS : Int, pairEW: Int) : Boolean{
        for(game in games){
            if(game.pairNS == pairNS && game.pairEW == pairEW){
                return true
            }
        }
        return false

    }

    fun hasGame(compareGame : Game) : Boolean{
        return hasGame(compareGame.pairNS, compareGame.pairEW)
    }

    fun addGame(game: Game) : Game{
        this.games.add(game)
        return game
    }

    fun getGame(pairNS: Int, pairEW: Int, suit: Char, trickNumbers: Int, tricksMade: Int, lead: String, declarer: Char, doubled: Boolean, redoubled: Boolean) : Game{

        var contract : Contract = Contract(suit, trickNumbers, declarer, doubled, redoubled)
        var leadCard = Card(lead)
        var g : Game = Game(boardNumber, contract, pairNS, pairEW, tricksMade, leadCard, vulnerability)
        return g
    }

    fun IMPConversion(score: Int) : Int{
        for(i in 0 until IMPCONVERSION.size - 1){
            if(score > IMPCONVERSION[i] && score < IMPCONVERSION[i+1]){
                return i
            }
        }
        return 24
    }

    fun teamsScore() : MutableMap<Int, Int?>{
        var scores = HashMap<Int, Int?>()
        for(game1 in this.games){
            for(game2 in this.games){
                if(game1.pairNS == game2.pairEW && game1.pairEW == game2.pairNS && !scores.containsKey(game1.pairNS)){
                    var overallScorewrtNS = game1.score - game2.score
                    var IMPs : Int = IMPConversion(abs(overallScorewrtNS))
                    if(overallScorewrtNS > 0){
                        scores[game1.pairNS] = IMPs
                        scores[game1.pairEW] = IMPs * -1
                    }
                    else{
                        scores[game1.pairNS] = IMPs * -1
                        scores[game1.pairEW] = IMPs
                    }
                }
            }
        }
        return scores
    }

    fun pairScoreToPercent(score: Int) : Int{
        return ((score as Double / (games.size - 1) as Double) * 100.0) as Int
    }

    fun pairScore() : MutableMap<Int, Int?>{
        var scores : MutableMap<Int, Int?> = HashMap<Int, Int?>()
        for(game1 in this.games){
            for(game2 in this.games){
                if(game1.pairNS != game2.pairNS){
                    if(!scores.containsKey(game1.pairNS)){
                        scores[game1.pairNS] = 0
                    }
                    scores[game1.pairNS] = when {
                        game1.score == game2.score -> scores[game1.pairNS] !!+ 1
                        game1.score > game2.score -> scores[game1.pairNS] !!+ 2
                        else -> scores[game1.pairNS]
                    }
                }
                if(game1.pairEW != game2.pairEW){
                    if(!scores.containsKey(game1.pairEW)){
                        scores[game1.pairEW] = 0
                    }
                    scores[game1.pairEW] = when {
                        game1.score == game2.score -> scores[game1.pairEW] !!+ 1
                        game1.score < game2.score -> scores[game1.pairEW] !!+ 2
                        else -> scores[game1.pairEW]
                    }
                }
            }
        }
        return scores
    }

    fun calculateScores(scoringMode : Int) : MutableMap<Int, Int?>{
        return when(scoringMode) {
            GAMEMODE_TEAMS -> teamsScore()
            GAMEMODE_PAIRS -> pairScore()
            else -> HashMap<Int, Int?>()
        }
    }

    private fun calculateVulnerability(boardNumber: Int) : Vulnerability{
        var cycleNum : Int = boardNumber % 4
        return when(cycleNum){
            0->Vulnerability(arrayOf('N', 'E', 'S', 'W'))
            1-> Vulnerability(arrayOf())
            2-> Vulnerability(arrayOf('N', 'S'))
            3-> Vulnerability(arrayOf('E', 'W'))
            else -> Vulnerability(arrayOf())
        }
    }
}