package com.bia.sky_runner

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var planeX = 180f
    private var planeY = 500f
    private var velocityY = 0f

    private var score = 0
    private var fuel = 100f

    private var gameStarted = false
    private var gameOver = false

    private val gravity = 0.45f
    private val flapPower = -8.5f

    private val obstacles = mutableListOf<Obstacle>()
    private val collectibles = mutableListOf<Collectible>()

    private var lastTime = System.currentTimeMillis()
    private var spawnTimer = 0L

    private data class Obstacle(
        var x: Float,
        var y: Float,
        var size: Float
    )

    private data class Collectible(
        var x: Float,
        var y: Float,
        var type: Int
    )

    init {
        paint.typeface = Typeface.DEFAULT
        setBackgroundColor(Color.rgb(135, 206, 235))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        drawSky(canvas, width, height)
        drawAirport(canvas, width, height)
        drawObjects(canvas, width, height)
        drawPlane(canvas)

        drawHud(canvas)

        if (!gameStarted) {
            drawStartScreen(canvas, width, height)
        }

        if (gameOver) {
            drawGameOver(canvas, width, height)
        }

        if (gameStarted && !gameOver) {
            updateGame(width, height)
        }

        postInvalidateDelayed(16)
    }

    private fun drawSky(canvas: Canvas, width: Float, height: Float) {

        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            height,
            Color.rgb(70, 170, 235),
            Color.rgb(220, 240, 255),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, width, height, paint)

        paint.shader = null

        paint.color = Color.WHITE
        paint.alpha = 180

        canvas.drawOval(
            60f,
            100f,
            220f,
            150f,
            paint
        )

        canvas.drawOval(
            width - 250f,
            160f,
            width - 70f,
            210f,
            paint
        )

        paint.alpha = 255
    }

    private fun drawAirport(canvas: Canvas, width: Float, height: Float) {

        val groundTop = height - 150f

        paint.color = Color.rgb(80, 170, 80)
        canvas.drawRect(
            0f,
            groundTop,
            width,
            height,
            paint
        )

        paint.color = Color.DKGRAY
        canvas.drawRect(
            0f,
            groundTop + 30f,
            width,
            groundTop + 95f,
            paint
        )

        paint.color = Color.WHITE

        var x = 0f

        while (x < width) {

            canvas.drawRect(
                x,
                groundTop + 58f,
                x + 55f,
                groundTop + 64f,
                paint
            )

            x += 110f
        }

        // Control tower

        paint.color = Color.LTGRAY

        canvas.drawRect(
            width - 120f,
            groundTop - 180f,
            width - 70f,
            groundTop + 30f,
            paint
        )

        paint.color = Color.DKGRAY

        canvas.drawRect(
            width - 135f,
            groundTop - 200f,
            width - 55f,
            groundTop - 170f,
            paint
        )

        paint.color = Color.rgb(80, 150, 200)

        canvas.drawRect(
            width - 125f,
            groundTop - 193f,
            width - 65f,
            groundTop - 176f,
            paint
        )
    }

    private fun drawObjects(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.textSize = 48f

        for (obstacle in obstacles) {

            paint.color = Color.BLACK

            canvas.drawText(
                "🐦",
                obstacle.x,
                obstacle.y,
                paint
            )
        }

        for (item in collectibles) {

            when (item.type) {

                0 -> {
                    paint.color = Color.YELLOW
                    paint.textSize = 42f

                    canvas.drawText(
                        "⛽",
                        item.x,
                        item.y,
                        paint
                    )
                }

                1 -> {
                    paint.color = Color.YELLOW
                    paint.textSize = 42f

                    canvas.drawText(
                        "🔧",
                        item.x,
                        item.y,
                        paint
                    )
                }
            }
        }
    }

    private fun drawPlane(canvas: Canvas) {

        paint.textSize = 65f

        canvas.drawText(
            "✈",
            planeX,
            planeY,
            paint
        )
    }

    private fun drawHud(canvas: Canvas) {

        paint.color = Color.WHITE
        paint.textSize = 34f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "SCORE: $score",
            30f,
            50f,
            paint
        )

        canvas.drawText(
            "FUEL",
            30f,
            95f,
            paint
        )

        paint.color = Color.DKGRAY

        canvas.drawRect(
            110f,
            70f,
            310f,
            92f,
            paint
        )

        paint.color = Color.GREEN

        canvas.drawRect(
            110f,
            70f,
            110f + (200f * fuel / 100f),
            92f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
    }

    private fun drawStartScreen(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = Color.argb(150, 0, 0, 0)

        canvas.drawRect(
            0f,
            0f,
            width,
            height,
            paint
        )

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.textSize = 48f

        canvas.drawText(
            "BIA SKY RUNNER",
            width / 2,
            height / 2 - 80f,
            paint
        )

        paint.textSize = 28f

        canvas.drawText(
            "AIRPORT MAINTENANCE MISSION",
            width / 2,
            height / 2 - 35f,
            paint
        )

        paint.textSize = 30f

        canvas.drawText(
            "TAP TO START",
            width / 2,
            height / 2 + 60f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawGameOver(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = Color.argb(170, 0, 0, 0)

        canvas.drawRect(
            0f,
            0f,
            width,
            height,
            paint
        )

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.textSize = 50f

        canvas.drawText(
            "MISSION FAILED",
            width / 2,
            height / 2 - 50f,
            paint
        )

        paint.textSize = 30f

        canvas.drawText(
            "SCORE: $score",
            width / 2,
            height / 2 + 10f,
            paint
        )

        canvas.drawText(
            "TAP TO RESTART",
            width / 2,
            height / 2 + 70f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun updateGame(
        width: Float,
        height: Float
    ) {

        val currentTime = System.currentTimeMillis()
        val delta = currentTime - lastTime

        lastTime = currentTime

        velocityY += gravity
        planeY += velocityY

        fuel -= 0.015f

        if (fuel <= 0f) {

            fuel = 0f
            gameOver = true
        }

        spawnTimer += delta

        if (spawnTimer > 1600L) {

            spawnTimer = 0L

            val randomY = Random.nextFloat() *
                    (height - 300f) + 120f

            obstacles.add(
                Obstacle(
                    width + 50f,
                    randomY,
                    50f
                )
            )

            if (Random.nextFloat() > 0.4f) {

                collectibles.add(
                    Collectible(
                        width + 80f,
                        randomY - 80f,
                        Random.nextInt(0, 2)
                    )
                )
            }
        }

        for (obstacle in obstacles) {

            obstacle.x -= 6f

            if (
                obstacle.x < planeX + 40f &&
                obstacle.x > planeX - 50f &&
                obstacle.y > planeY - 55f &&
                obstacle.y < planeY + 20f
            ) {

                gameOver = true
            }
        }

        obstacles.removeAll {
            it.x < -100f
        }

        val iterator = collectibles.iterator()

        while (iterator.hasNext()) {

            val item = iterator.next()

            item.x -= 6f

            if (
                item.x < planeX + 50f &&
                item.x > planeX - 50f &&
                item.y > planeY - 60f &&
                item.y < planeY + 20f
            ) {

                if (item.type == 0) {

                    fuel = minOf(
                        100f,
                        fuel + 30f
                    )

                } else {

                    score += 10
                }

                iterator.remove()
            }
        }

        if (
            planeY < 80f ||
            planeY > height - 170f
        ) {

            gameOver = true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {

            if (!gameStarted) {

                gameStarted = true
                gameOver = false
                score = 0
                fuel = 100f
                planeY = 500f
                velocityY = 0f

                obstacles.clear()
                collectibles.clear()

                lastTime = System.currentTimeMillis()

            } else if (gameOver) {

                gameOver = false
                gameStarted = true

                score = 0
                fuel = 100f
                planeY = 500f
                velocityY = 0f

                obstacles.clear()
                collectibles.clear()

                lastTime = System.currentTimeMillis()

            } else {

                velocityY = flapPower
            }

            performClick()

            return true
        }

        return true
    }

    override fun performClick(): Boolean {

        super.performClick()

        return true
    }
}
