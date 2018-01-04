package com.mobilemonkeysoftware.example.textureview

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.TextureView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by AR on 04/01/2018.
 */
class MainActivity : AppCompatActivity() {

    private val listener: TextureView.SurfaceTextureListener by lazy {
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                Log.d("MainActivity", "onSurfaceTextureSizeChanged: $surface, $width, $height")
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                Log.d("MainActivity", "onSurfaceTextureUpdated: $surface")
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {

                Log.d("MainActivity", "onSurfaceTextureDestroyed: $surface")
                camera?.stopPreview()
                camera?.release()
                return true
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {

                Log.d("MainActivity", "onSurfaceTextureAvailable: $surface, $width, $height")
                try {
                    camera = Camera.open()
                    camera?.setPreviewTexture(surface)
                    camera?.startPreview()
                } catch (e: Error) {
                    Log.e("MainActivity", "Camera start preview error", e)
                }
            }
        }
    }
    private val mockWidth = 800
    private val mockHeight = 600

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        texture_view.surfaceTextureListener = listener
        DisplayMetrics().also {
            windowManager.defaultDisplay.getMetrics(it)
            updateSize(it.widthPixels, it.heightPixels)
        }
        // TODO e.g: texture_view.draw()
    }

    private fun updateSize(viewWidth: Int, viewHeight: Int) {
        var scaleX = 1.0f
        var scaleY = 1.0f

        // Aspect ratio
        if (mockWidth > viewWidth && mockHeight > viewHeight) {
            scaleX = (mockWidth / viewWidth).toFloat()
            scaleY = (mockHeight / viewHeight).toFloat()
        } else if (mockWidth < viewWidth && mockHeight < viewHeight) {
            scaleY = (viewWidth / mockWidth).toFloat()
            scaleX = (viewHeight / mockHeight).toFloat()
        } else if (viewWidth > mockWidth) {
            scaleY = ((viewWidth / mockWidth) / (viewHeight / mockHeight)).toFloat()
        } else if (viewHeight > mockHeight) {
            scaleX = ((viewHeight / mockHeight) / (viewWidth / mockWidth)).toFloat()
        }

        // Center crop
        val pivotPointX: Float = (viewWidth / 2).toFloat()
        val pivotPointY: Float = (viewHeight / 2).toFloat()

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY)

        texture_view.setTransform(matrix)
        texture_view.layoutParams = ConstraintLayout.LayoutParams(viewWidth, viewHeight)
    }

}
