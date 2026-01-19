# MindPlay - Документація для розробників

---

## 📋 Розподіл завдань по особах

| Особа | Задачі |
|-------|--------|
| **Особа 1** | ✅ Каркас + Архітектура + Навігація + Спільні компоненти |
| **Особа 2** | Онбординг + DataStore + База даних прогресу |
| **Особа 3** | Звук + Локальні сповіщення |
| **Особа 4** | Гра "Prosta Arytmetyka" |
| **Особа 5** | Гра "Memory" |
| **Особа 6** | Гра "Puzzle 3×3" |
| **Особа 7** | Гра "Simon" + "Uwaga/Reakcja" + Анімації + Екран результатів |

---

### Виконано

#### Архітектура та структура проєкту
- Android проєкт: Kotlin + Jetpack Compose + Material 3
- Модулі (пакети):
  - `core/` - компоненти, навігація, DI, utils
  - `data/` - datastore, repository  
  - `domain/` - models, usecases
  - `ui/` - dashboard, games, onboarding, settings, splash, theme

#### Тема та кольори
- Файл: `ui/theme/Color.kt`
- Основні кольори:
  - Background: `#F2F7FD`
  - Primary (високий контраст): `#4A90E2`
  - Secondary (низький контраст): `#8DB9ED`
  - Success: `#6CC24A`
  - Error: `#F26060`
  - Simon: Green `#6CC24A`, Orange `#FF9B42`, Pink `#F56CA0`, Yellow `#FFD74B`

#### Типографіка
- Файл: `ui/theme/Type.kt`
- Шрифти: Rubik Bold, Rubik Medium, Inter Regular
- Три розміри тексту: SMALL (0.85x), MEDIUM (1x), LARGE (1.15x)

#### Навігація
- Файл: `core/navigation/NavGraph.kt`, `core/navigation/Screen.kt`
- Нижнє меню: Główna / Gry / Ustawienia
- Екрани: Splash → Welcome → Home/Games/Settings
- Переходи: fadeIn/fadeOut

#### Спільні компоненти (core/components/)

##### PrimaryButton / SecondaryButton
```kotlin
PrimaryButton(
    text = "ТЕКСТ",
    onClick = { },
    modifier = Modifier
)
```

##### MindPlayCard
```kotlin
MindPlayCard(
    size = 120.dp,
    backgroundColor = CardBlue,
    modifier = Modifier
) {
    // вміст
}
```

##### MindPlayProgressBar (лінійний)
```kotlin
MindPlayProgressBar(
    current = 3,
    total = 10,
    modifier = Modifier
)
```

##### CircularProgressBar
```kotlin
CircularProgressBar(
    current = 3,
    total = 5,
    size = 180.dp,
    strokeWidth = 18.dp,
    animationProgress = 1f  // 0f-1f для вхідної анімації
)
```

##### MindPlayToggle
```kotlin
MindPlayToggle(
    checked = true,
    onCheckedChange = { },
    modifier = Modifier
)
```

##### MindPlayRadioButton
```kotlin
MindPlayRadioButton(
    label = "Średni",
    selected = true,
    onClick = { }
)
```

##### MindPlayBottomNavigation
- Автоматично використовується в NavGraph
- Іконки: ic_home, ic_games, ic_settings

##### AnimatedCard
**Універсальний компонент для анімованих карток з ефектом натискання**

```kotlin
AnimatedCard(
    onClick = { /* клік */ },
    pressScale = 0.92f,
    animationDuration = 100
) { modifier ->
    Image(
        painter = painterResource(id = R.drawable.card_memory),
        contentDescription = "Memory",
        modifier = modifier.aspectRatio(1f)
    )
}
```

Параметри:
- `pressScale` - масштаб при натисканні (за замовчуванням 0.92f)
- `animationDuration` - тривалість анімації в мілісекундах (за замовчуванням 100)
- `onClick` - callback при натисканні
- `content` - вміст картки (отримує modifier з анімацією)

Використовується в: GamesScreen для карток ігор

---

## Дані для передачі (для інших осіб)

### 👤 ОСОБА 2 (Онбординг + DataStore + База даних прогресу)

#### Модель налаштувань (domain/models/AppSettings.kt)
```kotlin
data class AppSettings(
    val highContrast: Boolean = true,
    val textSize: TextSize = TextSize.MEDIUM,
    val stressMode: Boolean = false,
    val uiSoundEnabled: Boolean = true,
    val gameSoundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = false
)
```

