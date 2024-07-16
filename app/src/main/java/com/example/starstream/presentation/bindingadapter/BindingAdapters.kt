package com.example.starstream.presentation.bindingadapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.starstream.R
import com.example.starstream.domain.model.Genre
import com.example.starstream.domain.model.Movie
import com.example.starstream.presentation.ui.favoritemovies.FavoriteMoviesFragmentDirections
import com.example.starstream.presentation.ui.home.HomeFragmentDirections
import com.example.starstream.presentation.ui.movielists.MovieListsFragmentDirections
import com.example.starstream.presentation.ui.search.SearchFragmentDirections
import com.example.starstream.presentation.ui.seeall.SeeAllFragmentDirections
import com.example.starstream.util.Constants
import com.example.starstream.util.ImageQuality
import com.example.starstream.util.InfiniteScrollListener
import com.example.starstream.util.IntentType
import com.example.starstream.util.MediaType
import com.example.starstream.util.isDarkColor
import com.example.starstream.util.setTintColor
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jp.wasabeef.glide.transformations.CropTransformation

@BindingAdapter("isVisible")
fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("android:layout_width")
fun View.setWidth(width: Float) {
    layoutParams.width = width.toInt()
}

@BindingAdapter("android:layout_height")
fun View.setHeight(height: Float) {
    layoutParams.height = height.toInt()
}

@BindingAdapter("detailMediaType", "detailId", "detailImageUrl", "seasonNumber", requireAll = false)
fun View.setDetailsIntent(mediaType: MediaType, id: Int, imageUrl: String?, seasonNumber: Int?) {
    var backgroundColor = ContextCompat.getColor(context, R.color.day_night_inverse)

    imageUrl?.let {
        Glide.with(context)
            .asBitmap()
            .load("https://image.tmdb.org/t/p/w92$it")
            .priority(Priority.HIGH)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate().dominantSwatch?.rgb?.let { dominantColor ->
                        backgroundColor = dominantColor
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
    setOnClickListener {
        val navController = findNavController()
        val action = when (mediaType) {
            MediaType.MOVIE -> {
                when (navController.currentDestination?.id) {
                    R.id.seeAllFragment -> SeeAllFragmentDirections.actionSeeAllFragmentToMovieDetailsFragment(id, backgroundColor)
                    R.id.homeFragment -> HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(id, backgroundColor)
                    R.id.movieListsFragment -> MovieListsFragmentDirections.actionMovieListsFragmentToMovieDetailsFragment(id, backgroundColor)
                    R.id.favoriteMoviesFragment -> FavoriteMoviesFragmentDirections.actionFavoriteMoviesFragmentToMovieDetailsFragment(id,backgroundColor)
                    R.id.searchFragment -> SearchFragmentDirections.actionSearchFragmentToMovieDetailsFragment(id, backgroundColor)

                    else -> {
                        Log.e("BindingAdapter", "Unsupported destination id: ${navController.currentDestination?.id} for media type: $mediaType")
                        null
                    }
                }
            }
            else -> {
                Log.e("BindingAdapter", "Unsupported media type: $mediaType")
                null
            }
        }
        if (action != null) {
            navController.navigate(action)
        } else {
            Log.e("BindingAdapter", "Navigation action is null for media type: $mediaType with destination id: ${navController.currentDestination?.id}")
        }
    }
}


@BindingAdapter("intentType", "mediaType", "intId", "stringId", "title", "backgroundColor", "region", "list", "isLandscape", requireAll = false)
fun View.setSeeAllIntent(
    intentType: IntentType,
    mediaType: MediaType?,
    detailId: Int?,
    listId: String?,
    title: String,
    backgroundColor: Int,
    region: String?,
    list: List<Any>?,
    isLandscape: Boolean?
) {
    setOnClickListener {
        val navController = findNavController()

        val actionId = when (intentType) {
            IntentType.LIST -> R.id.action_movieListsFragment_to_seeAllFragment
            IntentType.DETAILS -> R.id.action_favoriteMoviesFragment_to_movieDetailsFragment
            IntentType.SEARCH -> R.id.action_searchFragment_to_seeAllFragment
            //add
            else -> throw IllegalArgumentException("Unsupported intent type")
        }

        val bundle = bundleOf(
            Constants.INTENT_TYPE to intentType as Parcelable,
            Constants.TITLE to title,
            Constants.BACKGROUND_COLOR to backgroundColor
        )

        mediaType?.let { bundle.putParcelable(Constants.MEDIA_TYPE, it) }
        detailId?.let { bundle.putInt(Constants.DETAIL_ID, it) }
        listId?.let { bundle.putString(Constants.LIST_ID, it) }
        region?.let { bundle.putString(Constants.REGION, it) }
        list?.let { bundle.putParcelableArrayList(Constants.LIST, ArrayList<Movie>()) }
        isLandscape?.let { bundle.putBoolean(Constants.IS_LANDSCAPE, it) }

        try {
            navController.navigate(actionId, bundle)
        } catch (e: IllegalArgumentException) {
            Log.e("NavigationError", "Failed to navigate: ${e.message}")
        }
    }
}


@BindingAdapter("android:layout_marginBottom", "isImage", requireAll = false)
fun View.setLayoutMarginBottom(isGrid: Boolean, isImage: Boolean) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.bottomMargin = resources.getDimension(
        if (isGrid) {
            if (isImage) R.dimen.bottom_margin_small else R.dimen.bottom_margin
        } else R.dimen.zero_dp
    ).toInt()

    layoutParams = params
}

@BindingAdapter("android:background")
fun View.setBackground(color: Int) {
    setBackgroundColor(if (color != 0) color else ContextCompat.getColor(context, R.color.day_night_inverse))
}

@BindingAdapter("transparentBackground")
fun View.setTransparentBackground(backgroundColor: Int) {
    setBackgroundColor(ColorUtils.setAlphaComponent(backgroundColor, 220))
}

@BindingAdapter("type", "isGrid", "loadMore", "shouldLoadMore", requireAll = false)
fun RecyclerView.addInfiniteScrollListener(type: Any?, isGrid: Boolean, infiniteScroll: InfiniteScrollListener, shouldLoadMore: Boolean) {
    if (shouldLoadMore) {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val layoutManagerType = if (isGrid) layoutManager as GridLayoutManager else layoutManager as LinearLayoutManager
            private val visibleThreshold = 10
            private var loading = true
            private var previousTotal = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val visibleItemCount = layoutManagerType.childCount
                val totalItemCount = layoutManagerType.itemCount
                val firstVisibleItem = layoutManagerType.findFirstVisibleItemPosition()

                if (totalItemCount < previousTotal) previousTotal = 0

                if (loading && totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    infiniteScroll.onLoadMore(type)
                    loading = true
                }
            }
        })
    }
}

