# yesitime - 目标追踪番茄钟 App

一款基于「计时器持续运行」理念的 Android 生产力应用，支持目标分级追踪、分类统计、重复任务、清单、日历等功能。

> 本项目修改自 [timeto.me](https://github.com/Medvedev91/timetome)（原作者：Dmitry Medvedev），在原项目基础上增加了目标分级、分类统计、父子目标等功能，并移除了 iOS / watchOS 相关代码，仅保留 Android 版本。

<p float="left">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg" width="200" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="200" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg" width="200" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.jpg" width="200" />
</p>

---

## 目录

- [核心理念](#核心理念)
- [功能特性](#功能特性)
- [使用指南](#使用指南)
- [构建项目](#构建项目)
- [项目结构](#项目结构)
- [许可证](#许可证)

---

## 核心理念

**计时器始终在运行。** 没有「停止」按钮——要结束当前活动，只需开始下一个活动。这样你始终知道自己在做什么，也获得了 24/7 的时间数据。

---

## 功能特性

### 计时器与活动
- 每个活动都有独立的计时器
- 番茄钟模式（Pomodoro）
- 自定义活动颜色和图标
- 计时器提示音与通知

### 目标追踪（新增功能）
- 将活动设置为 **Target（目标）**，自动显示在首页
- 支持 **父子目标** 嵌套关系
- 每个目标可设置 **重要度（importance，0%~100%）**
- 实时计算 **加权目标总时长**（按重要度加权）
- **10 级分级系统**，随累计时长变化显示不同颜色和表情
- 子目标时长自动计入父目标，统计时不重复计算

### 分类统计（新增功能）
- 自定义分类（Categories）
- 每个活动可归属多个分类
- 饼图统计支持按分类查看

### 任务与日历
- 任务列表与文件夹管理
- 重复任务（Repeating Tasks）— 像日程表一样自动生成每日任务
- 日历视图（月/日/列表）
- 今日 / 明日 特殊文件夹

### 清单（Checklists）
- 与活动/任务关联的清单
- 适合晨间例程、运动等场景

### 其他
- 快捷方式（Shortcuts）— 启动活动时自动打开 App 或链接
- 数据备份与恢复
- 暗色主题
- 一天开始时间自定义

---

## 使用指南

### 快速上手

#### 1. 开始你的第一个计时

首页中央是计时器。点击下方的活动按钮开始计时。要切换活动，点击另一个活动即可——计时器会自动切换。

> 💡 没有「停止」按钮。要结束当前活动，开始下一个就行。

#### 2. 创建自定义活动

进入 **Activities（活动）** 标签页 → 点击右下角 ➕ → 填写名称、选择颜色和图标 → 保存。

#### 3. 设置目标（Target）

在活动编辑页面，找到 **TARGET** 开关：
- 开启后，该活动会自动出现在首页
- 设置 **重要度（IM %）**：0~100%，用于计算加权目标时长
- 设置 **父目标（Parent）**：可选，建立父子目标嵌套关系

> 子目标的重要度留空则默认与父目标相同。

#### 4. 查看目标进度

首页计时器上方的彩色长条就是目标追踪栏：
- **上方大字体**：加权目标总时长（按重要度折算）
- **下方小字体**：实际追踪总时长
- 长条颜色和左侧表情随累计时长分级变化

#### 5. 分类统计

进入 **Activities → Summary**，点击顶部的 **Categories** 标签，即可查看按分类统计的饼图。

---

### 目标分级体系

| 级别 | 时长范围（分钟） | 含义 |
|------|-----------------|------|
| 0 | 0 ~ 30 | 🌱 种子 |
| 1 | 30 ~ 60 | 🌿 嫩芽 |
| 2 | 60 ~ 120 | 🍃 嫩叶 |
| 3 | 120 ~ 180 | 🌷 花开 |
| 4 | 180 ~ 240 | 🌸 盛放 |
| 5 | 240 ~ 360 | 🌺 繁花 |
| 6 | 360 ~ 480 | 🌷 郁金香 |
| 7 | 480 ~ 540 | 🌹 玫瑰 |
| 8 | 540 ~ 600 | ⭐ 星辰 |
| 9 | 600 ~ 720 | 🔥 燃烧 |

---

### 进阶用法

#### 重复任务

重复任务是本应用最强大的功能之一。如果你每天/每周有固定的作息，强烈建议使用：

1. 进入 **Tasks → Repeatings** 标签
2. 点击 ➕ 创建重复任务
3. 设置任务名、关联活动、计时器时长、触发时间、重复周期
4. 保存后，任务会在指定时间自动出现在「Today」文件夹
5. 点击任务 → 计时器自动开始

#### 清单

清单是重复任务的好搭档。比如「晨间例程」任务可以配一个清单：喝水、热身、洗漱、早餐……

#### 快捷方式

在活动编辑页添加快捷方式，启动该活动时会自动打开指定链接或 App。比如「冥想」活动自动打开 YouTube 引导视频。

---

## 构建项目

### 环境要求
- Android Studio Hedgehog 或更高版本
- JDK 21
- Android SDK 35

### 构建步骤

1. 克隆仓库：
   ```bash
   git clone https://github.com/Magnolia2294/yesitime
   ```

2. 用 Android Studio 打开项目根目录

3. 等待 Gradle 同步完成（首次会下载依赖，约 1~3 分钟）

4. 连接 Android 设备或启动模拟器（最低 Android 8.0 / API 26）

5. 点击 ▶ 运行按钮，或按 `Shift + F10`

### 构建 Release APK

```bash
./gradlew assembleBaseRelease
```

生成的 APK 位于 `android_app/build/outputs/apk/base/release/base-release.apk`

---

## 项目结构

```
.
├── android_app/          # Android 应用模块（Jetpack Compose UI）
│   └── src/main/java/me/timeto/app/
│       ├── ui/           # 所有界面组件
│       └── MainActivity.kt
├── shared/               # 共享业务逻辑模块（Kotlin Multiplatform）
│   └── src/
│       ├── commonMain/   # 通用业务逻辑、VM、数据库模型
│       └── androidMain/  # Android 特有实现
├── fastlane/             # 应用商店元数据
├── build.gradle.kts      # 根 Gradle 配置
└── settings.gradle.kts   # 模块配置
```

### 主要界面

| 界面名 | 说明 |
|--------|------|
| HomeScreen | 首页（计时器 + 目标栏 + 快捷按钮） |
| ActivitiesScreen | 活动列表 |
| TasksTabView | 任务标签页 |
| CalendarView | 日历 |
| ChecklistScreen | 清单详情 |
| SummaryFs | 数据统计（饼图） |
| SettingsScreen | 设置 |

---

## 许可证

本项目基于 **GNU General Public License v3.0** 开源。

原始项目 [timeto.me](https://github.com/Medvedev91/timetome) 由 Dmitry Medvedev 开发，同样采用 GPLv3 许可证。

修改部分版权所有 © 2026 yesitime contributors.
