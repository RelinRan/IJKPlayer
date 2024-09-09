#### IJKPlayer
IJK集成播放器，拥有亮度调整、音量调整、视频全屏播放。

#### 效果图标
![视频亮度](https://github.com/RelinRan/IJKPlayer/blob/master/ic_brightness.png)  
![视频音量](https://github.com/RelinRan/IJKPlayer/blob/master/ic_voice.png)  
![视频进度](https://github.com/RelinRan/IJKPlayer/blob/master/ic_progress.png)  

#### Fix
1.更新编译为ff4.0--ijk0.8.8--20210426--001版本;  
2.Android 11以上高版本报错问题    
3.音量、光亮、进度调节逻辑修改  
4.重新编译so,支持播放rtmp、rtsp协议  
5.支持音频解析pcm、amr、nellymoser、flac、ogg、wav、matroska  
6.去掉loading显示,显示实时网速缓冲  
7.自定义亮度、音量控件跟随手势变化  
8.增加手势滑动视频进度改变，同时中间显示滑动百分比显示  
9.快速来回滑动，出现水平和垂直冲突显示  
10.水平滑动修改视频进度增加网速缓冲显示   
11.增加VideoHolder常用的一些控制方法  
12.修改自定义View对应包名  
13.VideoView新增setDisplay方法设置显示方式  
14.VideoView新增setRatio方法设置显示比例  
15.手势滑动禁止多指滑动  
16.偶尔出现加载网速不消失  

#### [AAR]
[aar文件](https://github.com/RelinRan/IJKPlayer/blob/master/aar)
```
android {
    ....
    repositories {
    flatDir {
            dirs 'libs'
        }
    }
}

dependencies {
    implementation(name: 'IJKPlayer', ext: 'aar')
}
```
#### JitPack
项目/build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
项目/app/build.grade
```
dependencies {
	    implementation 'com.github.RelinRan:IJKPlayer:2024.9.7.1'
	}
```
源码中包含arm64-v8a armeabi-v7a x86 x86_64,可配置打包apk对应的ABI
```
android {
    ...
    defaultConfig {
        ...
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a'//只包含这些ABI
        }
    }
}
```

#### 权限配置
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
#### AndroidManifest.xml
播放页面
```
<activity
    android:name=".xxx"
    android:configChanges="keyboardHidden|orientation|screenSize"></activity>
```
Application
```
android:usesCleartextTraffic="true"
```
文件操作
```
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true"
    android:permission="android.permission.MANAGE_DOCUMENTS">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/path" />
    <intent-filter>
        <action android:name="android.content.action.DOCUMENTS_PROVIDER" />
    </intent-filter>
</provider>
```
path.xml
```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <root-path
        name="root"
        path="/storage/emulated/0" />
    <files-path
        name="files"
        path="/storage/emulated/0/Android/data/${applicationId}/files" />
    <cache-path
        name="cache"
        path="/storage/emulated/0/Android/data/${applicationId}/cache" />
    <external-path
        name="external"
        path="/storage/emulated/0/Android/data/${applicationId}/external" />
    <external-files-path
        name="Capture"
        path="/storage/emulated/0/Android/data/${applicationId}/files/Capture" />
    <external-cache-path
        name="Pick"
        path="/storage/emulated/0/Android/data/${applicationId}/files/Pick" />
    <external-cache-path
        name="TBS"
        path="/storage/emulated/0/Android/data/${applicationId}/files/TBS" />
</paths>
```

#### xml布局
```
<androidx.ijk.widget.VideoView
    android:id="@+id/ijk"
    android:layout_width="match_parent"
    android:layout_height="220dp"
    android:background="@color/colorBlack"/>
```
#### 参数配置
```
//初始化建议配置在Application
IJK ijk = IJK.config();
//设置默认显示方式
ijk.display(Display.AUTO);
//设置默认显示比例
ijk.ratio(16,9);
//使用硬解码器解码
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
//自动旋转视频画面
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
//处理分辨率变化
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
//设置最大缓冲区大小（默认是0，表示无限制）
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size",  1024*1024*5);
//设置最小缓冲帧数
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 60);
//设置最大缓存时长
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 5000);
//设置启动时的探测时间（毫秒）
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 400);
//设置分析最大时长（毫秒）
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
//强制刷新数据包
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
//禁用数据包缓冲
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
//设置帧率为30
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 120);
//设置超时时间
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000);
//启用无限缓冲模式
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 0);
//启用帧丢弃
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//跳过环路过滤器（Loop Filter），提高解码性能
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "skip_loop_filter", 48);
//禁用 HTTP 资源范围检测
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);
//启用精确的 seek（定位）
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//清除DNS缓存（为了提高域名解析的效率）
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
//自动重新连接
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
//调用prepareAsync()方法后是否自动开始播放
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
```
#### rtmp直播流，刚播放时，画面卡顿几秒，没有声音大概4、5秒后都恢复正常
不要配置probesize参数
```
ijk.option(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
ijk.option(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
```
#### 播放视频
```
VideoView video = findViewById(R.id.ijk);
//修改默认的显示方式
video.setDisplay(Display.AUTO);
//修改模式显示比例,注意：比例修改只适用Display.RATIO_WIDTH和Display.RATIO_HEIGHT
video.setRatio(Display.RATIO_WIDTH,16,9);
//是否是直播源
video.setLiveSource(true);
//视频控制ViewHolder
VideoHolder holder = video.getViewHolder();
//自定义全屏还是小屏幕显示，不设置就采用默认的逻辑；
video.setOnVideoSwitchScreenListener(orientation -> {
    //TODO: 自定显示方式 
});
//播放视频
String url = "https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209105011F0zPoYzHry.mp4";
//开始播放
String source = video.getDataSource();
if (TextUtils.isEmpty(source)) {
    video.setDataSource(url);
    video.start();
} else {
    video.reset();
    video.setDataSource(url);
    video.prepareAsync();
}
```
#### 颜色配置
```
<!--缓冲速度文字颜色-->
<color name="ijk_speed_text_color">#03DAC5</color>
<!--缓冲速度背景颜色-->
<color name="ijk_speed_background">#80000000</color>
<!--Seek圆点颜色-->
<color name="ijk_seek_dot">#DDDDDD</color>
<!--进度条进度颜色-->
<color name="ijk_seek_progress">#03DAC5</color>
<!--进度条背景颜色-->
<color name="ijk_seek_background">#E8E8E9</color>
<!--进度条缓冲进度颜色-->
<color name="ijk_seek_secondary_progress">#F0F0F1</color>
<!--声音/亮度背景颜色-->
<color name="ijk_voice_brightness_background">#80000000</color>
<!--声音/亮度文字颜色-->
<color name="ijk_circle_progress_text_color">#03DAC5</color>
<!--声音/亮度进度颜色-->
<color name="ijk_circle_progress_color">#03DAC5</color>
```
#### 尺寸配置
```
<!--缓冲速度文字大小-->
<dimen name="ijk_speed_text_size">14sp</dimen>
<!--音量亮度组合宽度-->
<dimen name="ijk_voice_brightness_width">65dp</dimen>
<!--音量亮度组合高度-->
<dimen name="ijk_voice_brightness_height">65dp</dimen>
<!--圆圈进度间距-->
<dimen name="ijk_circle_progress_margin">8dp</dimen>
<!--圆圈进度线条宽度-->
<dimen name="ijk_circle_progress_stroke_width">4dp</dimen>
<!--圆圈进度文字大小-->
<dimen name="ijk_circle_progress_text_size">13sp</dimen>
<!--音量图标宽度-->
<dimen name="ijk_voice_width">20dp</dimen>
<!--音量图标高度-->
<dimen name="ijk_voice_height">15dp</dimen>
<!--音量图标线条宽度-->
<dimen name="ijk_voice_stroke_width">1dp</dimen>
<!--亮度图标宽度-->
<dimen name="ijk_brightness_width">20dp</dimen>
<!--亮度图标高度-->
<dimen name="ijk_brightness_height">20dp</dimen>
<!--亮度图标圆圈和线条间距-->
<dimen name="ijk_brightness_gap">3dp</dimen>
<!--亮度图标线条宽度-->
<dimen name="ijk_brightness_line_width">2dp</dimen>
<!--亮度图标线条长度-->
<dimen name="ijk_brightness_line_length">2dp</dimen>
<!--亮度图标圆圈半径-->
<dimen name="ijk_brightness_radius">4dp</dimen>
```