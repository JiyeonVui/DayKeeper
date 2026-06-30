# Báo thức toàn màn hình & Báo cáo tuần

Tài liệu mô tả hai thay đổi lớn:

1. **Báo thức toàn màn hình** — nhắc nhở hoạt động như báo thức thật (đè màn khoá, kêu + rung liên tục).
2. **Ghi phản hồi & Báo cáo tuần** — người dùng bấm "Tắt" (bỏ qua) hoặc "Thực hiện" (đã làm) trên màn báo, dữ liệu được lưu theo từng lần xảy ra và tổng hợp ở màn Summary.

---

## 1. Báo thức toàn màn hình

### Trước đây
Khi đến giờ, `AlarmReceiver` đẩy **một notification thường** (`IMPORTANCE_HIGH`). Trên màn hình chỉ hiện banner/heads-up, kêu một nhịp âm thông báo rồi im — không phải báo thức thật.

### Bây giờ
Đến giờ, một **foreground Service** ([`AlarmSoundService`]) bật lên giữ thông báo `fullScreenIntent` và **kêu chuông báo thức lặp vô hạn + rung liên tục** cho tới khi người dùng tắt — kêu **kể cả khi màn hình đang mở**. Màn `AlarmActivity` chỉ là **UI** đè lên màn khoá để bấm Tắt/Thực hiện.

### Kiến trúc (foreground Service + DI thủ công)
Âm thanh do **Service** sở hữu (một-instance do hệ thống quản lý), thay cho `object` global mutable state. Tầng nghiệp vụ gọi gián tiếp qua interface `AlarmAudioController`, lấy bản dùng chung qua service-locator `AlarmAudio.controller(context)` — đồng bộ với `ScheduleRepository.get` và cách `ReminderScheduling`/`NoopScheduler` tách khỏi AlarmManager.

```
AlarmManager (setExactAndAllowWhileIdle, RTC_WAKEUP)
        │  đến giờ → đánh thức máy (kể cả app đã đóng / Doze)
        ▼
AlarmReceiver.onReceive
        │  nạp lại item từ DB → AlarmAudio.controller(ctx).start(item) → lập lịch lần kế tiếp
        ▼
ServiceAlarmAudioController
        │  ReminderNotifier.buildAlarmNotification(item) → Notification
        ▼
AlarmSoundService (foreground, mediaPlayback)
        │  startForeground(notification) + chuông lặp + rung  ← kêu cả khi màn hình mở
        │  notification.fullScreenIntent → AlarmActivity
        │     · màn tắt/khoá: hệ thống mở thẳng AlarmActivity
        │     · màn đang mở:  hiện heads-up, chạm để mở AlarmActivity (chuông đã kêu sẵn)
        ▼
AlarmActivity (chỉ là UI, đè màn khoá, bật sáng màn hình)
        │  bấm "Tắt"/"Thực hiện" → AlarmAudio.controller(ctx).stop() + ghi log
        ▼
AlarmSoundService nhận ACTION_STOP → dừng chuông/rung, gỡ thông báo, stopSelf
```

### File liên quan

| File | Vai trò |
|------|---------|
| `reminder/AlarmSoundService.kt` *(mới)* | Foreground Service (`mediaPlayback`) giữ `MediaPlayer` (`USAGE_ALARM`, looping) + `Vibrator` waveform lặp và thông báo foreground; `START_NOT_STICKY`; `start()/stop()` gói sẵn intent. |
| `reminder/AlarmAudioController.kt` *(mới)* | Interface `AlarmAudioController` + `NoopAlarmAudioController` (cho test/preview). |
| `reminder/ServiceAlarmAudioController.kt` *(mới)* | Impl dựa trên Service + locator `AlarmAudio.controller(context)` (DI thủ công). |
| `reminder/AlarmActivity.kt` *(mới)* | Màn báo thức Compose, `setShowWhenLocked`/`setTurnScreenOn`, 2 nút **Tắt** / **Thực hiện**; chỉ là UI — nút bấm gọi `controller.stop()` + ghi log. |
| `reminder/ReminderNotifier.kt` | `buildAlarmNotification(item): Notification` (`setFullScreenIntent` + `CATEGORY_ALARM`) cho Service `startForeground`; kênh `IMPORTANCE_HIGH` nhưng **tắt âm/rung của kênh** (âm thanh do Service lo). |
| `reminder/AlarmReceiver.kt` | Gọi `AlarmAudio.controller(ctx).start(item)`; tách `firePendingIntent(context, itemId)` dùng chung (DRY). |
| `reminder/ReminderScheduler.kt` | Dùng `AlarmReceiver.firePendingIntent` thay cho việc tự dựng `PendingIntent`. |
| `reminder/ReminderPermissions.kt` | Thêm `canUseFullScreenIntent()` + `openFullScreenIntentSettings()` (Android 14+). |
| `MainActivity.kt` | Gọi `ensureFullScreenIntentPermission()` khi mở app. |
| `AndroidManifest.xml` | Thêm quyền `USE_FULL_SCREEN_INTENT`, `VIBRATE`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`; khai báo `AlarmActivity` (task riêng, `showWhenLocked`, `turnScreenOn`, ngoài Recents) + `AlarmSoundService` (`foregroundServiceType="mediaPlayback"`). |

### Quyền
- `USE_FULL_SCREEN_INTENT`, `VIBRATE`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`.
- **Android 14 (API 34)+**: quyền full-screen intent bị thu hồi mặc định cho app không phải đồng hồ/gọi điện. App sẽ Toast + mở Cài đặt để người dùng cấp; nếu không cấp thì nhắc nhở chỉ còn dạng heads-up.
- Foreground Service được khởi từ `AlarmReceiver` (nền) nhờ ngoại lệ của báo thức chính xác (`USE_EXACT_ALARM`).

