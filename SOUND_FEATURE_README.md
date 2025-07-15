# Chức Năng Âm Thanh - SuSach App

## Tổng Quan
Đã thêm chức năng phát âm thanh vào ứng dụng SuSach để tăng trải nghiệm người dùng khi làm quiz.

## Các File Âm Thanh
- `correct_answer.mp3` - Phát khi trả lời đúng
- `wrong_answer.mp3` - Phát khi trả lời sai  
- `finish_quiz.mp3` - Phát khi hiển thị leaderboard

## Các Class Đã Thêm

### 1. SoundManager.java
**Vị trí:** `app/src/main/java/com/example/susach/managers/SoundManager.java`

**Chức năng:**
- Quản lý việc phát âm thanh
- Khởi tạo MediaPlayer cho từng file âm thanh
- Cung cấp các method để phát âm thanh
- Quản lý volume và trạng thái bật/tắt âm thanh

**Các method chính:**
```java
playCorrectSound()    // Phát âm thanh đáp án đúng
playWrongSound()      // Phát âm thanh đáp án sai
playFinishSound()     // Phát âm thanh hoàn thành
setSoundEnabled()     // Bật/tắt âm thanh
setVolume()          // Điều chỉnh âm lượng
```

### 2. SoundSettings.java
**Vị trí:** `app/src/main/java/com/example/susach/managers/SoundSettings.java`

**Chức năng:**
- Lưu trữ cài đặt âm thanh trong SharedPreferences
- Quản lý trạng thái bật/tắt âm thanh
- Quản lý âm lượng (0.0f - 1.0f)

## Tích Hợp Vào Các Activity

### 1. QuizActivity.java
**Thay đổi:**
- Thêm import `SoundManager`
- Khởi tạo `SoundManager` trong `onCreate()`
- Thêm logic phát âm thanh trong `showAnswerResult()`
- Thêm `release()` trong `onDestroy()`

**Logic phát âm thanh:**
```java
// Trong showAnswerResult()
if (chosenAnswer == correctAnswer) {
    soundManager.playCorrectSound();
} else {
    soundManager.playWrongSound();
}
```

### 2. LeaderboardActivity.java
**Thay đổi:**
- Thêm import `SoundManager`
- Khởi tạo `SoundManager` trong `onCreate()`
- Phát âm thanh khi hiển thị leaderboard
- Thêm `release()` trong `onDestroy()`

**Logic phát âm thanh:**
```java
// Trong onCreate() sau loadLeaderboardData()
if (soundManager != null) {
    soundManager.playFinishSound();
}
```

## Quyền Cần Thiết
Đã thêm quyền vào `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

## Cách Sử Dụng

### 1. Khởi tạo SoundManager
```java
SoundManager soundManager = new SoundManager(context);
```

### 2. Phát âm thanh
```java
// Phát âm thanh đáp án đúng
soundManager.playCorrectSound();

// Phát âm thanh đáp án sai
soundManager.playWrongSound();

// Phát âm thanh hoàn thành
soundManager.playFinishSound();
```

### 3. Cài đặt âm thanh
```java
// Bật/tắt âm thanh
soundManager.setSoundEnabled(true);

// Điều chỉnh âm lượng (0.0f - 1.0f)
soundManager.setVolume(0.8f);
```

### 4. Giải phóng tài nguyên
```java
@Override
protected void onDestroy() {
    if (soundManager != null) {
        soundManager.release();
    }
    super.onDestroy();
}
```

## Lưu Ý
- Âm thanh mặc định được bật với volume 70%
- Cài đặt âm thanh được lưu trong SharedPreferences
- Tự động giải phóng tài nguyên khi activity bị destroy
- Xử lý exception khi phát âm thanh để tránh crash app

## Testing
Để test chức năng âm thanh:
1. Chạy ứng dụng và làm quiz
2. Trả lời đúng → nghe âm thanh `correct_answer.mp3`
3. Trả lời sai → nghe âm thanh `wrong_answer.mp3`
4. Hoàn thành quiz → nghe âm thanh `finish_quiz.mp3` khi hiển thị leaderboard 