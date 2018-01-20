# LucklyRecyclerView
结合了SwipeRefreshLayout和RecyclerView,将其封装在一起。方便使用；上拉加载更多，下拉刷新

效果图：
![](https://github.com/MrGaoGang/LucklyRecyclerView/blob/master/images/main.gif) 
<br>
如何获取:
第一步：在项目的build.gradle中添加<br>
```Java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
第二步：添加依赖<br>
```Java
dependencies {
	        compile 'com.github.MrGaoGang:LucklyRecyclerView:v1.0.0'
	}
```

