Android:SlideDelete-ListView
====================

This is a special ListView that you can slide to delete it's item .

***

#Usage
1.add SlideDeleteListView in your layout file
```xml
	<yourpackagename.SlideDeleteListView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/listView"
            android:layout_gravity="left|top" />
```
2.add dependencies in build.gradle
```java
	compile project(':slidedeletelistview')
```

3.setup SlideDeleteListView in your java code 
+ set Adapter
+ set RemoveListener
```java
	mSlideDeleteListView.setRemoveListener(new SlideDeleteListView.RemoveListener() {
            @Override
            public void removeItem(SlideDeleteListView.RemoveDirection direction, int position) {
                Toast.makeText(getContext(), "Item " + position + " has deleted",
                        Toast.LENGTH_SHORT).show();
                        
                //update data
                mListAdapter.updateDataSet(position);
            }
        });
        
	public void updateDataSet(int position) {
    		Log.i("Gracker","update position =" + position);
		arrayList.remove(position);
		notifyDataSetChanged();
}
```
