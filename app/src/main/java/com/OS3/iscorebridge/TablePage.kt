package com.OS3.iscorebridge

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.annotation.RequiresApi

class TablePage(val columnTitles : Array<String>){

    var columns : MutableMap<String, ArrayList<String>> = HashMap<String, ArrayList<String>>()
    var maxSizes : MutableMap<String, Int> = HashMap<String, Int>()
    final val columnSpacing = 10
    final val characterWidth = 1.5f

    final val characterHeight = 1
    final val FONT_SIZE = 12f
    final val rowHeight = FONT_SIZE + 20
    var tableWidth = 0
    var totalRows = 1

    fun getCharacterHeight(fontSize : Float) : Float{
        return characterHeight * fontSize
    }

    fun getCharacterWidth(fontSize : Float) : Float{
        return characterWidth * fontSize
    }

    init{
        for (column in columnTitles){
            columns[column] = ArrayList<String>()
            maxSizes[column] = column.length
            tableWidth += column.length*getCharacterWidth(FONT_SIZE).toInt() + columnSpacing
        }

    }

    fun addRow(rowValues : Array<String>){
        if(rowValues.size != columnTitles.size){
            throw IllegalArgumentException("Not all columns have values")
        }
        var map : MutableMap<String, String> = HashMap<String, String>()
        for(i in rowValues.indices){
            map[columnTitles[i]] = rowValues[i]
        }
        addRow(map)
    }

    fun addRow(rowValues : MutableMap<String, String>){
        if(rowValues.size != columnTitles.size){
            throw IllegalArgumentException("Not all columns have values")
        }
        for (column in columnTitles){
            rowValues[column]?.let {
                columns[column]!!.add(it)
                if (it.length > maxSizes[column]!!) {
                    tableWidth -= maxSizes[column]!! * getCharacterWidth(FONT_SIZE).toInt()
                    tableWidth += it.length * getCharacterWidth(FONT_SIZE).toInt()
                    maxSizes[column] = it.length
                }
            }
        }
        totalRows++
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun draw(page : PdfDocument.Page, x : Float, y : Float, borders : Boolean){
        var currentX = x;
        var currentY = y;
        var titlePaint = Paint()
        titlePaint.textSize = FONT_SIZE
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.isFakeBoldText = true
        var linePaint = Paint()
        if(borders) {
            linePaint.color = Color.BLACK
        }
        else{
            linePaint.color = Color.WHITE
        }
        for(column in columnTitles){
            page.canvas.drawText(column, currentX, currentY, titlePaint)
            currentX += columnSpacing/2
            page.canvas.drawLine(currentX, currentY, currentX, currentY + (rowHeight * totalRows), linePaint)
            currentX += maxSizes[column]!! * getCharacterWidth(titlePaint.textSize) + columnSpacing/2
        }
        currentX = x
        for(row in 0..totalRows){
            page.canvas.drawLine(currentX, currentY, currentX + tableWidth, currentY, linePaint)
            currentY += rowHeight
        }

        var textPaint = Paint()
        textPaint.textSize = FONT_SIZE
        for(column in columnTitles){
            currentY = y + rowHeight/2
            for(value in columns[column]!!){
                page.canvas.drawText(value, currentX, currentY + rowHeight/2 - getCharacterHeight(FONT_SIZE), textPaint)
                currentY += rowHeight
            }
            currentX += maxSizes[column]!! * getCharacterWidth(textPaint.textSize) + columnSpacing
        }
    }

}