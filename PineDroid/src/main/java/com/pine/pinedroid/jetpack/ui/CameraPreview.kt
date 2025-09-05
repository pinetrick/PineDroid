package com.pine.pinedroid.jetpack.ui

import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pine.pinedroid.utils.appContext
import java.util.concurrent.TimeUnit

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    flashMode: Int = ImageCapture.FLASH_MODE_OFF,
    onUseCase: (imageCapture: ImageCapture) -> Unit = {}
) {
    val lifecycleOwner = LocalInspectionMode.current.takeIf { it }?.let { null } ?: LocalLifecycleOwner.current
    val isInPreview = LocalInspectionMode.current
    if (isInPreview) return

    // 用 key 包裹，让 cameraSelector 或 flashMode 改变时重建
    key(cameraSelector, flashMode) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageCapture = ImageCapture.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .setFlashMode(flashMode)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        val camera: Camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )

                        // 返回 ImageCapture 用例
                        onUseCase(imageCapture)

                        // 点击对焦
                        previewView.afterMeasured {
                            setOnTouchListener { _, event ->
                                if (event.action == android.view.MotionEvent.ACTION_UP) {
                                    val factory = meterPointFactory()
                                    val point = factory.createPoint(event.x, event.y)
                                    val action = FocusMeteringAction.Builder(
                                        point,
                                        FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                                    ).setAutoCancelDuration(3, TimeUnit.SECONDS)
                                        .build()
                                    camera.cameraControl.startFocusAndMetering(action)
                                }
                                true
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )
    }
}

// Helper: 确保 PreviewView 已经测量完成
private fun PreviewView.afterMeasured(f: PreviewView.() -> Unit) {
    if (width > 0 && height > 0) {
        f()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        })
    }
}

// Helper: 获取 MeteringPointFactory
private fun PreviewView.meterPointFactory(): MeteringPointFactory = SurfaceOrientedMeteringPointFactory(width.toFloat(), height.toFloat())
