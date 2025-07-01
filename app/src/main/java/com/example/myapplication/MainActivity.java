package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private int currentScore = 0;
    private ImageView mainImageView; // Ссылка на главное изображение для обработки клика

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Включаем edge-to-edge отображение

        // Восстановление состояния при повороте экрана
        if (savedInstanceState != null) {
            currentScore = savedInstanceState.getInt("SCORE_KEY", 0);
        }

        // Создаем корневую ScrollView программно
        ScrollView scrollView = createScrollView();
        setContentView(scrollView);

        // Обработка системных баров (статусная и навигационная панели)
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Устанавливаем обработчик клика для главного изображения
        if (mainImageView != null) {
            mainImageView.setOnClickListener(v -> {
                // Переход на SecondActivity с анимацией
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    // Создание корневой ScrollView
    private ScrollView createScrollView() {
        // Настройка основного ScrollView
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        scrollView.setBackgroundColor(Color.parseColor("#1B1E26")); // Темный фон
        scrollView.setFillViewport(true); // Заполнение всего видимого пространства

        // Создание основного вертикального контейнера
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Установка отступов вокруг контента
        int padding = dpToPx(16);
        mainLayout.setPadding(padding, padding, padding, padding);

        // Добавление элементов в основной контейнер
        mainLayout.addView(createTitleTextView()); // Заголовок "Movies List"
        mainLayout.addView(createFirstRow());     // Первый ряд карточек
        mainLayout.addView(createSecondRow());    // Второй ряд карточек
        mainLayout.addView(createThirdRow());      // Третий ряд карточек

        scrollView.addView(mainLayout);
        return scrollView;
    }

    // Создание заголовка "Movies List"
    private TextView createTitleTextView() {
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleTextView.setText(R.string.movies_list); // Текст из ресурсов
        titleTextView.setTextColor(Color.WHITE);     // Белый цвет текста
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21); // Размер 21sp
        titleTextView.setTypeface(null, Typeface.BOLD); // Жирный шрифт
        titleTextView.setGravity(Gravity.CENTER_VERTICAL); // Вертикальное центрирование

        // Настройка межстрочного интервала
        titleTextView.setLineSpacing(0, 1.0f);

        // Отступ снизу для разделения с карточками
        ((LinearLayout.LayoutParams) titleTextView.getLayoutParams()).bottomMargin = dpToPx(24);

        return titleTextView;
    }

    // Создание строки с карточками
    private LinearLayout createRowLayout() {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL); // Горизонтальное расположение

        // Отступ снизу между рядами
        ((LinearLayout.LayoutParams) row.getLayoutParams()).bottomMargin = dpToPx(16);

        // Распределение пространства между двумя карточками
        row.setWeightSum(2);

        return row;
    }

    // Создание карточки фильма
    private View createMovieCard(int imageRes, int filledStars, String genres, String title, String ageRating, boolean isMainImage) {
        // Внешний контейнер карточки
        LinearLayout cardLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, // Ширина будет рассчитана по весу
                dpToPx(200) // Фиксированная высота
        );
        params.weight = 1; // Равное распределение пространства
        params.setMarginEnd(dpToPx(8)); // Отступ между карточками
        cardLayout.setLayoutParams(params);
        cardLayout.setBackgroundColor(Color.parseColor("#2A2E38")); // Цвет фона карточки
        cardLayout.setClipToPadding(false); // Разрешаем отображение за пределами padding
        cardLayout.setOrientation(LinearLayout.VERTICAL); // Вертикальное расположение

        // Контейнер для изображения с затемнением
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        frameLayout.setBackgroundColor(Color.BLACK); // Фон на случай отсутствия изображения

        // Основное изображение фильма
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Обрезка по центру
        imageView.setImageResource(imageRes); // Установка ресурса изображения

        // Сохранение ссылки на главное изображение для обработки клика
        if (isMainImage) {
            imageView.setId(View.generateViewId());
            imageView.setClickable(true);
            imageView.setFocusable(true);
            this.mainImageView = imageView;
        }

        // Создание градиентного затемнения (снизу вверх)
        View gradientView = new View(this);
        gradientView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, // Ориентация снизу вверх
                new int[]{Color.BLACK, Color.TRANSPARENT} // От черного к прозрачному
        );
        gradientView.setBackground(gradient);

        // Контейнер для текстовой информации
        LinearLayout textContainer = createTextContainer(filledStars, genres, title);

        // Возрастной рейтинг
        TextView ageRatingView = createAgeRating(ageRating);

        // Сборка FrameLayout
        frameLayout.addView(imageView);       // Изображение фильма
        frameLayout.addView(gradientView);    // Градиентное затемнение
        frameLayout.addView(textContainer);   // Текстовая информация
        frameLayout.addView(ageRatingView);   // Возрастной рейтинг

        cardLayout.addView(frameLayout);
        return cardLayout;
    }

    // Создание контейнера для текстовой информации
    private LinearLayout createTextContainer(int filledStars, String genres, String title) {
        LinearLayout container = new LinearLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM; // Прикрепление к низу
        container.setLayoutParams(params);
        container.setOrientation(LinearLayout.VERTICAL); // Вертикальное расположение

        // Внутренние отступы для текста
        int padding = dpToPx(8);
        container.setPadding(padding, padding, padding, padding);

        // Добавление элементов
        container.addView(createStarRating(filledStars)); // Рейтинг звездами
        container.addView(createGenresView(genres));       // Жанры
        container.addView(createTitleView(title));         // Название фильма

        return container;
    }

    // Создание строки с жанрами
    private TextView createGenresView(String genres) {
        TextView genresView = new TextView(this);
        genresView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        genresView.setText(genres);
        genresView.setTextColor(Color.parseColor("#AAAAAA")); // Серый цвет текста
        genresView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Размер 12sp
        genresView.setLineSpacing(0, 1.0f); // Межстрочный интервал

        // Отступ снизу для разделения с названием
        ((LinearLayout.LayoutParams) genresView.getLayoutParams()).bottomMargin = dpToPx(4);

        return genresView;
    }

    // Создание названия фильма
    private TextView createTitleView(String title) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE); // Белый цвет текста
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Размер 16sp
        titleView.setLineSpacing(0, 1.0f); // Межстрочный интервал
        return titleView;
    }

    // Создание рейтинга звездами
    private LinearLayout createStarRating(int filledStars) {
        LinearLayout starLayout = new LinearLayout(this);
        starLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        starLayout.setOrientation(LinearLayout.HORIZONTAL); // Горизонтальное расположение звезд

        // Отступ снизу для разделения с жанрами
        ((LinearLayout.LayoutParams) starLayout.getLayoutParams()).bottomMargin = dpToPx(4);

        // Создание 5 звезд (заполненных и пустых)
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams(
                    dpToPx(10), // Ширина звезды
                    dpToPx(10)  // Высота звезды
            );
            star.setLayoutParams(starParams);

            // Выбор ресурса в зависимости от рейтинга
            star.setImageResource(i < filledStars ?
                    R.drawable.ic_star_filled : // Заполненная звезда
                    R.drawable.star_icon);      // Пустая звезда

            starLayout.addView(star);
        }
        return starLayout;
    }

    // Создание бейджа с возрастным рейтингом
    private TextView createAgeRating(String rating) {
        TextView ageView = new TextView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP | Gravity.START; // Позиция в левом верхнем углу
        params.setMargins(dpToPx(8), dpToPx(8), 0, 0); // Отступы от краев

        ageView.setLayoutParams(params);
        ageView.setText(rating);
        ageView.setTextColor(Color.WHITE); // Белый цвет текста
        ageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10); // Размер 10sp
        ageView.setTypeface(null, Typeface.BOLD); // Жирный шрифт

        // Внутренние отступы для бейджа
        ageView.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

        return ageView;
    }

    // Первый ряд карточек
    private LinearLayout createFirstRow() {
        LinearLayout row = createRowLayout();

        // Левая карточка: Star Trek
        row.addView(createMovieCard(
                R.drawable.star_treck,        // Изображение
                3,                            // 3 звезды
                getString(R.string.action_adventure_drama), // Жанры
                getString(R.string.star_trek_picard), // Название
                getString(R.string.rang_16),   // Возрастной рейтинг 16+
                false                         // Не главная карточка
        ));

        // Правая карточка: Mandalorian (главная)
        row.addView(createMovieCard(
                R.drawable.mandalorian,       // Изображение
                4,                            // 4 звезды
                getString(R.string.action_adventure_drama), // Жанры
                getString(R.string.mandalorian), // Название
                getString(R.string._12),       // Возрастной рейтинг 12+
                true                          // Главная карточка (для обработки клика)
        ));

        return row;
    }

    // Второй ряд карточек
    private LinearLayout createSecondRow() {
        LinearLayout row = createRowLayout();

        // Левая карточка: The Witcher
        row.addView(createMovieCard(
                R.drawable.witcher,           // Изображение
                5,                            // 5 звезд
                getString(R.string.action_adventure_drama), // Жанры
                getString(R.string.the_witcher), // Название
                getString(R.string.range_14),  // Возрастной рейтинг 14+
                false                         // Не главная карточка
        ));

        // Правая карточка: Joker
        row.addView(createMovieCard(
                R.drawable.joker,             // Изображение
                4,                            // 4 звезды
                getString(R.string.crime_drama_thriller), // Жанры
                getString(R.string.joker),     // Название
                getString(R.string.range_18),  // Возрастной рейтинг 18+
                false                         // Не главная карточка
        ));

        return row;
    }

    // Третий ряд карточек
    private LinearLayout createThirdRow() {
        LinearLayout row = createRowLayout();

        // Левая карточка: Tenet
        row.addView(createMovieCard(
                R.drawable.pataya,            // Изображение
                3,                            // 3 звезды
                getString(R.string.action_sci_fi), // Жанры
                getString(R.string.tenet),      // Название
                getString(R.string.range_18),   // Возрастной рейтинг 18+
                false                         // Не главная карточка
        ));

        // Правая карточка: Altered Carbon
        row.addView(createMovieCard(
                R.drawable.shestaya,          // Изображение
                5,                            // 5 звезд
                getString(R.string.action_sci_fi), // Жанры
                getString(R.string.altered_carbon), // Название
                getString(R.string._12),        // Возрастной рейтинг 12+
                false                         // Не главная карточка
        ));

        return row;
    }

    // Конвертация dp в пиксели
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    // Сохранение состояния активности
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SCORE_KEY", currentScore); // Сохранение текущего счета
    }
}