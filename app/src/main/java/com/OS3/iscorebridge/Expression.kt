package com.OS3.iscorebridge

class Expression(var string: String) {


    var funMap = mutableMapOf(
        "div" to fun (a : Double, b: Double) :Double {return a/b},
        "mul" to fun (a : Double, b: Double) :Double {return a*b},
        "add" to fun (a : Double, b: Double) :Double {return a+b},
        "sub" to fun (a : Double, b: Double) :Double {return a-b}

    )

    fun evaluate() : Double{
        try{
            return string.toDouble()
        }
        catch(e:Exception){ }
        var i = 0
        var op = funMap[string.substring(0, 3)]!!
        var comma = findComma()
        return op(Expression(
            string.substring(4, comma)).evaluate(),
            Expression(string.substring(comma+1, string.length-1)).evaluate())
    }

    fun findComma() : Int{
        var openBrackets = 0
        for(i in 0..string.length){
            if(string[i] == '(') {
                openBrackets++
            }
            else if(string[i] == ')'){
                openBrackets--
            }
            else if(string[i] == ','){
                if(openBrackets == 1){
                    return i
                }
            }
        }
        return -1
    }



}