package com.OS3.iscorebridge

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources

@RequiresApi(Build.VERSION_CODES.M)
class StarButton(context : Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet){

    var checked = false
    private var _onTurnOn = {}
    private var _onTurnOff = {}

    private var onImageView : ImageView
    private var offImageView : ImageView


    init{
        val on = AppCompatResources.getDrawable(context, R.drawable.outline_star_24).also {
            it!!.setTint(resources.getColor(R.color.starColor, context.theme))
        }
        val off = AppCompatResources.getDrawable(context, R.drawable.outline_star_outline_24).also {
            it!!.setTint(Color.BLACK)
        }
        var imageLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        onImageView = ImageView(context).also{
            it.setImageDrawable(on)
            it.layoutParams = imageLayoutParams
            it.visibility = INVISIBLE
        }
        offImageView = ImageView(context).also{
            it.setImageDrawable(off)
            it.layoutParams
        }
        addView(onImageView)
        addView(offImageView)
        setOnClickListener {
            if(checked){
                setOff()
            }
            else{
                setOn()
            }
        }
    }
    val spinOut : Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.spin_out)}
    val spinIn : Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.spin_in)}


    fun setOnTurnOn(f : () -> Unit){
        this._onTurnOn = f
    }
    fun setOnTurnOff(f : () -> Unit){
        this._onTurnOff = f
    }



    fun setOn(){
        offImageView.startAnimation(spinOut)
        onImageView.startAnimation(spinIn)
        onImageView.visibility = VISIBLE
        offImageView.visibility = INVISIBLE
        _onTurnOn()
        checked = true

    }

    fun setOff(){
        offImageView.startAnimation(spinIn)
        onImageView.startAnimation(spinOut)
        onImageView.visibility = INVISIBLE
        offImageView.visibility = VISIBLE
        _onTurnOff()
        checked = false
    }

    fun update(){
        if(checked){
            setOn()
        }
        else{
            setOff()
        }
    }



}