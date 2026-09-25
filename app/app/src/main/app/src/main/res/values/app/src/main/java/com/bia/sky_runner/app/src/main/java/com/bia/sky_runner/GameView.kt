package com.bia.sky_runner

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var planeY = 0f
    private var velocity = 0f

    private var score = 0
    private var parts = 0
    private var fuel = 100f

    private var running = false
    private var gameStarted = false

    private var lastTime = System.nanoTime()
    private var spawnTimer = 0f

    private val items = mutableListOf<Item>()

    data class Item(
        var x: Float,
        var y: Float,
        var type: Int,
        var collected: Boolean = false
    )

    init {
        setBackgroundColor(Color.rgb(135, 206, 250))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        if (planeY == 0f) {
            planeY = height * 0.45f
        }

        drawSky(canvas, width, height)
        drawGround(canvas, width, height)
        drawRunway(canvas, width, height)
        drawClouds(canvas, width, height)
        drawTower(canvas, width, height)
        drawItems(canvas)
        drawPlane(canvas, width)
        drawHUD(canvas, width)

        if (!running) {
            drawStartScreen(canvas, width, height)
        }

        if (running) {
            updateGame(width, height)
        }

        postInvalidateOnAnimation()
    }

    // =========================
    // BACKGROUND
    // =========================

    private fun drawSky(canvas: Canvas, width: Float, height: Float) {

        paint.color = Color.rgb(135, 206, 250)

        canvas.drawRect(
            0f,
            0f,
            width,
            height * 0.74f,
            paint
        )
    }

    private fun drawGround(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = Color.rgb(93, 145, 82)

        canvas.drawRect(
            0f,
            height * 0.74f,
            width,
            height,
            paint
        )
    }

    private fun drawRunway(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = Color.DKGRAY

        canvas.drawRect(
            0f,
            height * 0.82f,
            width,
            height * 0.96f,
            paint
        )

        paint.color = Color.WHITE

        for (i in 0..10) {

            val x = i * width / 10f + 10f

            canvas.drawRect(
                x,
                height * 0.885f,
                x + 55f,
                height * 0.90f,
                paint
            )
        }
    }

    // =========================
    // CLOUDS
    // =========================

    private fun drawClouds(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = Color.WHITE

        canvas.drawCircle(
            width * 0.16f,
            height * 0.16f,
            28f,
            paint
        )

        canvas.drawCircle(
            width * 0.20f,
            height * 0.14f,
            35f,
            paint
        )

        canvas.drawCircle(
            width * 0.24f,
            height * 0.17f,
            25f,
            paint
        )

        canvas.drawCircle(
            width * 0.68f,
            height * 0.24f,
            25f,
            paint
        )

        canvas.drawCircle(
            width * 0.72f,
            height * 0.22f,
            34f,
            paint
        )

        canvas.drawCircle(
            width * 0.77f,
            height * 0.25f,
            24f,
            paint
        )
    }

    // =========================
    // CONTROL TOWER
    // =========================

    private fun drawTower(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = Color.rgb(210, 220, 225)

        canvas.drawRect(
            width * 0.84f,
            height * 0.48f,
            width * 0.91f,
            height * 0.74f,
            paint
        )

        paint.color = Color.rgb(50, 70, 85)

        canvas.drawRect(
            width * 0.81f,
            height * 0.45f,
            width * 0.94f,
            height * 0.51f,
            paint
        )

        paint.color = Color.rgb(40, 80, 120)

        canvas.drawRect(
            width * 0.83f,
            height * 0.40f,
            width * 0.92f,
            height * 0.45f,
            paint
        )
    }

    // =========================
    // GAME ITEMS
    // =========================

    private fun drawItems(canvas: Canvas) {

        for (item in items) {

            if (item.collected) continue

            paint.textSize = 34f

            val symbol = when (item.type) {
                0 -> "🐦"
                1 -> "⛽"
                else -> "🔧"
            }

            canvas.drawText(
                symbol,
                item.x,
                item.y,
                paint
            )
        }
    }

    // =========================
    // AIRPLANE
    // =========================

    private fun drawPlane(
        canvas: Canvas,
        width: Float
    ) {

        paint.textSize = 52f

        canvas.drawText(
            "✈",
            width * 0.20f,
            planeY,
            paint
        )
    }

    // =========================
    // HUD
    // =========================

    private fun drawHUD(
        canvas: Canvas,
        width: Float
    ) {

        paint.color = Color.WHITE
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.textSize = 24f

        canvas.drawText(
            "BIA SKY RUNNER",
            18f,
            34f,
            paint
        )

        paint.textSize = 20f

        canvas.drawText(
            "Score: $score",
            18f,
            65f,
            paint
        )

        canvas.drawText(
            "Parts: $parts",
            18f,
            92f,
            paint
        )

        // Fuel background

        paint.color = Color.DKGRAY

        canvas.drawRect(
            width - 145f,
            24f,
            width - 25f,
            40f,
            paint
        )

        // Fuel level

        paint.color = Color.GREEN

        canvas.drawRect(
            width - 145f,
            24f,
            width - 145f + 120f * (fuel / 100f),
            40f,
            paint
        )

        paint.color = Color.WHITE
        paint.textSize = 15f

        canvas.drawText(
            "FUEL",
            width - 145f,
            60f,
            paint
        )
    }

    // =========================
    // START / GAME OVER SCREEN
    // =========================

    private fun drawStartScreen(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {

        paint.color = 0xAA000000.toInt()

        canvas.drawRect(
            0f,
            0f,
            width,
            height,
            paint
        )

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        paint.textSize = 32f

        canvas.drawText(
            if (!gameStarted)
                "BIA SKY RUNNER"
            else
                "GAME OVER",
            width / 2f,
            height * 0.42f,
            paint
        )

        paint.textSize = 20f

        canvas.drawText(
            if (!gameStarted)
                "TAP TO START"
            else
                "TAP TO FLY AGAIN",
            width / 2f,
            height * 0.49f,
            paint
        )

        paint.textSize = 15f

        canvas.drawText(
            "Collect fuel and maintenance parts",
            width / 2f,
            height * 0.55f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    // =========================
    // GAME UPDATE
    // =========================

    private fun updateGame(
        width: Float,
        height: Float
    ) {

        val currentTime = System.nanoTime()

        val deltaTime =
            ((currentTime - lastTime) / 1_000_000_000f)
                .coerceAtMost(0.032f)

        lastTime = currentTime

        // Gravity

        velocity += 620f * deltaTime

        planeY += velocity * deltaTime

        // Fuel consumption

        fuel -= 3.5f * deltaTime

        // Spawn objects

        spawnTimer -= deltaTime

        if (spawnTimer <= 0f) {

            val randomY =
                height * 0.15f +
                        height * 0.43f *
                        Random.nextFloat()

            items.add(
                Item(
                    width + 30f,
                    randomY,
                    Random.nextInt(3)
                )
            )

            spawnTimer = 1.15f
        }

        // Move objects

        for (item in items) {

            item.x -=
                (260f + score * 4f) *
                        deltaTime

            val distanceX =
                abs(item.x - width * 0.20f)

            val distanceY =
                abs(item.y - planeY)

            // Collision

            if (
                !item.collected &&
                distanceX < 45f &&
                distanceY < 45f
            ) {

                item.collected = true

                when (item.type) {

                    // Bird
                    0 -> {
                        gameOver()
                    }

                    // Fuel
                    1 -> {
                        fuel =
                            (fuel + 30f)
                                .coerceAtMost(100f)
                    }

                    // Maintenance part
                    2 -> {
                        parts++
                        score += 10
                    }
                }
            }

            // Successfully passed obstacle

            if (
                !item.collected &&
                item.x <
                width * 0.20f - 45f &&
                item.type == 0
            ) {

                score++
                item.collected = true
            }
        }

        items.removeAll {
            it.x < -80f ||
                    it.collected
        }

        // Boundaries

        if (
            planeY < 65f ||
            planeY > height * 0.72f ||
            fuel <= 0f
        ) {

            gameOver()
        }
    }

    // =========================
    // GAME OVER
    // =========================

    private fun gameOver() {

        running = false
    }

    // =========================
    // TOUCH CONTROL
    // =========================

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (event.action ==
            MotionEvent.ACTION_DOWN
        ) {

            if (!running) {

                score = 0
                parts = 0
                fuel = 100f

                planeY =
                    height * 0.45f

                velocity = 0f

                items.clear()

                running = true
                gameStarted = true

                lastTime =
                    System.nanoTime()

            } else {

                // TAP = AIRPLANE GOES UP

                velocity = -330f
            }

            return true
        }

        return true
    }
}
