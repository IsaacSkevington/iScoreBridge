package com.OS3.iscorebridge

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.annotation.RequiresApi

class TablePage(private val columnTitles : Array<String>){

    private var columns : MutableMap<String, ArrayList<String>> = HashMap()
    private var maxSizes : MutableMap<String, Int> = HashMap()
    private val columnSpacing = 10
    private val characterWidth = 1.5f

    private val characterHeight = 1
    private val FONT_SIZE = 12f
    private val rowHeight = FONT_SIZE + 20
    private var tableWidth = 0
    private var totalRows = 1

    private fun getCharacterHeight(fontSize : Float) : Float{
        return characterHeight * fontSize
    }

    private fun getCharacterWidth(fontSize : Float) : Float{
        return characterWidth * fontSize
    }

    init{
        for (column in columnTitles){
            columns[column] = ArrayList()
            maxSizes[column] = column.length
            tableWidth += column.length*getCharacterWidth(FONT_SIZE).toInt() + columnSpacing
        }

    }

    fun addRow(rowValues : Array<String>){
        if(rowValues.size != columnTitles.size){
            throw IllegalArgumentException("Not all columns have values")
        }
        val map : MutableMap<String, String> = HashMap()
        for(i in rowValues.indices){
            map[columnTitles[i]] = rowValues[i]
        }
        addRow(map)
    }

    private fun addRow(rowValues : MutableMap<String, String>){
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
        var currentX = x
        var currentY = y
        val titlePaint = Paint()
        titlePaint.textSize = FONT_SIZE
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.isFakeBoldText = true
        val linePaint = Paint()
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

        val textPaint = Paint()
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