#### Repository для розширення (data/repository/SettingsRepository.kt)
- Зараз: in-memory stub
- Потрібно зробити: DataStore інтеграція
- Методи: updateHighContrast(), updateTextSize(), updateStressMode() тощо

#### ProvideTheme (ui/theme/ProvideTheme.kt)
- Автоматично читає налаштування з SettingsRepository
- Застосовує MindPlayTheme з highContrast і textSize

### 👤 ОСОБА 3 (Звук + Сповіщення) - ✅ РЕАЛІЗОВАНО

#### Звуки (core/sound/)

##### SoundManager.kt
**Менеджер звуків з підтримкою SoundPool та MediaPlayer**

```kotlin
// Ін'єкція через Koin
val soundManager: SoundManager by inject()

// Короткі звуки (SoundPool)
soundManager.playTap()      // звук натискання (UI)
soundManager.playCorrect()  // звук правильної відповіді (Game)
soundManager.playWrong()    // звук неправильної відповіді (Game)

// Тони Simon (MediaPlayer)
soundManager.playSimonTone(SoundManager.SimonTone.GREEN)
soundManager.playSimonTone(SoundManager.SimonTone.ORANGE)
soundManager.playSimonTone(SoundManager.SimonTone.PINK)
soundManager.playSimonTone(SoundManager.SimonTone.YELLOW)
soundManager.stopSimonTone() // зупинити поточний тон
```

Особливості:
- Автоматично читає налаштування з SettingsRepository
- `uiSoundEnabled` - контролює playTap()
- `gameSoundEnabled` - контролює playCorrect(), playWrong(), playSimonTone()
- Безпечно обробляє відсутні звукові файли

##### Звукові файли (res/raw/)
Потрібно додати наступні файли:
- `tap.mp3` - звук натискання
- `correct.mp3` - правильна відповідь
- `wrong.mp3` - неправильна відповідь
- `simon_green.mp3`, `simon_orange.mp3`, `simon_pink.mp3`, `simon_yellow.mp3` - тони Simon

#### Локальні сповіщення (core/notifications/)

##### NotificationScheduler.kt
**Планування щоденних нагадувань**

```kotlin
val notificationScheduler: NotificationScheduler by inject()

// Створити канал (викликається в MindPlayApp.onCreate())
notificationScheduler.createNotificationChannel()

// Планувати щоденне нагадування о 10:00
notificationScheduler.scheduleDailyReminder()

// Скасувати нагадування
notificationScheduler.cancelDailyReminder()

// Показати сповіщення вручну
notificationScheduler.showNotification(
    title = "Час для вправ!",
    message = "Твій мозок чекає на тренування"
)

// Перевірити дозвіл на точні будильники (Android 12+)
notificationScheduler.canScheduleExactAlarms()
```

##### ReminderReceiver.kt
BroadcastReceiver для отримання alarm і показу сповіщення

##### BootReceiver.kt
Відновлює заплановані нагадування після перезавантаження пристрою

#### Налаштування в UI (SettingsScreen.kt)

Додано нові секції:
- **Dźwięki** (Звуки)
  - Dźwięki interfejsu (on/off) - звуки UI
  - Dźwięki gier (on/off) - звуки ігор
- **Przypomnienia** (Нагадування)
  - Codzienne powiadomienia o 10:00 (on/off)

#### AndroidManifest.xml - додані permissions
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

#### Koin DI
```kotlin
// В AppModule.kt
single { SoundManager(androidContext(), get()) }
single { NotificationScheduler(androidContext()) }
```

#### Використання в іграх
```kotlin
@Composable
fun GameScreen() {
    val context = LocalContext.current
    val soundManager = remember { 
        (context.applicationContext as MindPlayApp).soundManager 
    }
    
    // Або через Koin
    val soundManager: SoundManager = get()
    
    // При правильній відповіді
    soundManager.playCorrect()
    
    // При неправильній відповіді
    soundManager.playWrong()
    
    // У грі Simon
    soundManager.playSimonTone(SoundManager.SimonTone.GREEN)
}
```

### 👤 ОСОБИ 4-7 (Ігри)

#### Навігація до ігор
- Файл: `core/navigation/Screen.kt`
- Routes визначені:
  - `Screen.GameArytmetyka.route` = "game/arytmetyka"
  - `Screen.GameMemory.route` = "game/memory"
  - `Screen.GamePuzzle.route` = "game/puzzle"
  - `Screen.GameSimon.route` = "game/simon"
  - `Screen.GameUwaga.route` = "game/uwaga"
  - `Screen.GamePary.route` = "game/pary"

