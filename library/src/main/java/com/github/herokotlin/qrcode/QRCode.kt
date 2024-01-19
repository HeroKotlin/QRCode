package com.github.herokotlin.qrcode

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.ChecksumException
import com.google.zxing.FormatException
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader

class QRCode {

    companion object {

        @JvmStatic fun decodeQRCode(bitmap: Bitmap): String {

            val readSource = fun(sourceImage: Bitmap): String {
                val width = sourceImage.width
                val height = sourceImage.height
                val data = IntArray(width * height)
                sourceImage.getPixels(data, 0, width, 0, 0, width, height)

                var text = ""

                val source = RGBLuminanceSource(width, height, data)
                val reader = QRCodeReader()
                val sourceList = listOf(source, source.invert())
                for (element in sourceList) {
                    try {
                        val result = reader.decode(BinaryBitmap(HybridBinarizer(element)))
                        text = result.text
                    }
                    catch (e: NotFoundException) {
                        e.printStackTrace()
                    }
                    catch (e: ChecksumException) {
                        e.printStackTrace()
                    }
                    catch (e: FormatException) {
                        e.printStackTrace()
                    }
                    if (text.isNotBlank()) {
                        break
                    }
                }
                return text
            }

            var result: String

            // 图片太大无法识别二维码
            var sourceImage = bitmap
            var width = bitmap.width
            var height = bitmap.height

            while (true) {
                result = readSource(sourceImage)
                // 回收临时对象
                if (sourceImage != bitmap) {
                    sourceImage.recycle()
                }
                if (result.isNotBlank()) {
                    break
                }
                else {
                    width = (width.toFloat() * 0.7).toInt()
                    height = (height.toFloat() * 0.7).toInt()
                    if (width < 200 || height < 200) {
                        break
                    }
                    sourceImage = Bitmap.createScaledBitmap(bitmap, width, height, true)
                }
            }

            return result

        }

    }
}