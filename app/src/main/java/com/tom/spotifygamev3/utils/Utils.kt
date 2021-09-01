package com.tom.spotifygamev3.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.AlbumGameFragmentBinding
import com.tom.spotifygamev3.databinding.HighLowGameFragment3Binding
import com.tom.spotifygamev3.models.spotify_models.Images
import java.text.Normalizer
import java.util.*

object Utils {
    val TAG = "Utils"

    fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
        this.subList(fromIndex, toIndex.coerceAtMost(this.size))

    fun regexedString(string: String, regex: Regex): String {
        return regex.replace(string, "")
    }

    fun regexedString(string: String, regexes: List<Regex>): String {
        var ret = string
        for (reg in regexes) {
            ret = reg.replace(ret, "")
        }
        return ret
    }

    fun cleanedString(string: String): String {
        val original_tokens = string.split(" ").toMutableList()
        val regexed = regexedString(string, Constants.ALPHANUM_REGEX)
        val toRemove = mutableListOf<Int>()
        val splits = regexed.toLowerCase(Locale.ROOT).split(" ")
        val index = splits.indexOf("edition")
        if (index > 0) {
            toRemove.add(index)
            toRemove.add(index - 1)
        }
        toRemove.forEach { original_tokens.removeAt(it) }
        return original_tokens.joinToString(" ")
    }

    //    fun hlShowImage1(images: List<Images>, context: Context, imgView: ImageView, binding: HighLowGameFragment3Binding) {
    fun hlShowImage1(images: List<Images>, context: Context, binding: HighLowGameFragment3Binding) {
        glideShowImagePaletteHL(
            images,
            context,
            binding.imageAns1,
            binding.clAns1,
            binding.artistAns1,
            binding.songAns1,
            binding.divAns1,
            binding.bground1,
            intArrayOf(-1, ContextCompat.getColor(context, R.color.spotify_black))

        )
    }

    fun hlShowImage2(images: List<Images>, context: Context, binding: HighLowGameFragment3Binding) {
        glideShowImagePaletteHL(
            images,
            context,
            binding.imageAns2,
            binding.clAns2,
            binding.artistAns2,
            binding.songAns2,
            binding.divAns2,
            binding.bground2,
            intArrayOf(ContextCompat.getColor(context, R.color.spotify_black), -1)

        )
    }

