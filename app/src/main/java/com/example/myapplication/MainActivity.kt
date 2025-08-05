package com.example.myapplication

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    companion object {
        // Имя shared element для анимации перехода
        const val SHARED_ELEMENT_NAME = "shared_image"
        // Ключ для передачи ресурса изображения
        const val EXTRA_IMAGE_RES = "image_res"
        // Ключи для сохранения состояния при повороте экрана
        private const val KEY_SHARED_IMAGE_VIEW_ID = "shared_image_view_id"
        private const val KEY_DETAIL_OPENED = "detail_opened"
    }

    // Текущий счет (не используется в текущей реализации, но сохраняется)
    private var currentScore = 0
    // Основной контейнер с прокруткой
    private lateinit var scrollView: ScrollView
    // Контейнер для фрагмента детализации
    private lateinit var container: FragmentContainerView
    // ID элемента для анимации shared element transition
    private var sharedImageViewId: Int = -1
    // Главный контейнер с контентом
    private lateinit var mainContainer: LinearLayout
    // Флаг, указывающий открыт ли фрагмент детализации
    private var detailOpened = false
    // Затемняющий слой для портретного режима
    private lateinit var dimmerView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Восстановление состояния после поворота экрана
        savedInstanceState?.let {
            currentScore = it.getInt("SCORE_KEY", 0)
            sharedImageViewId = it.getInt(KEY_SHARED_IMAGE_VIEW_ID, -1)
            detailOpened = it.getBoolean(KEY_DETAIL_OPENED, false)
        }

        // Регистрация обработчика кнопки "назад"
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        // Инициализация контейнера для фрагментов
        container = FragmentContainerView(this).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            visibility = View.GONE // Изначально скрыт
        }

        // Инициализация затемняющего слоя
        dimmerView = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#80000000")) // Полупрозрачный черный
            visibility = View.GONE
            isClickable = true
            // Закрытие фрагмента при клике на затемненную область
            setOnClickListener {
                supportFragmentManager.popBackStack()
            }
        }

        // Создание основной прокручиваемой области
        scrollView = createScrollView()
        mainContainer = createMainLayout()
        scrollView.addView(mainContainer)

        // Корневой layout (FrameLayout)
        val rootLayout = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(scrollView)
            addView(dimmerView)
            addView(container)
        }

        setContentView(rootLayout)

        // Обработка системных инсетов (статус-бар, навигационная панель)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Восстановление позиции прокрутки после поворота
        savedInstanceState?.let {
            val y = it.getInt("SCROLL_POSITION_Y", 0)
            scrollView.post { scrollView.scrollTo(0, y) }
        }

        // Слушатель изменений в back stack фрагментов
        supportFragmentManager.addOnBackStackChangedListener {
            updateContainerVisibility()
        }

        // Восстановление shared element после поворота экрана
        if (sharedImageViewId != -1) {
            scrollView.post {
                val view = findViewById<ImageView>(sharedImageViewId)
                view?.let {
                    ViewCompat.setTransitionName(it, SHARED_ELEMENT_NAME)
                }
            }
        }

        // Восстановление ландшафтного режима после поворота
        if (detailOpened && isLandscape()) {
            container.layoutParams = FrameLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.6).toInt(),
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.END // Выравнивание по правому краю
            }
            container.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE
        }
    }

    // Проверка ландшафтной ориентации
    private fun isLandscape() =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Обновление видимости элементов при изменении стека фрагментов
    private fun updateContainerVisibility() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            // Фрагмент детализации открыт
            container.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE
            // Затемнение только в портретном режиме
            dimmerView.visibility = if (isLandscape()) View.GONE else View.VISIBLE
        } else {
            // Фрагмент детализации закрыт
            container.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
            dimmerView.visibility = View.GONE
        }
    }

    // Создание ScrollView
    private fun createScrollView(): ScrollView {
        return ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#1B1E26")) // Темный фон
            isFillViewport = true // Заполнение всего доступного пространства
        }
    }

    // Создание главного контейнера с контентом
    private fun createMainLayout(): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

            // Добавление заголовка и строк с фильмами
            addView(createTitleTextView())
            createMovieRows().forEach { addView(it) }
        }
    }

    // Создание строк с карточками фильмов
    private fun createMovieRows(): List<LinearLayout> {
        val rows = mutableListOf<LinearLayout>()
        // Данные фильмов: ресурс изображения, количество звезд рейтинга, является ли главным изображением
        val movies = listOf(
            Triple(R.drawable.star_treck, 3, false),
            Triple(R.drawable.mandalorian, 4, true), // Главное изображение
            Triple(R.drawable.witcher, 5, false),
            Triple(R.drawable.joker, 4, false),
            Triple(R.drawable.pataya, 3, false),
            Triple(R.drawable.shestaya, 5, false)
        )

        // Количество колонок зависит от ориентации
        val columns = if (isLandscape()) 3 else 2

        var currentRow: LinearLayout? = null

        // Создание строк и заполнение их карточками
        movies.forEachIndexed { index, (imageRes, stars, isMain) ->
            // Создаем новую строку для каждой группы карточек
            if (index % columns == 0) {
                currentRow = createRowLayout(columns)
                rows.add(currentRow!!)
            }
            // Добавление карточки в текущую строку
            currentRow?.addView(createMovieCard(
                imageRes,
                stars,
                getString(R.string.action_adventure_drama),
                getMovieTitle(index),
                getAgeRating(index),
                isMain
            ))
        }
        return rows
    }

    // Проверка, является ли устройство планшетом (по диагонали экрана)
    private fun isTablet(): Boolean {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        val widthPixels: Int
        val heightPixels: Int
        val xdpi: Float
        val ydpi: Float

        // Получение метрик экрана в зависимости от версии API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            widthPixels = bounds.width()
            heightPixels = bounds.height()
            xdpi = resources.displayMetrics.xdpi
            ydpi = resources.displayMetrics.ydpi
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            widthPixels = displayMetrics.widthPixels
            heightPixels = displayMetrics.heightPixels
            xdpi = displayMetrics.xdpi
            ydpi = displayMetrics.ydpi
        }

        // Расчет диагонали в дюймах
        val widthInches = widthPixels / xdpi
        val heightInches = heightPixels / ydpi
        val diagonalInches = sqrt(widthInches * widthInches + heightInches * heightInches)

        // Считаем устройство планшетом если диагональ >= 6.5 дюймов
        return diagonalInches >= 6.5
    }

    // Получение названия фильма по индексу
    private fun getMovieTitle(index: Int): String {
        return when (index) {
            0 -> getString(R.string.star_trek_picard)
            1 -> getString(R.string.mandalorian)
            2 -> getString(R.string.the_witcher)
            3 -> getString(R.string.joker)
            4 -> getString(R.string.tenet)
            else -> getString(R.string.altered_carbon)
        }
    }

    // Получение возрастного рейтинга по индексу
    private fun getAgeRating(index: Int): String {
        return when (index) {
            0 -> getString(R.string.rang_16)
            1 -> getString(R.string._12)
            2 -> getString(R.string.range_14)
            3 -> getString(R.string.range_18)
            4 -> getString(R.string.range_18)
            else -> getString(R.string._12)
        }
    }

    // Создание заголовка списка фильмов
    private fun createTitleTextView(): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(24)
            }
            text = getString(R.string.movies_list)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            setLineSpacing(0f, 1.0f)
        }
    }

    // Создание строки для карточек фильмов
    private fun createRowLayout(columns: Int): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            orientation = LinearLayout.HORIZONTAL
            weightSum = columns.toFloat() // Равномерное распределение пространства
        }
    }

    // Создание карточки фильма
    private fun createMovieCard(
        imageRes: Int,
        filledStars: Int,
        genres: String,
        title: String,
        ageRating: String,
        isMainImage: Boolean
    ): View {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                getCardHeight() // Динамическая высота карточки
            ).apply {
                weight = 1f // Равномерное распределение ширины
                marginEnd = dpToPx(8) // Отступ между карточками
            }
            setBackgroundColor(Color.parseColor("#2A2E38")) // Цвет фона карточки
            clipToPadding = false
            orientation = LinearLayout.VERTICAL

            // Добавление контента карточки
            addView(createCardContent(imageRes, filledStars, genres, title, ageRating, isMainImage))
        }
    }

    // Определение высоты карточки в зависимости от устройства и ориентации
    private fun getCardHeight(): Int {
        val metrics = resources.displayMetrics
        val isLandscape = isLandscape()

        return when {
            isTablet() && isLandscape -> (metrics.heightPixels * 0.4).toInt()
            isTablet() -> (metrics.heightPixels * 0.3).toInt()
            isLandscape -> (metrics.heightPixels * 0.5).toInt()
            else -> dpToPx(200) // Высота по умолчанию для портретного режима на телефоне
        }
    }

    // Создание контента для карточки фильма
    private fun createCardContent(
        imageRes: Int,
        filledStars: Int,
        genres: String,
        title: String,
        ageRating: String,
        isMainImage: Boolean
    ): FrameLayout {
        return FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)

            // Основное изображение карточки
            addView(ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(imageRes)

                // Обработка клика для главного изображения
                if (isMainImage) {
                    id = View.generateViewId()
                    isClickable = true
                    isFocusable = true
                    // Установка имени для shared element transition
                    ViewCompat.setTransitionName(this, SHARED_ELEMENT_NAME)

                    // Обработчик клика для открытия детализации
                    setOnClickListener {
                        openDetailFragment(this, imageRes)
                    }
                }
            })

            // Градиентный оверлей для текста
            addView(View(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                background = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(Color.BLACK, Color.TRANSPARENT)
                )
            })

            // Контейнер с текстовой информацией
            addView(createTextContainer(filledStars, genres, title))
            // Возрастной рейтинг
            addView(createAgeRating(ageRating))
        }
    }

    // Открытие фрагмента с детализацией фильма
    private fun openDetailFragment(sharedView: View, imageRes: Int) {
        // Сохранение ID элемента для анимации
        sharedImageViewId = sharedView.id
        val fragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_IMAGE_RES, imageRes) // Передача ресурса изображения
            }
        }

        detailOpened = true

        // Обработка ландшафтного режима
        if (isLandscape()) {
            container.layoutParams = FrameLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.6).toInt(),
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.END // Выравнивание по правому краю
            }
            container.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(container.id, fragment)
                .commit()
        }
        // Обработка портретного режима
        else {
            container.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            container.visibility = View.VISIBLE
            dimmerView.visibility = View.VISIBLE // Показ затемнения

            // Расчет позиции для анимации открытия
            calculateDetailPosition { topMargin, screenHeight ->
                (container.layoutParams as FrameLayout.LayoutParams).apply {
                    this.topMargin = topMargin
                    height = screenHeight - topMargin
                }
                container.requestLayout()

                // Открытие фрагмента с анимацией shared element
                supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .addSharedElement(sharedView, SHARED_ELEMENT_NAME)
                    .replace(container.id, fragment)
                    .addToBackStack("detail")
                    .commit()
            }
        }
    }

    // Расчет позиции открытия детализации в портретном режиме
    private fun calculateDetailPosition(callback: (topMargin: Int, screenHeight: Int) -> Unit) {
        scrollView.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val topMargin = when {
                // Расчет позиции относительно второго элемента в первой строке
                mainContainer.childCount > 1 -> {
                    val firstRow = mainContainer.getChildAt(1) as? LinearLayout
                    if (firstRow != null && firstRow.childCount >= 2) {
                        val location = IntArray(2)
                        firstRow.getChildAt(1).getLocationOnScreen(location)
                        location[1] + firstRow.getChildAt(1).height - scrollView.scrollY
                    } else {
                        (screenHeight * 0.4).toInt() // Значение по умолчанию
                    }
                }
                else -> (screenHeight * 0.4).toInt() // Значение по умолчанию
            }

            // Гарантия минимального отступа сверху
            val minTopMargin = dpToPx(100)
            val finalTopMargin = if (topMargin < minTopMargin) minTopMargin else topMargin

            callback(finalTopMargin, screenHeight)
        }
    }

    // Создание контейнера с текстовой информацией
    private fun createTextContainer(filledStars: Int, genres: String, title: String): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM // Выравнивание по нижнему краю
            }
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))

            // Добавление рейтинга, жанра и названия
            addView(createStarRating(filledStars))
            addView(createGenresView(genres))
            addView(createTitleView(title))
        }
    }

    // Создание текстового поля с жанрами
    private fun createGenresView(genres: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(4)
            }
            text = genres
            setTextColor(Color.parseColor("#AAAAAA")) // Серый цвет текста
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setLineSpacing(0f, 1.0f)
        }
    }

    // Создание текстового поля с названием
    private fun createTitleView(title: String): TextView {
        return TextView(this).apply {
            text = title
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setLineSpacing(0f, 1.0f)
        }
    }

    // Создание рейтинга в виде звезд
    private fun createStarRating(filledStars: Int): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(4)
            }
            orientation = LinearLayout.HORIZONTAL

            // Создание 5 звезд (заполненных и пустых)
            for (i in 0 until 5) {
                addView(ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(10),
                        dpToPx(10)
                    )
                    setImageResource(
                        if (i < filledStars) R.drawable.ic_star_filled
                        else R.drawable.star_icon
                    )
                })
            }
        }
    }

    // Создание бейджа с возрастным рейтингом
    private fun createAgeRating(rating: String): TextView {
        return TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START // Выравнивание в левом верхнем углу
                setMargins(dpToPx(8), dpToPx(8), 0, 0)
            }
            text = rating
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTypeface(typeface, Typeface.BOLD)
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
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

    // Обработчик нажатия кнопки "назад"
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            // Особенности закрытия в ландшафтном режиме
            if (isLandscape && container.visibility == View.VISIBLE) {
                supportFragmentManager.beginTransaction()
                    .remove(supportFragmentManager.findFragmentById(container.id)!!)
                    .commit()
                container.visibility = View.GONE
                // Сброс параметров контейнера
                container.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                detailOpened = false
            }
            // Стандартное закрытие фрагмента
            else if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            }
            // Закрытие активности
            else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    // Сохранение состояния перед уничтожением активности
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SCORE_KEY", currentScore)
        outState.putInt("SCROLL_POSITION_Y", scrollView.scrollY)
        outState.putInt(KEY_SHARED_IMAGE_VIEW_ID, sharedImageViewId)
        outState.putBoolean(KEY_DETAIL_OPENED, detailOpened)
    }

    // Получение shared image view для анимации обратного перехода
    fun getSharedImageView(): View? {
        return if (sharedImageViewId != -1) findViewById(sharedImageViewId) else null
    }
}