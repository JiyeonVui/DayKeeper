# DayKeeper

Ứng dụng Android lập **kế hoạch tuần**: tạo các hoạt động lặp lại theo thứ trong tuần
(ví dụ "Tập gym", "Học tiếng Hàn"), mỗi hoạt động có giờ bắt đầu/kết thúc, phân loại,
ghi chú và nhắc nhở. Timeline hiển thị các hoạt động của từng ngày; khi lưu một hoạt động
**trùng giờ** với hoạt động đã có (cùng ngày lặp) sẽ bị chặn kèm cảnh báo.

Giao diện và toàn bộ chuỗi hiển thị bằng **tiếng Việt**.

## Tính năng

- 📅 **Timeline theo ngày** — chọn thứ trong tuần, xem hoạt động sắp theo giờ.
- ➕ **Thêm / Sửa hoạt động** — tên, loại (Nghỉ ngơi / Học tập / Thể thao / Công việc),
  giờ bắt đầu–kết thúc (bộ chọn giờ làm tròn 5 phút), ngày lặp, bật nhắc, ghi chú.
- ⚠️ **Phát hiện trùng giờ** — chặn lưu khi đụng giờ với hoạt động khác trên ngày chung,
  hiện rõ hoạt động bị đụng và các ngày trùng.
- 🔔 **Nhắc nhở** — thông báo đúng giờ bắt đầu, trên mọi ngày hoạt động lặp lại
  (dùng `AlarmManager` báo thức chính xác, tự lập lịch lại sau khi máy khởi động lại).

## Công nghệ

| Hạng mục | Lựa chọn |
|----------|----------|
| Ngôn ngữ | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Lưu trữ | Room 2.7.1 (KSP) |
| Bất đồng bộ | Kotlin Coroutines + Flow |
| Kiến trúc | MVVM + Repository + luồng dữ liệu một chiều (UDF) |
| Nhắc nhở | `AlarmManager` (báo thức chính xác) + `BroadcastReceiver` |
| Min / Target / Compile SDK | 24 / 36 / 36 |

> Một số lựa chọn có chủ đích: **không dùng Hilt** (DI thủ công trong `MainActivity`),
> **không dùng Navigation-Compose** (điều hướng bằng `HorizontalPager`), và **không dùng
> dynamic color** — luôn dùng design token cố định.

## Kiến trúc

```
ui (Compose)  →  ViewModel  →  ScheduleCoordinator / ScheduleRepository  →  Room DAO
                                        │
                                ReminderScheduler (AlarmManager)
```

- **`data/`** — Room entity `ScheduleItem`, DAO, `ScheduleRepository`, `ConflictChecker`.
- **`reminder/`** — `NextOccurrence` (hàm thuần tính lần báo kế tiếp), `ReminderScheduler`,
  `AlarmReceiver`, `BootReceiver`, `ReminderNotifier`, `ScheduleCoordinator`.
- **`ui/`** — màn hình & component Compose (timeline, addedit, settings, summary, theme).

Quy ước dữ liệu đáng chú ý:
- **Giờ** lưu dạng `Int` = số phút từ 0h (420 = 07:00).
- **Ngày lặp** lưu dạng bitmask `Int` (Thứ Hai = 1, Thứ Ba = 2, … Chủ Nhật = 64).

Xem [`ProjectArchite.md`](ProjectArchite.md) để có tài liệu kiến trúc đầy đủ.

## Chạy dự án

Yêu cầu: Android Studio (bản hỗ trợ AGP 9.x) + JDK 11+.

```bash
# Build APK debug
./gradlew assembleDebug

# Chạy unit test
./gradlew testDebugUnitTest
```

Hoặc mở thư mục bằng Android Studio rồi Run cấu hình `app`.

## Quyền

- `POST_NOTIFICATIONS` (Android 13+) — xin runtime khi mở app.
- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` — để nhắc đúng giờ; Android 12+ có thể cần
  cấp trong Cài đặt (app sẽ hướng dẫn nếu thiếu).
- `RECEIVE_BOOT_COMPLETED` — lập lại lịch nhắc sau khi khởi động lại máy.

## Cấu trúc thư mục (rút gọn)

```
app/src/main/java/com/jiyeon/daykeeper/
├── MainActivity.kt          Điểm vào, dựng dependency thủ công
├── data/                    Repository, ConflictChecker, Room (local/)
├── reminder/                Lập lịch & hiển thị nhắc nhở
├── ui/                      Compose: timeline, addedit, summary, settings, theme
└── util/                    Định dạng giờ / ngày
```

## Trạng thái

- ✅ Timeline, Thêm/Sửa, phát hiện trùng giờ, nhắc nhở: đã hoạt động.
- 🚧 Màn hình **Summary** và **Settings** còn là placeholder.