#### Як додати новий екран гри
1. Створити файл в `ui/games/[назва]/[Назва]Screen.kt`
2. Додати composable в `NavGraph.kt`:
```kotlin
composable(Screen.GameMemory.route) {
    MemoryScreen(
        onBack = { navController.popBackStack() },
        onFinish = { score -> /* зберегти результат */ }
    )
}
```

#### Доступні компоненти для ігор
- `MindPlayCard` - карти для Memory
- `MindPlayProgressBar` - панель прогресу (10 завдань)
- `CircularProgressBar` - круговий прогрес
- `PrimaryButton` / `SecondaryButton` - кнопки
- Кольори: `SuccessGreen`, `ErrorRed`, `SimonGreen/Orange/Pink/Yellow`

#### Екран результатів (ОСОБА 7 - створити)
- Локація: `core/components/ResultModal.kt`
- Параметри: score, totalTasks, onReplay, onBack

---

## Анімації (ОСОБА 7 - реалізувати)

### ✅ Реалізовані готові компоненти анімацій

#### NavigationAnimations (core/navigation/NavigationAnimations.kt)
**Об'єкт з готовими анімаціями переходів між екранами**

Доступні анімації:
```kotlin
// Fade in/out (за замовчуванням використовується в навігації)
NavigationAnimations.fadeInTransition(durationMillis = 200)
NavigationAnimations.fadeOutTransition(durationMillis = 200)

// Slide з права наліво (для прямих переходів)
NavigationAnimations.slideInFromRightTransition(durationMillis = 300)
NavigationAnimations.slideOutToLeftTransition(durationMillis = 300)

// Slide зліва направо (для повернення назад)
NavigationAnimations.slideInFromLeftTransition(durationMillis = 300)
NavigationAnimations.slideOutToRightTransition(durationMillis = 300)
```

Використання в NavHost:
```kotlin
NavHost(
    enterTransition = { NavigationAnimations.fadeInTransition() },
    exitTransition = { NavigationAnimations.fadeOutTransition() }
) {
    composable(
        route = Screen.GameDetails.route,
        enterTransition = { NavigationAnimations.slideInFromRightTransition() },
        exitTransition = { NavigationAnimations.slideOutToLeftTransition() },
        popEnterTransition = { NavigationAnimations.slideInFromLeftTransition() },
        popExitTransition = { NavigationAnimations.slideOutToRightTransition() }
    ) {
        GameDetailsScreen()
    }
}
```

#### AnimatedCard (core/components/AnimatedCard.kt)
**Компонент для карток з анімацією натискання**
- Див. розділ "Компоненти" вище для деталей
- Використовується в GamesScreen для карток ігор

### Реалізація додаткових анімацій (для використання в іграх)

#### Анімація входу елемента (fade + scale)
```kotlin
var visible by remember { mutableStateOf(false) }
val alpha by animateFloatAsState(
    targetValue = if (visible) 1f else 0f,
    animationSpec = tween(durationMillis = 300)
)
val scale by animateFloatAsState(
    targetValue = if (visible) 1f else 0.8f,
    animationSpec = tween(durationMillis = 300)
)

LaunchedEffect(Unit) { visible = true }

Box(modifier = Modifier.alpha(alpha).scale(scale)) {
    // вміст
}
```

#### Анімація натискання (scale down)
```kotlin
var isPressed by remember { mutableStateOf(false) }
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1f,
    animationSpec = tween(durationMillis = 100)
)

Box(
    modifier = Modifier
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
)
```

#### Анімація кольору (підсвічування)
```kotlin
val backgroundColor by animateColorAsState(
    targetValue = when (state) {
        State.CORRECT -> SuccessGreen
        State.WRONG -> ErrorRed
        else -> CardBlue
    },
    animationSpec = tween(durationMillis = 300)
)
```

#### Анімація панелі прогресу
- Вже реалізована в `MindPlayProgressBar`
- Автоматично анімується при зміні `current`

#### Переходи між екранами
- Використовуй `NavigationAnimations` (див. вище)
- Fade анімації вже налаштовані в NavGraph
- Для складніших переходів використовуй slide анімації

---

## Структура файлів

