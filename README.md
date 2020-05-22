# IJKPlayer
IJK集成播放器，拥有亮度调整、音量调整、视频全屏播放。
## 方法一  ARR依赖
[AndroidKit.arr](https://github.com/RelinRan/AndroidKit/blob/master/IJKPlayer.aar)
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
        implementation 'tv.danmaku.ijk.media:ijkplayer-java:0.8.8'
        implementation 'tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.4'
}

```
## 方法二   JitPack依赖
### A.项目/build.grade
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### B.项目/app/build.grade
```
	dependencies {
	    implementation 'com.github.RelinRan:IJKPlayer:1.0.0'
        implementation 'tv.danmaku.ijk.media:ijkplayer-java:0.8.8'
        implementation 'tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.4'
	}
	android:configChanges="keyboardHidden|orientation|screenSize"
```
## AndroidManifest.xml Activity配置
```
        <activity
            android:name=".XXXXX"
            android:configChanges="keyboardHidden|orientation|screenSize"></activity>
```

## xml布局
```
    <com.android.ijk.player.view.IJKVideoView
        android:id="@+id/ijk"
        android:layout_width="match_parent"
        android:layout_height="220dp"
        android:background="@color/colorBlack"></com.android.ijk.player.view.IJKVideoView>
```
## 播放视频
```
        //初始化建议配置在Application
        IJK.config().initOptions();
        IJKVideoView ijk = findViewById(R.id.ijk);
        //播放视频
        ijk.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        ijk.start();
```