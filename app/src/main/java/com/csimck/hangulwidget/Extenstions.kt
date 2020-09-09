package com.csimck.hangulwidget

fun Int.halfAsFloat() : Float { return this * 0.5f}
fun Float.half() : Float { return this * 0.5f}
fun Int.doubled() : Int { return this * 2}
fun Int.percentage(max : Int) : Float { return this.toFloat() / max.coerceAtLeast(1).toFloat()}
fun Int.mapToRange(srcMin : Float, srcMax : Float, dstMin : Float, dstMax : Float) : Int {
    return (((this.toFloat() - srcMin) / (srcMax - srcMin)) * (dstMax - dstMin) + dstMin).toInt()
}
