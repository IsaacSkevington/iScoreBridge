package com.OS3.iscorebridge

import kotlin.math.pow

class Base64Int{

    var value : Int

    val fromMap = mapOf<Char, Int>(
        '0' to 0,
        '1' to 1,
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'A' to 10,
        'B' to 11,
        'C' to 12,
        'D' to 13,
        'E' to 14,
        'F' to 15,
        'G' to 16,
        'H' to 17,
        'I' to 18,
        'J' to 19,
        'K' to 20,
        'L' to 21,
        'M' to 22,
        'N' to 23,
        'O' to 24,
        'P' to 25,
        'Q' to 26,
        'R' to 27,
        'S' to 28,
        'T' to 29,
        'U' to 30,
        'V' to 31,
        'W' to 32,
        'X' to 33,
        'Y' to 34,
        'Z' to 35,
        'a' to 36,
        'b' to 37,
        'c' to 38,
        'd' to 39,
        'e' to 40,
        'f' to 41,
        'g' to 42,
        'h' to 43,
        'i' to 44,
        'j' to 45,
        'k' to 46,
        'l' to 47,
        'm' to 48,
        'n' to 49,
        'o' to 50,
        'p' to 51,
        'q' to 52,
        'r' to 53,
        's' to 54,
        't' to 55,
        'u' to 56,
        'v' to 57,
        'w' to 58,
        'x' to 59,
        'y' to 61,
        'z' to 62,
        '!' to 63
    )
    val toMap = fromMap.entries.associate{(k,v)-> v to k}

    constructor(s : String){
        value = 0
        var position = s.length - 1
        for(char in s){
            value += fromMap[char]!!.times(64.toDouble().pow(position)).toInt()
            position--
        }
    }
    constructor(i : Int){
        this.value = i
    }

    override fun toString(): String {
        var numerator = value
        var out = ""
        while(numerator != 0){
            out += toMap[numerator % 64]
            numerator /= 64
        }
        return out
    }

    constructor(base16Int: Base16Int) : this(base16Int.value)


}

class Base16Int{

    var value : Int
    constructor(){
        value = 0
    }

    constructor(i : Int){
        value = i
    }
    constructor(hex : String){
        value = hex.toInt(16)
    }
    constructor(bsi : Base64Int){
        value = bsi.value
    }

    fun toMAC() : String{
        var stringAddress = toString()
        var MAC = stringAddress[0].toString()
        for(i in 1..stringAddress.length){
            if(i%2 == 0){
                MAC += ":"
            }
            MAC += stringAddress[i]
        }
        return MAC
    }

    fun fromMAC(MAC : String){
        var hexString = ""
        for(char in MAC){
            if(char != ':'){
                hexString += char.toString()
            }
        }
        value = hexString.toInt(16)
    }

    override fun toString(): String {
        return value.toString(16)
    }

}