```
com.mind.play/
├── MainActivity.kt
├── MindPlayApp.kt
├── core/
│   ├── components/
│   │   ├── BottomNavigation.kt
│   │   ├── Buttons.kt
│   │   ├── Cards.kt
│   │   ├── CircularProgress.kt
│   │   ├── ProgressBar.kt
│   │   ├── RadioButtons.kt
│   │   └── Toggle.kt
│   ├── di/
│   │   └── AppModule.kt
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   └── Screen.kt
│   ├── notifications/
│   │   ├── NotificationScheduler.kt
│   │   ├── ReminderReceiver.kt
│   │   └── BootReceiver.kt
│   └── sound/
│       └── SoundManager.kt
├── data/
│   ├── datastore/
│   └── repository/
│       └── SettingsRepository.kt
├── domain/
│   ├── models/
│   │   └── AppSettings.kt
│   └── usecases/
└── ui/
    ├── dashboard/
    │   └── HomeScreen.kt
    ├── games/
    │   └── GamesScreen.kt
    ├── onboarding/
    │   └── WelcomeScreen.kt
    ├── settings/
    │   ├── SettingsScreen.kt
    │   └── SettingsViewModel.kt
    ├── splash/
    │   └── SplashScreen.kt
    └── theme/
        ├── Color.kt
        ├── ProvideTheme.kt
        ├── Theme.kt
        └── Type.kt
```

---

## Ресурси (res/)

### Шрифти (res/font/)
- rubik_bold.ttf
- rubik_medium.ttf
- inter_regular.ttf

### Іконки (res/drawable/)
- ic_home.xml
- ic_games.xml
- ic_settings.xml
- card_aritmetic.png
- card_memory.png
- card_pairs.png
- card_puzzle.png
- card_reaction.png
- card_simon.png
- ic_mindplay.xml (лого)

---

## Koin DI

### Як додати новий ViewModel
```kotlin
// В core/di/AppModule.kt
val appModule = module {
    viewModel { MyViewModel(get()) }
}

// Використання в Composable
@Composable
fun MyScreen(
    viewModel: MyViewModel = koinViewModel()
) {
    // ...
}
```

### Як додати новий Repository
```kotlin
val appModule = module {
    single { MyRepository(get()) }
}
```

---

## Технічні вимоги

- Min SDK: 26 (Android 8.0)
- Target SDK: 35
- Kotlin: 2.0.0
- Compose BOM: 2024.04.01
- Koin: 4.0.0

---

## 🗄️ База даних прогресу (ОСОБА 2 - РЕАЛІЗУВАТИ)

> **Відповідальний: Особа 2 (Онбординг + DataStore)**
> 
> Це частина роботи Особи 2, оскільки вона вже працює з DataStore і збереженням даних.

### Загальний опис
Зараз в `HomeScreen.kt` використовуються mock-дані для прогресу. Потрібно створити реальну базу даних для збереження:
- Щоденний прогрес (скільки ігор зіграно)
- Тижнева статистика (хвилини гри по днях)
- Історія результатів ігор

### Моделі даних (domain/models/)

#### DailyProgress.kt
```kotlin
data class DailyProgress(
    val date: LocalDate,
    val gamesPlayed: Int,        // Кількість зіграних ігор сьогодні
    val totalGames: Int = 5,     // Ціль: 5 ігор на день
    val minutesPlayed: Int       // Хвилин гри за день
)
```

#### GameResult.kt
```kotlin
data class GameResult(
    val id: Long = 0,
    val gameType: String,        // "arytmetyka", "memory", "puzzle", etc.
    val score: Int,              // Результат гри
    val totalTasks: Int,         // Загальна кількість завдань
    val duration: Int,           // Тривалість в секундах
    val timestamp: Long,         // Час завершення гри
    val stressMode: Boolean      // Чи був увімкнений режим зі стресом
)
```

#### WeeklyStats.kt
```kotlin
data class WeeklyStats(
    val weekStartDate: LocalDate,
    val dailyMinutes: List<Int>, // 7 елементів - хвилини по днях тижня
    val dailyTargetMet: List<Boolean> // 7 елементів - чи виконана ціль
)
```

### Repository (data/repository/)

