package com.OS3.iscorebridge


val NORTH = Cardinality('N')
val EAST = Cardinality('E')
val SOUTH = Cardinality('S')
val WEST = Cardinality('W')
val EMPTYCARDINALITY = Cardinality(5)

val CARDINALITIES = arrayOf(NORTH, EAST, SOUTH, WEST)

class Cardinality {

    var cardinInt : Int

    val cardinalityToNumberMap = mapOf(
        'N' to 0,
        'E' to 1,
        'S' to 2,
        'W' to 3
    )



    val numberToCardinalityMap = mapOf(
        0 to 'N',
        1 to 'E',
        2 to 'S',
        3 to 'W'
    )

    fun add(amount : Int){
        cardinInt += amount
        cardinInt %= 4
    }

    fun add(other : Cardinality){
        add(other.cardinInt)
    }

    fun differenceBetween(other : Cardinality) : Int{
        return this.cardinInt - other.cardinInt
    }

    constructor(cardinality: Char){
        cardinInt = cardinalityToNumberMap[cardinality]!!
    }

    constructor(other :Cardinality){
        this.cardinInt = other.cardinInt
    }

    constructor(cardinality: Cardinality, add : Int){
        this.cardinInt = cardinality.cardinInt
        add(add)
    }

    constructor(string: String) : this(string.toInt())

    constructor(cardinInt : Int){
        this.cardinInt = cardinInt
    }

    fun isOpposition(other: Cardinality) : Boolean{
        return (this.cardinInt + other.cardinInt)%2 != 0
    }

    fun increment(){
        add(1)
    }

    fun decrement(){
        add(5)
    }

    override fun hashCode(): Int {
        return this.cardinInt
    }

    override fun equals(other: Any?): Boolean {
        return try{
            var otherCardin = other as Cardinality
            otherCardin.cardinInt == this.cardinInt
        } catch(e : ClassCastException){
            false
        }
    }

    override fun toString(): String {
        return cardinInt.toString()
    }

    fun toDisplayString() : String{
        return numberToCardinalityMap[cardinInt]!!.toString()
    }



}