@BindingAdapter("fixedSize")
fun RecyclerView.setFixedSize(hasFixedSize: Boolean) {
    setHasFixedSize(hasFixedSize)
}

@SuppressLint("CheckResult")
@BindingAdapter("imageUrl", "imageMediaType", "imageQuality", "centerCrop", "fitTop", "isThumbnail", requireAll = false)
fun ImageView.loadImage(posterPath: String?, mediaType: MediaType?, quality: ImageQuality?, centerCrop: Boolean?, fitTop: Boolean, isThumbnail: Boolean) {
    val imageUrl = if (isThumbnail) "https://img.youtube.com/vi/$posterPath/0.jpg" else quality?.imageBaseUrl + posterPath

    val errorImage = AppCompatResources.getDrawable(
        context,
        when (mediaType) {
            MediaType.MOVIE -> R.drawable.ic_baseline_movie_24
            MediaType.PERSON -> R.drawable.ic_baseline_person_24
            null -> R.drawable.ic_baseline_image_24
        }
    )

    val glide = Glide.with(context)
        .load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade())
        .error(errorImage)
        .skipMemoryCache(false)

    if (centerCrop == true) glide.centerCrop()
    if (fitTop) glide.apply(RequestOptions.bitmapTransform(CropTransformation(0, 1235, CropTransformation.CropType.TOP)))

    glide.into(this)
}

@BindingAdapter("iconTint")
fun ImageView.setIconTint(color: Int?) {
    color?.let { setColorFilter(it.setTintColor()) }
}

@BindingAdapter("coloredRating")
fun TextView.setRatingColor(rating: Double) {
    text = rating.toString()
    setTextColor(
        ContextCompat.getColor(
            context,
            when {
                rating >= 9.0 -> R.color.nine_to_ten
                rating >= 8.0 -> R.color.eight_to_nine
                rating >= 7.0 -> R.color.seven_to_eight
                rating >= 6.0 -> R.color.six_to_seven
                rating >= 5.0 -> R.color.five_to_six
                rating >= 4.0 -> R.color.four_to_five
                rating >= 3.0 -> R.color.three_to_four
                rating >= 2.0 -> R.color.two_to_three
                rating >= 1.0 -> R.color.one_to_two
                rating > 0.0 -> R.color.zero_to_one
                else -> R.color.zero
            }
        )
    )
}

@BindingAdapter("fragment", "backArrowTint", "seeAllTitle", "titleTextColor", requireAll = false)
fun Toolbar.setupToolbar(fragment: Fragment?, backArrowTint: Int?, seeAllTitle: String?, titleTextColor: Int?) {
    fragment?.activity?.let { activity ->
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(this)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            if (seeAllTitle != null) activity.supportActionBar?.title = seeAllTitle
        }

        navigationIcon?.setTint(
            backArrowTint?.takeIf { it != 0 } ?: ContextCompat.getColor(context, R.color.day_night)
        )

        titleTextColor?.let {
            setTitleTextColor(
                if (it != 0) it else ContextCompat.getColor(context, R.color.day_night)
            )
        }

        setNavigationOnClickListener {
            activity.finish()
        }
    } ?: run {
        Log.e("BindingAdapter", "Fragment or activity is null in setupToolbar")
    }
}

@BindingAdapter("collapsingToolbar", "frameLayout", "toolbarTitle", "backgroundColor", requireAll = false)
fun AppBarLayout.setToolbarCollapseListener(collapsingToolbar: CollapsingToolbarLayout, frameLayout: FrameLayout, toolbarTitle: String, backgroundColor: Int) {
    var isShow = true
    var scrollRange = -1
    this.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (scrollRange == -1) {
            scrollRange = appBarLayout?.totalScrollRange!!
        }

        if (scrollRange + verticalOffset == 0) {
            frameLayout.isVisible = false
            collapsingToolbar.setCollapsedTitleTextColor(backgroundColor.setTintColor())
            collapsingToolbar.title = toolbarTitle
            isShow = true
        } else if (isShow) {
            frameLayout.isVisible = isShow
            collapsingToolbar.title = " "
            isShow = false
        }
    })
}

@BindingAdapter("genreMediaType", "genres", "chipTint", requireAll = false)
fun ChipGroup.setGenreChips(mediaType: MediaType, genreList: List<Genre>?, backgroundColor: Int) {
    genreList?.let {
        it.forEach { genre ->
            addView(
                Chip(context).apply {
                    setChipBackgroundColorResource(if (backgroundColor.isDarkColor()) R.color.white else R.color.black)
                    text = genre.name
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setTextColor(backgroundColor.setTintColor(true))
                    setOnClickListener {
                        /*handle genre click */
                    }
                }
            )
        }
    }
}