#### ProgressRepository.kt
```kotlin
interface ProgressRepository {
    // Щоденний прогрес
    fun getDailyProgress(date: LocalDate): Flow<DailyProgress>
    fun getTodayProgress(): Flow<DailyProgress>
    suspend fun incrementGamesPlayed()
    suspend fun addMinutesPlayed(minutes: Int)
    
    // Тижнева статистика
    fun getWeeklyStats(weekOffset: Int = 0): Flow<WeeklyStats>
    fun getCurrentWeekStats(): Flow<WeeklyStats>
    
    // Результати ігор
    suspend fun saveGameResult(result: GameResult)
    fun getGameResults(gameType: String): Flow<List<GameResult>>
    fun getAllGameResults(): Flow<List<GameResult>>
}
```

### Реалізація з Room Database

#### AppDatabase.kt (data/database/)
```kotlin
@Database(
    entities = [DailyProgressEntity::class, GameResultEntity::class],
    version = 1
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun gameResultDao(): GameResultDao
}
```

#### ProgressDao.kt
```kotlin
@Dao
interface ProgressDao {
    @Query("SELECT * FROM daily_progress WHERE date = :date")
    fun getProgressByDate(date: String): Flow<DailyProgressEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: DailyProgressEntity)
    
    @Query("SELECT * FROM daily_progress WHERE date BETWEEN :startDate AND :endDate")
    fun getProgressForWeek(startDate: String, endDate: String): Flow<List<DailyProgressEntity>>
}
```

### Koin DI налаштування

```kotlin
// В core/di/AppModule.kt додати:
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "mindplay_database"
        ).build()
    }
    
    single { get<AppDatabase>().progressDao() }
    single { get<AppDatabase>().gameResultDao() }
    single<ProgressRepository> { ProgressRepositoryImpl(get(), get()) }
}

// Додати в allModules:
val allModules = listOf(appModule, databaseModule)
```

### ViewModel для Dashboard

#### DashboardViewModel.kt (ui/dashboard/)
```kotlin
class DashboardViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    val todayProgress: StateFlow<DailyProgress> = progressRepository
        .getTodayProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyProgress.default())
    
    val currentWeekStats: StateFlow<WeeklyStats> = progressRepository
        .getCurrentWeekStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyStats.empty())
    
    private val _weekOffset = MutableStateFlow(0)
    val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()
    
    val displayedWeekStats: StateFlow<WeeklyStats> = _weekOffset
        .flatMapLatest { offset -> progressRepository.getWeeklyStats(offset) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyStats.empty())
    
    fun nextWeek() { _weekOffset.value++ }
    fun previousWeek() { _weekOffset.value-- }
}
```

### Як використовувати в HomeScreen

```kotlin
@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val todayProgress by viewModel.todayProgress.collectAsState()
    val weekStats by viewModel.displayedWeekStats.collectAsState()
    val weekOffset by viewModel.weekOffset.collectAsState()
    
    // Використання замість mock-даних:
    CircularProgressBar(
        current = todayProgress.gamesPlayed,
        total = todayProgress.totalGames,
        // ...
    )
    
    // Тижневий графік з реальними даними:
    WeeklyChart(
        stats = weekStats,
        onSwipeLeft = { viewModel.nextWeek() },
        onSwipeRight = { viewModel.previousWeek() }
    )
}
```

### Як записувати прогрес з ігор

Кожна гра після завершення повинна викликати:
```kotlin
// В ViewModel гри:
fun onGameFinished(score: Int, totalTasks: Int, durationSeconds: Int) {
    viewModelScope.launch {
        progressRepository.saveGameResult(
            GameResult(
                gameType = "memory", // або інша гра
                score = score,
                totalTasks = totalTasks,
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                stressMode = settingsRepository.settings.value.stressMode
            )
        )
        progressRepository.incrementGamesPlayed()
        progressRepository.addMinutesPlayed(durationSeconds / 60)
    }
}
```

### Gradle залежності (додати в app/build.gradle.kts)

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")
```

### Файли для створення

1. `domain/models/DailyProgress.kt`
2. `domain/models/GameResult.kt`
3. `domain/models/WeeklyStats.kt`
4. `data/database/AppDatabase.kt`
5. `data/database/ProgressDao.kt`
6. `data/database/GameResultDao.kt`
7. `data/database/entities/DailyProgressEntity.kt`
8. `data/database/entities/GameResultEntity.kt`
9. `data/database/DateConverters.kt`
10. `data/repository/ProgressRepository.kt`
11. `data/repository/ProgressRepositoryImpl.kt`
12. `ui/dashboard/DashboardViewModel.kt`

---