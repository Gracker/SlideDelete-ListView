Android:SlideDelete-ListView
====================

这是基于Android的一个项目,这个ListView提供左右滑动来删除Item的功能. 

***

#Usage
使用很简单,将SlideCutListView.java这个类导入到项目中,然后就像使用正常的ListView一样去使用即可,不需要额外的设置.例如:

		<yourpackagename.SlideCutListView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/listView"
            android:layout_gravity="left|top" />

其中将yourpackagenam换成SlideCutListView.java所在的包得路径即可.