# PROMPT: Add/Edit Activity screen for DayKeeper

Implement a full-screen Add/Edit screen with Jetpack Compose + Material3, plus its
helper functions. The app is a weekly planner (Kotlin/Compose/Room), root package
`com.jiyeon.daykeeper`. Use the existing `DayKeeperColors` and `DayKeeperType`; do NOT
hardcode hex/sp inline.

Color conventions (follow existing components like ScheduleItemRow/DayTabRow):
- Theme-aware tokens come from `MaterialTheme.colorScheme.*`
  (primary=Primary #378ADD, onSurface=TextPrimary, onSurfaceVariant=TextSecondary,
  surfaceVariant=SurfaceVariant, outline=BorderSecondary, outlineVariant=Divider,
  onPrimary=OnPrimary).
- Tokens with no colorScheme slot (TextTertiary, category colors + soft fills) come
  straight from `DayKeeperColors`.
- Shared radius: `RadiusMedium = 8.dp`, `FieldShape = RoundedCornerShape(RadiusMedium)`.

═══════════════════════════════════════════════════════════════════════
## 1. ui/timeline/components/CategoryColors.kt — category → color mapping
═══════════════════════════════════════════════════════════════════════
Place next to the existing `barColor()`. Each function is a `when` over all four
REST/STUDY/SPORT/WORK branches:

- `fun ActivityCategory.barColor(): Color`        → CategoryRest/Study/Sport/Work
- `fun ActivityCategory.softFillColor(): Color`   → Category{X}Soft (selected chip fill)
- `fun ActivityCategory.onSoftFillColor(): Color` → OnCategory{X}Soft (text on soft fill)

═══════════════════════════════════════════════════════════════════════
## 2. ui/addedit/AddEditScreen.kt — stateless UI (renders the form only)
═══════════════════════════════════════════════════════════════════════
Full screen, shared by both create and edit; only the title and pre-filled values
differ. Hoist all state out. NO ViewModel/persistence/navigation.

```kotlin
@Composable
fun AddEditScreen(
    isEditing: Boolean,
    name: String, onNameChange: (String) -> Unit,
    selectedCategory: ActivityCategory, onCategoryChange: (ActivityCategory) -> Unit,
    startTime: String, endTime: String,
    onPickStart: () -> Unit, onPickEnd: () -> Unit,
    repeatDays: Set<DayOfWeek>, onToggleDay: (DayOfWeek) -> Unit,
    reminderEnabled: Boolean, onReminderChange: (Boolean) -> Unit,
    note: String, onNoteChange: (String) -> Unit,
    onClose: () -> Unit, onSave: () -> Unit,
    modifier: Modifier = Modifier,
)
```

Layout: a vertical Column = TopBar + a vertically scrollable body (verticalScroll,
padding 16dp, spacedBy 18dp between fields).

TOP BAR (private TopBar): Row SpaceBetween, height 56dp, horizontal padding 16dp,
a 0.5dp bottom divider (outlineVariant).
- Left: Icons.Default.Close 24dp, tint onSurfaceVariant, onClick = onClose.
- Center: title bodyLarge.medium / onSurface — "Sửa hoạt động" when isEditing,
  otherwise "Hoạt động mới".
- Right: "Lưu" text, bodyLarge.medium, color primary, onClick = onSave.

Private helper `FieldLabel(text, bottomMargin: Dp)`: Text bodySmall / onSurfaceVariant,
padding bottom = bottomMargin.

Fields (each field is its own child Column; spacing between fields handled by the
18dp spacedBy):
1. NameField: label "Tên hoạt động" (margin 6dp) + OutlinedTextField fillMaxWidth,
   singleLine, textStyle body, shape FieldShape.
2. CategoryPicker: label "Loại" (margin 8dp) + a 2×2 grid (gap 8dp) from
   `ActivityCategory.entries.chunked(2)`. Each chip = `RowScope.CategoryChip`:
   Row weight(1f), clip FieldShape, vertical padding 8dp, with an 8dp color dot
   (barColor) + caption label. Unselected: surfaceVariant background, onSurfaceVariant
   text, no border. Selected: softFillColor() background, onSoftFillColor() text,
   2dp barColor() border. Click → onCategoryChange(category).
3. TimeRow: Row gap 12dp with two `RowScope.TimeField` weight(1f):
   "Bắt đầu"/startTime, "Kết thúc"/endTime. Each field: bodySmall label (margin 6dp)
   + a tappable box height 36dp, 0.5dp outline border, FieldShape, horizontal padding
   12dp, Row SpaceBetween: time text (body/onSurface) + Icons.Outlined.Schedule 16dp
   tint TextTertiary. Click = onPickStart/onPickEnd (stub only; do NOT open a dialog
   in this file).
4. RepeatDays: label "Lặp lại" (margin 8dp) + Row gap 6dp, 7 circular
   `RowScope.DayChip` chips weight(1f) aspectRatio(1f) CircleShape, labels T2..CN
   (MON..SUN). Selected: primary background, onPrimary text. Unselected: surfaceVariant
   background, onSurfaceVariant text. Click = onToggleDay(day). Use a list
   `repeatDayLabels: List<Pair<DayOfWeek,String>>` ordered MON→SUN.