### Lưu ý / giới hạn
- **Chuông kêu liên tục cả khi màn hình đang mở** vì Service sở hữu âm thanh. Khi mở khoá, full-screen intent vẫn hiện dạng **heads-up** (chạm để mở màn báo) nhưng chuông đã kêu sẵn — khác với trước đây phải chạm mới kêu.
- Báo **không tự dừng** khi UI bị đóng/đổi (chỉ dừng khi bấm Tắt/Thực hiện) — đúng kiểu báo thức thật.
- Kênh thông báo cố ý **im lặng** để không kêu chồng với Service.
- `foregroundServiceType="mediaPlayback"`: nếu lên Play Store có thể bị soi chính sách — cân nhắc `specialUse` kèm khai báo justification.
- Toggle "Rung" trong Settings hiện vẫn là UI tĩnh (chưa nối vào logic); báo thức luôn rung.

---

## 2. Ghi phản hồi & Báo cáo tuần

### Ý tưởng
Màn báo thức có 2 nút, **cả hai đều tắt chuông** nhưng ghi trạng thái khác nhau:

| Nút | Trạng thái ghi | Ý nghĩa |
|-----|----------------|---------|
| **Tắt** (viền) | `SKIPPED` | Bỏ qua lần này |
| **Thực hiện** (nền đặc) | `DONE` | Đã làm hoạt động |

### Thiết kế dữ liệu — log theo *từng lần xảy ra*
Một hoạt động lặp lại mỗi tuần, nên trạng thái phải gắn với **ngày cụ thể** chứ không phải gắn vào item. Vì vậy dùng **bảng riêng** thay vì thêm cột vào `schedule_items`.

`ActivityLog` (bảng `activity_logs`):

| Trường | Kiểu | Ghi chú |
|--------|------|---------|
| `id` | `Long` (PK, auto) | |
| `itemId` | `Long` | Tham chiếu hoạt động |
| `epochDay` | `Long` | Ngày của lần xảy ra (`LocalDate.toEpochDay`) |
| `status` | `ActivityStatus` | `DONE` / `SKIPPED` |
| `category` | `ActivityCategory` | **Chụp lại** để báo cáo sống sót khi item bị xoá/đổi loại |
| `loggedAt` | `Long` | Mốc thời gian ghi |

- **Unique index `(itemId, epochDay)`** → mỗi item/ngày chỉ một bản ghi; bấm lại sẽ ghi đè (`OnConflictStrategy.REPLACE`).

### File liên quan

| File | Vai trò |
|------|---------|
| `data/local/ActivityStatus.kt` *(mới)* | Enum `DONE`/`SKIPPED` kèm nhãn tiếng Việt. |
| `data/local/ActivityLog.kt` *(mới)* | Entity Room + unique index. |
| `data/local/ActivityLogDao.kt` *(mới)* | `upsert(log)`, `observeBetween(start, end)`. |
| `data/local/Week.kt` *(mới)* | `Week.currentRange()` — khoảng `epochDay` Thứ Hai–Chủ Nhật. |
| `data/local/Converters.kt` | Thêm converter cho `ActivityStatus`. |
| `data/local/AppDatabase.kt` | Thêm entity `ActivityLog` + `activityLogDao()`; **version 2 → 3**. |
| `data/ActivityLogRepository.kt` *(mới)* | `log(...)`, `observeRange(range)`; singleton `get(context)`. |
| `reminder/AlarmActivity.kt` | Mang thêm `category` (enum) + `epochDay` qua intent; bấm nút → ghi log qua `ActivityLogRepository`. |
| `ui/summary/WeeklyReport.kt` *(mới)* | Model `WeeklyReport` / `CategoryStat` + hàm gom `List<ActivityLog>.toWeeklyReport()`. |
| `ui/summary/SummaryViewModel.kt` *(mới)* | Quan sát log tuần hiện tại → `StateFlow<WeeklyReport>`. |
| `ui/summary/SummaryViewModelFactory.kt` *(mới)* | Factory tạo VM từ repo. |
| `ui/summary/SummaryScreen.kt` | Thay stub bằng UI báo cáo: thẻ % hoàn thành + phân tích theo loại + empty state. |
| `ui/MainScreen.kt` | Truyền `SummaryViewModelFactory` xuống `SummaryScreen`; cập nhật Preview (thêm DAO log giả). |
| `MainActivity.kt` | Tạo `SummaryViewModelFactory(ActivityLogRepository.get(...))` và truyền vào `MainScreen`. |

### Ghi log từ màn báo
- `AlarmActivity.intent(...)` đặt `epochDay = LocalDate.now().toEpochDay()` (ngày báo nổ) và `category` enum.
- Khi bấm nút, ghi qua một `CoroutineScope(SupervisorJob + Dispatchers.IO)` **ngoài lifecycle** của Activity để không bị huỷ khi `finish()`. Insert ngắn, app đang foreground nên kịp hoàn tất.

### Tính báo cáo
- Tỉ lệ hoàn thành = `DONE / (DONE + SKIPPED)` — chỉ tính các lần **có phản hồi**.
- Lần báo bị **bỏ lỡ** (không bấm gì) hiện **không** được tính là bỏ qua. Muốn coi "không phản hồi" là trạng thái riêng thì cần thêm logic đối chiếu lịch với log.
- Tuần lấy mốc lúc mở màn Summary (màn tạo lại mỗi lần vào tab nên đủ tươi).

---

## Migration / dữ liệu
- `AppDatabase` đang dùng `fallbackToDestructiveMigration(dropAllTables = true)` → khi bump version 2 → 3, **DB cũ bị xoá** (chấp nhận được ở giai đoạn dev). Khi lên production cần viết Migration thật.