    private fun glideShowImagePaletteHL(
        images: List<Images>,
        context: Context,
        imgView: ImageView,
        cl: ConstraintLayout,
        artistTv: TextView,
        songTv: TextView,
        div: View,
        bground: View,
        gdColors: IntArray
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            .error(
                reloadHL(
                    context,
                    imgUri2,
                    imgView,
                    cl,
                    artistTv,
                    songTv,
                    div,
                    bground,
                    gdColors
                )
            )
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading_animation)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "single fail HL")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        hlAnim(context, resource, cl, artistTv, songTv, div, bground, gdColors)
                    }
                    return false
                }
            })
            .into(imgView)
    }

    private fun hlAnim(
        context: Context,
        resource: Drawable,
        cl: ConstraintLayout,
        artistTv: TextView,
        songTv: TextView,
        div: View,
        bground: View,
        gdColors: IntArray
    ) {
        val bitmap = resource.toBitmap()
        val builder = Palette.Builder(bitmap)
        val black = ContextCompat.getColor(context, R.color.spotify_black)
        val white = ContextCompat.getColor(context, R.color.spotify_white)
        val grey = ContextCompat.getColor(context, R.color.spotify_grey)
        val index = if (gdColors[0] == -1) 0 else 1
        builder.generate { palette ->
            val fromDomColor = (cl.background as ColorDrawable).color
            val fromMutColor = (bground.background as GradientDrawable).colors?.get(index)
            Log.d(TAG, "from color $fromDomColor")
            val toDomColor = palette?.dominantSwatch?.rgb ?: black
            val toMutColor = palette?.mutedSwatch?.rgb ?: black
//            val toDarkVibColor = palette?.mute?.rgb ?: black
            val animator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromDomColor,
                toDomColor
            )
            val bgAnimator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromMutColor,
                toMutColor
            )
            animator.duration = 250
            bgAnimator.duration = 250
            animator.addUpdateListener { anim ->
                cl.setBackgroundColor(anim.animatedValue as Int)
            }
            bgAnimator.addUpdateListener { anim ->
                gdColors[index] = anim.animatedValue as Int
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    gdColors
                )
                bground.background = gd
            }
            animator.start()
            bgAnimator.start()
            artistTv.setTextColor(
                palette?.dominantSwatch?.bodyTextColor ?: grey
            )
            songTv.setTextColor(
                palette?.dominantSwatch?.bodyTextColor ?: white
            )
            div.setBackgroundColor(
                palette?.dominantSwatch?.bodyTextColor ?: white
            )
        }
    }

    private fun reloadHL(
        context: Context,
        uri: Uri,
        imgView: ImageView,
        cl: ConstraintLayout,
        artistTv: TextView,
        songTv: TextView,
        div: View,
        bground: View,
        gdColors: IntArray
    ) {
        Glide.with(context).load(uri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "single fail HL")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        hlAnim(context, resource, cl, artistTv, songTv, div, bground, gdColors)
                    }
                    return false
                }
            })
            .into(imgView)

    }

    fun glideShowImagePaletteV2(
        images: List<Images>,
        context: Context,
        imgView: ImageView,
        binding: AlbumGameFragmentBinding
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            // on error try reload with 2nd uri
            .error(reloadAC(context, imgUri2, imgView, binding))
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "single fail AC")
                    return false
                }

                // on ready change background colour
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // On image load - animates background change colour
                    if (resource != null) {
                        bgColorAnimation(context, resource, binding)
                    }
                    return false
                }
            })
            .into(imgView)
    }

    // factor out binding potentially
    private fun bgColorAnimation(
        context: Context, resource: Drawable, binding: AlbumGameFragmentBinding
    ) {
        val bitmap = resource.toBitmap()
        val builder = Palette.Builder(bitmap)
        val black = ContextCompat.getColor(context, R.color.spotify_black)
        val white = ContextCompat.getColor(context, R.color.spotify_white)
        var toColor: Int
        builder.generate { palette ->
            // .colors ups the api level to 24 from 21 - some way to use customGradDrawable to get color[0]
            val fromColor = (binding.mainBackground.background as GradientDrawable).colors?.get(0)
            Log.d(TAG, "from color $fromColor")
            toColor = palette?.dominantSwatch?.rgb ?: black
            val animator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromColor,
                toColor
            )
            animator.duration = 250
            animator.addUpdateListener { anim ->
                // make new gradient drawable
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(anim.animatedValue as Int, black)
                )
                binding.mainBackground.background = gd
            }
            animator.start()
            binding.albumScoreCounter.setTextColor(
                palette?.dominantSwatch?.bodyTextColor ?: white
            )
        }
    }

    private fun reloadAC(
        context: Context,
        uri: Uri,
        imageView: ImageView,
        binding: AlbumGameFragmentBinding
    ) {
        Log.d(TAG, "reload")
        Glide.with(context).load(uri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "single fail")
                    return false
                }

                // on ready change background colour
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        bgColorAnimation(context, resource, binding)
                    }
                    return false
                }
            })
            .into(imageView)
    }

    fun glideShowImage(
        images: List<Images>,
        context: Context,
        imageView: ImageView,
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            .error(Glide.with(context).load(imgUri2))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Glide.with(context).load(imgUri2)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d(TAG, "load double failed")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(imageView)

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .apply(
                RequestOptions()
//                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .into(imageView)
    }

    fun glideShowImageLoadAnim(images: List<Images>, context: Context, imageView: ImageView) {
        val imgUri = urlToUri(images[0].url)

        val glide = Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
            )

        if (images.size > 1) {
            val imgUri2 = urlToUri(images[1].url)
            glide.error(Glide.with(context).load(imgUri2))
        }
        glide.into(imageView)
    }

    fun glidePreloadImage(images: List<Images>, context: Context) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .error(
                Glide.with(context)
                    .load(imgUri2)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .preload()
            )
//            .listener(preloadListener(context, imgUri2))
            .preload()
        Log.d(TAG, "Preloaded")

    }

    fun urlToUri(url: String): Uri {
        return url.toUri().buildUpon().scheme("https").build()
    }

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Constants.UNACCENT_REGEX.replace(temp, "")
    }

    fun doAlphaAnimation(imgView: ImageView) {
        val anim = AlphaAnimation(1f, 0f)
        anim.duration = 1500
        anim.fillAfter = true
        imgView.startAnimation(anim)
        imgView.visibility = View.VISIBLE
    }

}