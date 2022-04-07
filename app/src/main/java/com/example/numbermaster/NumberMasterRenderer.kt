package com.example.numbermaster

import android.opengl.GLSurfaceView
import android.opengl.GLU
import javax.microedition.khronos.opengles.GL10
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class NumberMasterRenderer constructor(private val context: AppCompatActivity, private val textureIds: MutableList<Int>) : GLSurfaceView.Renderer {
    val rotateUp: Int = 0
    val rotateRight: Int = 1
    val rotateDown: Int = 2
    val rotateLeft: Int = 3

    private var textureKind: Int = 0
    private var gl: GL10? = null
    private var textures: IntArray = IntArray(3)
    private val pointAbs: Float = ((2 / 4F) + (2 / 3.4F))
    private val verticesGroups = listOf(
        // 正面
        floatArrayOf(
            -this.pointAbs, -this.pointAbs, +this.pointAbs,  // (x, y, z)
            +this.pointAbs, -this.pointAbs, +this.pointAbs,  // (x, y, z)
            -this.pointAbs, +this.pointAbs, +this.pointAbs,  // (x, y, z)
            +this.pointAbs, +this.pointAbs, +this.pointAbs,  // (x, y, z)
        ),

        // 奥
        floatArrayOf(
            +this.pointAbs, -this.pointAbs, -this.pointAbs,  // (x, y, z)
            -this.pointAbs, -this.pointAbs, -this.pointAbs,  // (x, y, z)
            +this.pointAbs, +this.pointAbs, -this.pointAbs,  // (x, y, z)
            -this.pointAbs, +this.pointAbs, -this.pointAbs,  // (x, y, z)
        ),

        // 右
        floatArrayOf(
            +this.pointAbs, -this.pointAbs, +this.pointAbs,  // (x, y, z)
            +this.pointAbs, -this.pointAbs, -this.pointAbs,  // (x, y, z)
            +this.pointAbs, +this.pointAbs, +this.pointAbs,  // (x, y, z)
            +this.pointAbs, +this.pointAbs, -this.pointAbs,  // (x, y, z)
        ),

        // 左
        floatArrayOf(
            -this.pointAbs, -this.pointAbs, -this.pointAbs,  // (x, y, z)
            -this.pointAbs, -this.pointAbs, +this.pointAbs,  // (x, y, z)
            -this.pointAbs, +this.pointAbs, -this.pointAbs,  // (x, y, z)
            -this.pointAbs, +this.pointAbs, +this.pointAbs,  // (x, y, z)
        ),

        // 上
        floatArrayOf(
            +this.pointAbs, +this.pointAbs, -this.pointAbs,  // (x, y, z)
            -this.pointAbs, +this.pointAbs, -this.pointAbs,  // (x, y, z)
            +this.pointAbs, +this.pointAbs, +this.pointAbs,  // (x, y, z)
            -this.pointAbs, +this.pointAbs, +this.pointAbs,  // (x, y, z)
        ),

        // 下
        floatArrayOf(
            -this.pointAbs, -this.pointAbs, -this.pointAbs,  // (x, y, z)
            +this.pointAbs, -this.pointAbs, -this.pointAbs,  // (x, y, z)
            -this.pointAbs, -this.pointAbs, +this.pointAbs,  // (x, y, z)
            +this.pointAbs, -this.pointAbs, +this.pointAbs,  // (x, y, z)
        ),
    )
    private val rotateAngle: Float = 9F
    private var rotateCount: Int = 0
    private var rotateDirection: Int = this.rotateUp
    var execRotate: Boolean = false

    override fun onSurfaceCreated(gl: GL10, config: javax.microedition.khronos.egl.EGLConfig?) {
        this.gl = gl
        gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F) // 背景を塗る
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f) // オブジェクトを塗る

        gl.glEnable(GL10.GL_CULL_FACE)
        gl.glGenTextures(this.textures.size, this.textures, 0)

        val texturePoint: FloatArray = floatArrayOf(
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
        )
        val textureBuffer = ByteBuffer.allocateDirect(verticesGroups[0].size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer.put(texturePoint)
        textureBuffer.position(0)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        var textureImage: Bitmap
        var pixels: IntArray
        for (i in 0..2) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textures[i])
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            textureImage = BitmapFactory.decodeResource(this.context.resources, this.textureIds[i])
            pixels = IntArray(textureImage.width * textureImage.height)
            textureImage.getPixels(pixels, 0, textureImage.width, 0, 0, textureImage.width, textureImage.height)
            gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, textureImage.width, textureImage.height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, IntBuffer.wrap(pixels))
        }
    }

    fun changeTexture(kind: Int) {
        this.textureKind = kind - 1
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)

        val ratio = height / width.toFloat()
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glFrustumf(-1F, 1F, -ratio, ratio, 2F, 6F)

        GLU.gluLookAt(gl,
            0f, 0f, 4f,  // カメラの位置（視点）
            0f, 0f, 0f,  // カメラの向き（注視点）
            0f, 1f, 0f   // カメラ姿勢（上方向を表すベクトル）
        )
    }

    override fun onDrawFrame(gl: GL10) {
        // 画面のクリア
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textures[this.textureKind])

        for (verticesGroup in this.verticesGroups) {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, Util.makeFloatBuffer(verticesGroup))
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, verticesGroup.size / 3)
        }

        if (this.execRotate) {
            this.doRotate()
        }

        gl.glDisable(GL10.GL_TEXTURE_2D)
    }

    fun rotateStart(rotateDirection: Int) {
        this.execRotate = true
        this.rotateDirection = rotateDirection
    }

    private fun doRotate() {
        when (this.rotateDirection) {
            this.rotateUp -> this.gl!!.glRotatef(-this.rotateAngle, 1F, 0F, 0F)
            this.rotateRight -> this.gl!!.glRotatef(this.rotateAngle, 0F, 1F, 0F)
            this.rotateDown -> this.gl!!.glRotatef(this.rotateAngle, 1F, 0F, 0F)
            this.rotateLeft -> this.gl!!.glRotatef(-this.rotateAngle, 0F, 1F, 0F)
        }

        this.rotateCount += 1
        if (this.rotateCount >= 10) {
            this.rotateCount = 0
            this.execRotate = false
        }
    }
}