5. ReminderToggle: Row SpaceBetween: text "Bật nhắc nhở" (body/onSurface) + Switch
   (checkedTrackColor=primary, checkedThumbColor=onPrimary), value reminderEnabled.
6. NoteField: label "Ghi chú" (margin 6dp) + OutlinedTextField fillMaxWidth,
   heightIn(min=60dp), multi-line, placeholder "Công viên gần nhà...", shape FieldShape.

@Preview: sample data — isEditing=false, name="Tập gym", category=SPORT,
start="18:00", end="19:00", repeatDays={MON,WED,FRI}, reminder=true, note="".

═══════════════════════════════════════════════════════════════════════
## 3. ui/component/TimePickerDialog.kt — time picker dialog (stateless)
═══════════════════════════════════════════════════════════════════════
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int, initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,  // minute already snapped to /5
    onDismiss: () -> Unit,
)
```

- `rememberTimePickerState(initialHour, initialMinute, is24Hour = true)`.
- Local `showInput` flag (default true): true→TimeInput, false→TimePicker.
- AlertDialog:
  - confirmButton: TextButton "Xong" → onConfirm(state.hour, snapTo5(state.minute)),
    label bodyLarge.medium color Primary.
  - dismissButton: TextButton "Huỷ" → onDismiss(), label bodyLarge.medium color
    TextSecondary.
  - text: Column (fillMaxWidth, center-aligned, spacedBy 8dp) holding TimeInput or
    TimePicker per showInput + a TextButton that flips the mode: "Dùng đồng hồ" (when
    in input mode) / "Gõ giờ" (when in clock mode), bodySmall.medium color Primary.
- Picker colors: `TimePickerDefaults.colors(selectorColor = DayKeeperColors.Primary,
  containerColor = DayKeeperColors.PrimaryContainer)`.
- Top-level helper: `fun snapTo5(minute: Int): Int = ((minute + 2) / 5 * 5).coerceIn(0, 55)`
  (32→30, 33→35).
- @Preview: opens in input mode at 09:30.

Exact Vietnamese labels: "Xong", "Huỷ", "Dùng đồng hồ", "Gõ giờ".

═══════════════════════════════════════════════════════════════════════
## 4. ui/addedit/AddEditHost.kt — stateful host, wires the dialog
═══════════════════════════════════════════════════════════════════════
```kotlin
@Composable
fun AddEditHost(existingItem: ScheduleItem?, onClose: () -> Unit, modifier: Modifier = Modifier)
```

- Hoist every field with `remember(existingItem)` (reset when the item changes):
  name, category, startMinute, endMinute, repeatDays (Set<DayOfWeek>),
  reminderEnabled, note. Defaults for create: start=07:00, end=08:00,
  category=ActivityCategory.DEFAULT, reminder=true, rest empty.
  Constants: `DEFAULT_START_MINUTE = 7*60`, `DEFAULT_END_MINUTE = 8*60`.
- Helper `private fun ScheduleItem?.repeatDaySet(): Set<DayOfWeek>` = filter
  DayOfWeek.entries by `(daysOfWeek and it.toBit()) != 0` (reuse existing `toBit()`).
- Two flags `showStartPicker`/`showEndPicker` (default false).
- Render AddEditScreen: startTime/endTime = `startMinute.toHourMinute()` /
  `endMinute.toHourMinute()`; onPickStart/onPickEnd set the matching flag;
  onToggleDay = immutable add/remove (`if (day in repeatDays) repeatDays - day else + day`);
  onClose = onClose.
- After AddEditScreen, render `TimePickerDialog` when a flag is set:
  initialHour=startMinute/60, initialMinute=startMinute%60; onConfirm →
  startMinute = hour*60+minute then clear the flag; onDismiss clears the flag.
  Same for end.
- Current TODO: `onSave = onClose` (no DB save yet).

═══════════════════════════════════════════════════════════════════════
## 5. ui/MainScreen.kt — navigation wiring
═══════════════════════════════════════════════════════════════════════
- State `showAddEdit` + `editingItemId: Long?`; collect `items` from TimelineViewModel.
- When showAddEdit: `existingItem = editingItemId?.let { id -> items.firstOrNull { it.id == id } }`,
  render `AddEditHost(existingItem, onClose = { showAddEdit = false })`.
- Otherwise render MainPager:
  - onAddClick → editingItemId=null; showAddEdit=true (create).
  - onItemClick(id) → editingItemId=id; showAddEdit=true (edit, prefilled).

═══════════════════════════════════════════════════════════════════════
## Referenced helpers (already in the repo)
═══════════════════════════════════════════════════════════════════════
- `util/TimeFormat.kt`: `fun Int.toHourMinute(): String` (420 → "07:00").
- `data/local/DayBit.kt`: `fun DayOfWeek.toBit(): Int`, `fun todayBit(): Int`.
- `data/local/ActivityCategory.kt`: enum REST/STUDY/SPORT/WORK, `label`, `DEFAULT = STUDY`.

## GLOBAL CONSTRAINTS
- Stateless except local state (the dialog's showInput, the Host's remembers).
- Immutable: toggling a Set / changing a field always produces a new copy.
- No inline hex/sp; literals only for structural padding/gap/size.
- Keep composables small, files < 800 lines, split by feature.
