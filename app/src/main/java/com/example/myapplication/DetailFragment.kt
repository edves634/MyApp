package com.example.myapplication

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet

class DetailFragment : Fragment() {

    // UI-элементы
    private lateinit var scrollView: ScrollView
    private lateinit var headerImage: ImageView
    private lateinit var backButton: ImageView

    // Создание анимации перехода
    private fun createTransitionSet(duration: Long): TransitionSet {
        return TransitionSet().apply {
            addTransition(ChangeBounds()) // Анимация изменения границ
            addTransition(ChangeTransform()) // Анимация трансформации
            addTransition(ChangeImageTransform()) // Анимация изменения изображения
            this.duration = duration // Длительность анимации
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Настройка анимации перехода
        sharedElementEnterTransition = createTransitionSet(500)
        sharedElementReturnTransition = createTransitionSet(500)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Создание основного контейнера с прокруткой
        scrollView = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor("#1B1E26".toColorInt()) // Темный фон
            isFillViewport = true // Заполнение всего пространства
            overScrollMode = View.OVER_SCROLL_NEVER // Отключение эффекта перетягивания
        }

        try {
            // Создание основного контента
            val relativeLayout = createRelativeLayout()
            scrollView.addView(relativeLayout)
        } catch (e: Exception) {
            Log.e("DetailFragment", "Error creating fragment: ${e.message}", e)
        }

        return scrollView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка анимированного перехода для главного изображения
        ViewCompat.setTransitionName(headerImage, MainActivity.SHARED_ELEMENT_NAME)

        // Отложенный старт анимации до полной готовности view
        postponeEnterTransition()
        headerImage.doOnPreDraw {
            startPostponedEnterTransition()
        }

        // Обработка клика на кнопку назад
        backButton.setOnClickListener {
            val mainActivity = activity as? MainActivity
            val sharedView = mainActivity?.getSharedImageView()

            // Восстановление shared element для обратной анимации
            if (sharedView != null) {
                ViewCompat.setTransitionName(sharedView, MainActivity.SHARED_ELEMENT_NAME)
            }

            // Разное поведение в зависимости от ориентации
            if (isLandscape()) {
                // Простое удаление фрагмента в ландшафте
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(this)
                    .commit()
            } else {
                // Анимация закрытия в портрете
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, view.height.toFloat()).apply {
                    duration = 300
                    addListener(object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: android.animation.Animator) {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    })
                    start()
                }
            }
        }

        // Автоматическая прокрутка к началу
        view.post {
            scrollView.scrollTo(0, 0)
            // Дополнительный отступ для планшетов
            if (isTablet()) {
                scrollView.smoothScrollBy(0, dpToPx(24))
            }
        }

        // Восстановление позиции прокрутки
        savedInstanceState?.let {
            val y = it.getInt("SCROLL_POSITION_Y", 0)
            scrollView.post { scrollView.scrollTo(0, y) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохранение позиции прокрутки
        outState.putInt("SCROLL_POSITION_Y", scrollView.scrollY)
    }

    // Проверка ландшафтной ориентации
    private fun isLandscape() =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Проверка планшетного режима по минимальной ширине экрана
    private fun isTablet(): Boolean {
        val context = requireContext()
        val configuration = context.resources.configuration
        return configuration.smallestScreenWidthDp >= 600
    }

    // Создание основного контейнера
    private fun createRelativeLayout(): RelativeLayout {
        return RelativeLayout(requireContext()).apply {
            // Адаптация размеров под ориентацию
            layoutParams = if (isLandscape()) {
                RelativeLayout.LayoutParams(
                    (resources.displayMetrics.widthPixels * 0.6).toInt(),
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    // Дополнительный отступ для планшетов
                    if (isTablet()) topMargin = dpToPx(24)
                }
            } else {
                RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            setPadding(0, dpToPx(24), 0, 0)

            // Минимальная высота для корректного отображения
            minimumHeight = resources.displayMetrics.heightPixels - dpToPx(80)

            // Фон с закругленными углами
            background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_top_corners)

            // Построение иерархии view
            headerImage = createHeaderImage()
            addView(headerImage)

            val gradientOverlay = createGradientOverlay(headerImage.id)
            addView(gradientOverlay)

            val ageRating = createAgeRating()
            addView(ageRating)

            val titleText = createTitleText(ageRating.id)
            addView(titleText)

            val ratingContainer = createRatingContainer(titleText.id)
            addView(ratingContainer)

            addRatingStars(ratingContainer)

            val genresText = createGenresText(ratingContainer.id)
            addView(genresText)

            val storylineLabel = createStorylineLabel(genresText.id)
            addView(storylineLabel)

            val storylineText = createStorylineText(storylineLabel.id)
            addView(storylineText)

            val castLabel = createCastLabel(storylineText.id)
            addView(castLabel)

            val scrollViewActors = createActorsScrollView(castLabel.id)
            addView(scrollViewActors)

            backButton = createBackButton()
            addView(backButton)
        }
    }

    // Создание заглавного изображения
    private fun createHeaderImage(): ImageView {
        // Получение ресурса изображения из аргументов
        val imageRes = arguments?.getInt(MainActivity.EXTRA_IMAGE_RES) ?: R.drawable.img

        return ImageView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                // Адаптивная высота для разных устройств
                height = dpToPx(if (isTablet()) 450 else 400)
                topMargin = dpToPx(if (isLandscape()) 24 else 16)
            }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_CROP
            try {
                setImageResource(imageRes)
            } catch (e: Exception) {
                Log.e("DetailFragment", "Error setting header image", e)
            }
        }
    }

    // Создание градиентного оверлея
    private fun createGradientOverlay(headerImageId: Int): View {
        return View(requireContext()).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                dpToPx(232)
            ).apply {
                // Выравнивание по низу изображения
                addRule(RelativeLayout.ALIGN_BOTTOM, headerImageId)
            }

            // Градиент от прозрачного к цвету фона
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.TRANSPARENT, "#1B1E26".toColorInt())
            ).apply {
                gradientType = GradientDrawable.LINEAR_GRADIENT
            }
        }
    }

    // Создание возрастного рейтинга
    private fun createAgeRating(): TextView {
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                topMargin = dpToPx(if (isTablet()) 360 else 320)
            }
            alpha = 0.5f // Полупрозрачность
            gravity = Gravity.CENTER
            text = getString(R.string.range)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setPadding(dpToPx(4), 0, dpToPx(4), 0)
            includeFontPadding = false // Точное выравнивание
        }
    }

    // Создание заголовка
    private fun createTitleText(belowId: Int): TextView {
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(306),
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                topMargin = dpToPx(8)
            }
            gravity = Gravity.CENTER
            text = getString(R.string.the_mandalorian)
            setTextColor("#ECECEC".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
            setTypeface(typeface, Typeface.BOLD)
            setShadowLayer(6f, 0f, 6f, Color.BLACK) // Тень для лучшей читаемости
        }
    }

    // Контейнер для рейтинга в звездах
    private fun createRatingContainer(belowId: Int): LinearLayout {
        return LinearLayout(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                topMargin = dpToPx(8)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
    }

    // Добавление звезд рейтинга
    private fun addRatingStars(container: LinearLayout) {
        // 4 заполненные звезды
        for (i in 0 until 4) {
            ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(20), dpToPx(20)).apply {
                    marginEnd = dpToPx(2)
                }
                try {
                    setImageResource(R.drawable.ic_star_filled)
                    setColorFilter("#FFB800".toColorInt()) // Желтый цвет
                    container.addView(this)
                } catch (e: Exception) {
                    Log.e("DetailFragment", "Error adding filled star", e)
                }
            }
        }

        // 1 пустая звезда
        ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(20), dpToPx(20))
            try {
                setImageResource(R.drawable.star_icon)
                setColorFilter("#6D6D80".toColorInt()) // Серый цвет
                container.addView(this)
            } catch (e: Exception) {
                Log.e("DetailFragment", "Error adding gray star", e)
            }
        }
    }

    // Отображение жанров
    private fun createGenresText(belowId: Int): TextView {
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                topMargin = dpToPx(8)
                marginStart = dpToPx(16)
                marginEnd = dpToPx(16)
            }
            gravity = Gravity.CENTER
            text = getString(R.string.action_adventure_fantasy)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            isSingleLine = false
            maxLines = 2 // Максимум 2 строки
            ellipsize = TextUtils.TruncateAt.END // Многоточие в конце
        }
    }

    // Заголовок сюжета
    private fun createStorylineLabel(belowId: Int): TextView {
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(343),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                topMargin = dpToPx(36)

                // Адаптация под ориентацию
                when (resources.configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        addRule(RelativeLayout.ALIGN_PARENT_START)
                        marginStart = dpToPx(16)
                    }
                    else -> addRule(RelativeLayout.CENTER_HORIZONTAL)
                }
            }
            gravity = Gravity.START
            text = getString(R.string.storyline)
            setTextColor("#ECECEC".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setShadowLayer(6f, 0f, 6f, Color.BLACK)
        }
    }

    // Текст сюжета
    private fun createStorylineText(belowId: Int): TextView {
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            val isLandscape = isLandscape()

            layoutParams = RelativeLayout.LayoutParams(
                if (isLandscape) ViewGroup.LayoutParams.MATCH_PARENT else dpToPx(343),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                topMargin = dpToPx(8)

                if (isLandscape) {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    marginStart = dpToPx(16)
                    marginEnd = dpToPx(16)
                } else {
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                }
            }
            alpha = 0.75f // Полупрозрачность
            text = getString(R.string.the_travels)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setLineSpacing(dpToPx(4).toFloat(), 1.0f) // Межстрочный интервал
            gravity = Gravity.START
        }
    }

    // Заголовок актерского состава
    private fun createCastLabel(belowId: Int): TextView {
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            val isLandscape = isLandscape()

            layoutParams = RelativeLayout.LayoutParams(
                if (isLandscape) ViewGroup.LayoutParams.MATCH_PARENT else dpToPx(343),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                topMargin = dpToPx(36)

                if (isLandscape) {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    marginStart = dpToPx(16)
                    marginEnd = dpToPx(16)
                } else {
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                }
            }
            gravity = Gravity.START
            text = getString(R.string.cast_and_crew)
            setTextColor("#ECECEC".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setShadowLayer(6f, 0f, 6f, Color.BLACK)
        }
    }

    // Горизонтальный список актеров
    private fun createActorsScrollView(belowId: Int): HorizontalScrollView {
        return HorizontalScrollView(requireContext()).apply {
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, belowId)
                topMargin = dpToPx(8)
                bottomMargin = dpToPx(if (isTablet()) 32 else 16)
                marginStart = dpToPx(16)
                marginEnd = dpToPx(16)
            }
            isHorizontalScrollBarEnabled = false // Скрыть полосу прокрутки

            // Контейнер для карточек актеров
            val container = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(dpToPx(16), 0, dpToPx(16), 0)

                // Данные актеров
                val actors = listOf(
                    "Pedro Pascal",
                    "Carl Weathers",
                    "Gina Carano",
                    "Misty Rosas",
                    "Rio Hackford",
                    "Chris Bartlett"
                )

                val photos = listOf(
                    R.drawable.pedro_pascal,
                    R.drawable.carl_weathers,
                    R.drawable.gina_carano,
                    R.drawable.misty_rosas,
                    R.drawable.rio_hackford,
                    R.drawable.chris_bartlett
                )

                // Создание карточек для каждого актера
                actors.forEachIndexed { i, actor ->
                    try {
                        addView(createActorCard(actor, photos[i]))
                    } catch (e: OutOfMemoryError) {
                        Log.e("DetailFragment", "Memory error: $actor", e)
                    } catch (e: Exception) {
                        Log.e("DetailFragment", "Error: $actor", e)
                    }
                }
            }

            addView(container)
        }
    }

    // Создание карточки актера
    private fun createActorCard(name: String, photoRes: Int): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(129),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(if (isTablet()) 16 else 8)
            }

            // Фото актера
            val photo = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(129),
                    dpToPx(152)
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                try {
                    setBackgroundResource(R.drawable.rounded_corners) // Закругленные углы
                    setImageResource(photoRes)
                } catch (e: Exception) {
                    Log.e("DetailFragment", "Error setting actor photo: $name", e)
                }
            }
            addView(photo)

            // Имя актера
            val nameText = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, dpToPx(12), 0, dpToPx(8))
                gravity = Gravity.CENTER
                text = name
                setTextColor(Color.WHITE)
                alpha = 0.75f
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                maxLines = 2 // Максимум 2 строки
                ellipsize = TextUtils.TruncateAt.END
            }
            addView(nameText)
        }
    }

    // Создание кнопки "Назад"
    private fun createBackButton(): ImageView {
        return ImageView(requireContext()).apply {
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(48),
                dpToPx(48)
            ).apply {
                marginStart = dpToPx(21)
                topMargin = dpToPx(32)
            }

            try {
                setImageResource(R.drawable.ic_back)
            } catch (e: Exception) {
                Log.e("DetailFragment", "Error setting back button icon", e)
            }

            setColorFilter(Color.WHITE) // Белый цвет иконки
            isClickable = true
            isFocusable = true
            contentDescription = getString(R.string.back_button_desc) // Доступность
        }
    }

    // Конвертация dp в пиксели
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    // Конвертация строки в цвет
    private fun String.toColorInt(): Int {
        return Color.parseColor(this)
    }
}