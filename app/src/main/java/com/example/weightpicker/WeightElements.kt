package com.example.weightpicker

import android.graphics.Color.WHITE
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Scale(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    minWeight: Int = 20,
    maxWeight: Int = 250,
    initialWeight: Int = 80,
    onWeightChange: (Int) -> Unit
){
    val radius = style.radius
    val scaleWidth = style.scaleWidth
    var center by remember {
        mutableStateOf(Offset.Zero)
    }
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var angle by remember {
        mutableStateOf(0f)
    }
    Canvas(modifier = modifier) {
        center = this.center
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )
        val outerRadius = radius.toPx() + scaleWidth.toPx() /2f
        val innerRadius = radius.toPx() - scaleWidth.toPx() /2f
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                android.graphics.Paint().apply {
                    strokeWidth = scaleWidth.toPx()
                    color = WHITE
                    setStyle(android.graphics.Paint.Style.STROKE)
                    setShadowLayer(
                        60f,
                        0f,
                        0f,
                        android.graphics.Color.argb(50,0,0,0)
                    )

                }
            )
            for (i in minWeight..maxWeight){
                val angleInRad = (i - initialWeight + angle - 90) * (PI/180f).toFloat()

                val lineType = when{
                    i%10 == 0->LineType.TenStep
                    i%5 == 0->LineType.FiveStep
                    else ->LineType.Normal
                }

                val lineColor = when(lineType){
                    LineType.Normal -> style.normalLineColor
                    LineType.FiveStep -> style.fiveStepLineColor
                    else -> style.tenStepLineColor
                }

                val lineLength = when(lineType){
                    LineType.TenStep -> style.tenStepLineLength.toPx()
                    LineType.FiveStep -> style.fiveStepLineLength.toPx()
                    else -> style.normalStepLineLength.toPx()
                }

                val lineStart = Offset(
                    x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
                    y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
                )

                val lineEnd  = Offset(
                    x = outerRadius * cos(angleInRad) + circleCenter.x,
                    y = outerRadius * sin(angleInRad) + circleCenter.y
                )

                drawContext.canvas.nativeCanvas.apply {
                    if(lineType is LineType.TenStep){
                        val textRadius = outerRadius - lineLength - style.textSize.toPx() - 5.dp.toPx()
                        val x = textRadius * cos(angleInRad) + circleCenter.x
                        val y = textRadius * sin(angleInRad) + circleCenter.y

                        withRotation(
                            degrees = angleInRad * (180f/ PI.toFloat()) + 90f,
                            pivotX = x,
                            pivotY = y
                        ) {
                            drawText(
                                abs(i).toString(),
                                x,
                                y,
                                android.graphics.Paint().apply {
                                    textSize = style.textSize.toPx()
                                    textAlign = android.graphics.Paint.Align.CENTER

                                }
                            )
                        }
                    }
                }

                drawLine(
                    color = lineColor,
                    start = lineStart,
                    end = lineEnd,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    var weight by remember {
        mutableStateOf(80)
    }
    Box(modifier = Modifier.fillMaxSize()){
        Scale(
            style = ScaleStyle(
                scaleWidth = 150.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)

        ) {
            weight = it
        }
    }
}