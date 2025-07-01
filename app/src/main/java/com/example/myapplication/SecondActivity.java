package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Создаем корневую ScrollView
            ScrollView scrollView = new ScrollView(this);
            scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            scrollView.setBackgroundColor(Color.parseColor("#1B1E26"));
            scrollView.setFillViewport(true);

            // Основной контейнер
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            // Верхнее изображение
            ImageView headerImage = createHeaderImage();
            relativeLayout.addView(headerImage);

            // Градиентный оверлей
            View gradientOverlay = createGradientOverlay(headerImage.getId());
            relativeLayout.addView(gradientOverlay);

            // Возрастной рейтинг
            TextView ageRating = createAgeRating();
            relativeLayout.addView(ageRating);

            // Заголовок
            TextView titleText = createTitleText(ageRating.getId());
            relativeLayout.addView(titleText);

            // Контейнер рейтинга
            LinearLayout ratingContainer = createRatingContainer(titleText.getId());
            relativeLayout.addView(ratingContainer);

            // Добавление звезд рейтинга
            addRatingStars(ratingContainer);

            // Жанры
            TextView genresText = createGenresText(ratingContainer.getId());
            relativeLayout.addView(genresText);

            // Storyline заголовок
            TextView storylineLabel = createStorylineLabel(genresText.getId());
            relativeLayout.addView(storylineLabel);

            // Описание сюжета
            TextView storylineText = createStorylineText(storylineLabel.getId());
            relativeLayout.addView(storylineText);

            // Cast and Crew заголовок
            TextView castLabel = createCastLabel(storylineText.getId());
            relativeLayout.addView(castLabel);

            // Горизонтальный скролл актеров
            HorizontalScrollView scrollViewActors = createActorsScrollView(castLabel.getId());
            relativeLayout.addView(scrollViewActors);

            // Кнопка назад
            ImageView backButton = createBackButton();
            relativeLayout.addView(backButton);

            scrollView.addView(relativeLayout);
            setContentView(scrollView);

        } catch (Exception e) {
            Log.e(TAG, "Error creating activity: " + e.getMessage(), e);
            finish();
        }
    }

    private ImageView createHeaderImage() {
        ImageView headerImage = new ImageView(this);
        headerImage.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        headerImage.setLayoutParams(params);
        headerImage.setAdjustViewBounds(true);
        headerImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        try {
            headerImage.setImageResource(R.drawable.img);
        } catch (Exception e) {
            Log.e(TAG, "Error setting header image", e);
        }

        return headerImage;
    }

    private View createGradientOverlay(int headerImageId) {
        View gradientOverlay = new View(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                dpToPx(232)
        );
        params.addRule(RelativeLayout.ALIGN_BOTTOM, headerImageId);
        gradientOverlay.setLayoutParams(params);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.TRANSPARENT, Color.parseColor("#1B1E26")}
        );
        gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientOverlay.setBackground(gradient);

        return gradientOverlay;
    }

    private TextView createAgeRating() {
        TextView ageRating = new TextView(this);
        ageRating.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // Ширина по содержимому
                RelativeLayout.LayoutParams.WRAP_CONTENT  // Высота по содержимому
        );
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = dpToPx(320);
        ageRating.setLayoutParams(params);

        ageRating.setAlpha(0.5f);
        ageRating.setGravity(Gravity.CENTER);
        ageRating.setText(R.string.range);
        ageRating.setTextColor(Color.WHITE);
        ageRating.setTextSize(16);

        // Убираем вертикальные отступы и устанавливаем компактные горизонтальные
        ageRating.setPadding(dpToPx(4), 0, dpToPx(4), 0);

        // Критически важная настройка для правильного отображения символов
        ageRating.setIncludeFontPadding(false);

        return ageRating;
    }

    private TextView createTitleText(int belowId) {
        TextView titleText = new TextView(this);
        titleText.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                dpToPx(306),
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.BELOW, belowId);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = dpToPx(8);

        titleText.setLayoutParams(params);
        titleText.setGravity(Gravity.CENTER);
        titleText.setText(R.string.the_mandalorian);
        titleText.setTextColor(Color.parseColor("#ECECEC"));
        titleText.setTextSize(25);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setShadowLayer(6, 0, 6, Color.BLACK);

        return titleText;
    }

    private LinearLayout createRatingContainer(int belowId) {
        LinearLayout ratingContainer = new LinearLayout(this);
        ratingContainer.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.BELOW, belowId);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = dpToPx(8);
        ratingContainer.setLayoutParams(params);
        ratingContainer.setOrientation(LinearLayout.HORIZONTAL);
        ratingContainer.setGravity(Gravity.CENTER);

        return ratingContainer;
    }

    private void addRatingStars(LinearLayout container) {
        for (int i = 0; i < 4; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
            params.setMarginEnd(dpToPx(2));
            star.setLayoutParams(params);

            try {
                star.setImageResource(R.drawable.ic_star_filled);
                star.setColorFilter(Color.parseColor("#FFB800"));
                container.addView(star);
            } catch (Exception e) {
                Log.e(TAG, "Error adding filled star", e);
            }
        }

        ImageView grayStar = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
        grayStar.setLayoutParams(params);

        try {
            grayStar.setImageResource(R.drawable.star_icon);
            grayStar.setColorFilter(Color.parseColor("#6D6D80"));
            container.addView(grayStar);
        } catch (Exception e) {
            Log.e(TAG, "Error adding gray star", e);
        }
    }

    private TextView createGenresText(int belowId) {
        TextView genresText = new TextView(this);
        genresText.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Ширина на весь экран
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.BELOW, belowId);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = dpToPx(8);
        params.setMarginStart(dpToPx(16)); // Отступ слева
        params.setMarginEnd(dpToPx(16));   // Отступ справа
        genresText.setLayoutParams(params);

        genresText.setGravity(Gravity.CENTER); // Выравнивание по центру
        genresText.setText(R.string.action_adventure_fantasy);
        genresText.setTextColor(Color.WHITE);
        genresText.setTextSize(14);

        // Разрешаем перенос текста
        genresText.setSingleLine(false);
        genresText.setMaxLines(2);
        genresText.setEllipsize(TextUtils.TruncateAt.END);

        return genresText;
    }

    private TextView createStorylineLabel(int belowId) {
        TextView label = new TextView(this);
        label.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                dpToPx(343),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.addRule(RelativeLayout.BELOW, belowId);
        params.topMargin = dpToPx(36);

        // Определяем ориентацию
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Для ландшафта: прижимаем к началу (слева для LTR, справа для RTL)
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.setMarginStart(dpToPx(16)); // Добавляем отступ от края
        } else {
            // Для портрета: центрируем по горизонтали
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        label.setLayoutParams(params);

        // Всегда выравниваем текст внутри TextView по началу (слева)
        label.setGravity(Gravity.START);
        label.setText(R.string.storyline);
        label.setTextColor(Color.parseColor("#ECECEC"));
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setShadowLayer(6, 0, 6, Color.BLACK);

        return label;
    }

    @SuppressLint("SetTextI18n")
    private TextView createStorylineText(int belowId) {
        TextView text = new TextView(this);
        text.setId(View.generateViewId());

        // Определяем ориентацию
        int orientation = getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Создаем параметры
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                isLandscape ? ViewGroup.LayoutParams.MATCH_PARENT : dpToPx(343),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.addRule(RelativeLayout.BELOW, belowId);
        params.topMargin = dpToPx(8);

        // Настройка для разных ориентаций
        if (isLandscape) {
            // Ландшафт: прижимаем к началу с отступами
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.setMarginStart(dpToPx(16));
            params.setMarginEnd(dpToPx(16));
        } else {
            // Портрет: центрируем по горизонтали
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        text.setLayoutParams(params);
        text.setAlpha(0.75f);

        try {
            text.setText(getString(R.string.the_travels));
        } catch (Exception e) {
            text.setText("Description not available");
            Log.e(TAG, "Error loading storyline text", e);
        }

        text.setTextColor(Color.WHITE);
        text.setTextSize(16);
        text.setLineSpacing(dpToPx(4), 1.0f);

        // Всегда выравниваем текст по левому краю
        text.setGravity(Gravity.START);

        return text;
    }

    private TextView createCastLabel(int belowId) {
        TextView label = new TextView(this);
        label.setId(View.generateViewId());

        // Определяем ориентацию
        int orientation = getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Создаем параметры
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                isLandscape ? ViewGroup.LayoutParams.MATCH_PARENT : dpToPx(343),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Общие правила для всех ориентаций
        params.addRule(RelativeLayout.BELOW, belowId);
        params.topMargin = dpToPx(36);

        // Настройка для разных ориентаций
        if (isLandscape) {
            // Ландшафт: прижимаем к началу с отступами
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.setMarginStart(dpToPx(16));
            params.setMarginEnd(dpToPx(16));
        } else {
            // Портрет: центрируем по горизонтали
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        label.setLayoutParams(params);

        // Текст всегда прижат к началу (слева для LTR, справа для RTL)
        label.setGravity(Gravity.START);
        label.setText(R.string.cast_and_crew);
        label.setTextColor(Color.parseColor("#ECECEC"));
        label.setTextSize(18);
        label.setTypeface(null, Typeface.BOLD);
        label.setShadowLayer(6, 0, 6, Color.BLACK);

        return label;
    }

    private HorizontalScrollView createActorsScrollView(int belowId) {
        HorizontalScrollView scrollView = new HorizontalScrollView(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(180)
        );
        params.addRule(RelativeLayout.BELOW, belowId);
        params.topMargin = dpToPx(8);
        params.bottomMargin = dpToPx(16);
        scrollView.setLayoutParams(params);
        scrollView.setHorizontalScrollBarEnabled(false);

        LinearLayout container = new LinearLayout(this);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(dpToPx(16), 0, dpToPx(16), 0);

        // Массив актеров
        String[] actors = {"RPedro Pascal", "Carl Weathers", "Gina Carano", "Misty Rosas", "Rio Hackford", "Chris Bartlett"};
        int[] photos = {
                R.drawable.pedro_pascal,
                R.drawable.carl_weathers,
                R.drawable.gina_carano,
                R.drawable.misty_rosas,
                R.drawable.rio_hackford,
                R.drawable.chris_bartlett
        };

        for (int i = 0; i < actors.length; i++) {
            try {
                container.addView(createActorCard(actors[i], photos[i]));
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Memory error creating actor card: " + actors[i], e);
            } catch (Exception e) {
                Log.e(TAG, "Error creating actor card: " + actors[i], e);
            }
        }

        scrollView.addView(container);
        return scrollView;
    }

    private LinearLayout createActorCard(String name, int photoRes) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(129),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(dpToPx(8));
        layout.setLayoutParams(params);

        ImageView photo = new ImageView(this);
        LinearLayout.LayoutParams photoParams = new LinearLayout.LayoutParams(
                dpToPx(129),
                dpToPx(152)
        );
        photo.setLayoutParams(photoParams);
        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);

        try {
            photo.setBackgroundResource(R.drawable.rounded_corners);
            photo.setImageResource(photoRes);
        } catch (Exception e) {
            Log.e(TAG, "Error setting actor photo: " + name, e);
        }

        layout.addView(photo);

        TextView nameText = new TextView(this);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        nameText.setPadding(0, dpToPx(8), 0, 0);
        nameText.setGravity(Gravity.START);
        nameText.setText(name);
        nameText.setTextColor(Color.WHITE);
        nameText.setAlpha(0.75f);
        nameText.setTextSize(14);
        layout.addView(nameText);

        return layout;
    }

    private ImageView createBackButton() {
        ImageView button = new ImageView(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                dpToPx(48),
                dpToPx(48)
        );
        params.setMarginStart(dpToPx(21));
        params.topMargin = dpToPx(32);
        button.setLayoutParams(params);

        try {
            button.setImageResource(R.drawable.ic_back);
        } catch (Exception e) {
            Log.e(TAG, "Error setting back button icon", e);
        }

        button.setColorFilter(Color.WHITE);
        button.setClickable(true);
        button.setFocusable(true);

        try {
            button.setContentDescription(getString(R.string.back_button_desc));
        } catch (Exception e) {
            button.setContentDescription("Back button");
            Log.e(TAG, "Error setting back button description", e);
        }

        button.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        return button;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}