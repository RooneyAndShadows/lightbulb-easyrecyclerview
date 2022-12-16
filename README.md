# 💡 Lightbulb-EasyRecyclerView

All-in-one easy to use RecyclerView for your android project

-------

### 📋 Features

- **Lifecycle aware adapter state** - Items loaded in the list outlive configuration changes (
  orientation change etc.)
- Selection support
- Drag to reorder support
- Pull to refresh support
- Swipe to delete support
- Lazy loading support
- Empty layout support
- Diff util support
- Header and footer list items support
- Sticky headers decoration
- Bounce effect on overscroll
- Different layout managers support: LinearLayoutVertical, LinearLayoutHorizontal,
  FlowLayoutVertical, FlowLayoutHorizontal

-------

## 🎨 Screenshots

![Image](DEV/screenshots/combined.png)

## Latest releases 🛠

- Java & AndroidX
  | [v1.0.24](https://github.com/RooneyAndShadows/lightbulb-easyrecyclerview/tree/1.0.24)

# Setup

### 1. Add Jitpack repository to your project

```
repositories {
    ...
    maven {
        url 'https://jitpack.io'
    }
}
```

### 2. Provide the gradle dependency

```gradle
implementation 'com.github.rooneyandshadows:lightbulb-easyrecyclerview:1.0.24'
// Add recycler adapters support
implementation 'com.github.rooneyandshadows:lightbulb-recycleradapters:1.0.14'
```

### Note

> EasyRecyclerView works with adapters of type EasyRecyclerAdapter.

### 3. Describe the data model for the adapter

```Kotlin
class DemoModel : EasyAdapterDataModel {
    val subtitle: String
    override val itemName: String

    constructor(title: String, subtitle: String) {
        itemName = title
        this.subtitle = subtitle
    }

    // Parcelling part
    constructor(parcel: Parcel) {
        itemName = parcel.readString()!!
        subtitle = parcel.readString()!!
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(itemName)
        dest.writeString(subtitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<DemoModel> {
        override fun createFromParcel(parcel: Parcel): DemoModel {
            return DemoModel(parcel)
        }

        override fun newArray(size: Int): Array<DemoModel?> {
            return arrayOfNulls(size)
        }
    }
}
```

### 4. Prepare your data adapter

```kotlin
class SimpleAdapter : EasyRecyclerAdapter<DemoModel>() {
    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //create your ViewHolder
    }

    @Override
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //bind your data
    }

    class ViewHolder(val binding: DemoListItemLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
```

### 5. Add the `EasyRecyclerView` into the XML

```xml
<com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
    android:id="@+id/easy_recycler_view" android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 6. Select the view in your activity/fragment and provide it with adapter and data

```kotlin
@Override
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    easyRecyclerView=getView().findViewById(R.id.recycler_view)
    easyRecyclerView.setAdapter(SimpleAdapter())
    if(savedState==null)
        recyclerView.getAdapter().setCollection(generateInitialData())
}
```

And that's it. `EasyRecyclerView` is ready to use.

# Additional Setup

## Available attributes

```xml
<EasyRecyclerView>
    <attr name="ERV_EmptyLayoutId" format="reference" /> <!--Layout to show when there is no data-->
    <attr name="ERV_SupportsPullToRefresh" format="boolean" /> <!--Whether pull to refresh is supported.[default:false]-->
    <attr name="ERV_SupportsLoadMore" format="boolean" /> <!--Whether lazy loading is supported.[default:false]-->
    <attr name="ERV_SupportsOverscrollBounce" format="boolean" /> <!--Whether bounce on overscroll is supported.[default:false]-->
    <attr name="ERV_LayoutManager" format="enum"> <!--Type of the layout manager for the recyclerview. [default:LAYOUT_LINEAR_VERTICAL] -->
      <enum name="LAYOUT_LINEAR_VERTICAL" value="1" />
      <enum name="LAYOUT_LINEAR_HORIZONTAL" value="2" />
      <enum name="LAYOUT_FLOW_VERTICAL" value="3" />
      <enum name="LAYOUT_FLOW_HORIZONTAL" value="4" />
    </attr>
</EasyRecyclerView>
```

## Enable pull to refresh

### Note

> To use this feature you must enable it trough XMl by adding ERV_SupportsPullToRefresh="true"

```kotlin
@Override
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    recyclerView.setRefreshCallback(object : EasyRecyclerView.RefreshCallback<DemoModel, SimpleAdapter> {
        override fun refresh(view: EasyRecyclerView<DemoModel, SimpleAdapter>) {
            //Get your new payload for the recycler and set it.
        }
    })
}
```

## Enable lazy loading

### Note

> To use this feature you must enable it trough XMl by adding ERV_SupportsLoadMore="true"

```kotlin
@Override
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    recyclerView.setLoadMoreCallback(object : EasyRecyclerView.LoadMoreCallback<DemoModel, SimpleAdapter> {
        override fun loadMore(rv: EasyRecyclerView<DemoModel, SimpleAdapter>) {
            //Get your next batch of data and append it to list
        }
    })
}
```

## Enable swipe/drag of items

```kotlin




@Override
protected void onViewCreated(View fragmentView,Bundle savedInstanceState){
        ...
        easyRecyclerView.setAdapter(new SimpleAdapter(),generateTouchCallback(easyRecyclerView));
        }

private void generateTouchCallback(EasyRecyclerView<DemoModel, SimpleAdapter> recyclerView){
        return new EasyRecyclerViewTouchHandler.TouchCallbacks<DemoModel>(){
@Override
public Directions getAllowedSwipeDirections(DemoModel item){
        return Directions.LEFT_RIGHT;
        }

@Override
public Directions getAllowedDragDirections(DemoModel item){
        return Directions.UP_DOWN;
        }

@Override
public String getActionBackgroundText(DemoModel item){
        return item.getItemName();
        }

@Override
public void onSwipeActionApplied(DemoModel item,int position,EasyRecyclerAdapter<DemoModel> adapter,Directions direction){
        recyclerView.post(()->{
        int actualPosition=recyclerView.getAdapter().getPosition(item);
        adapter.removeItem(actualPosition);
        });
        }

@Override
public void onActionCancelled(DemoModel item,EasyRecyclerAdapter<DemoModel> adapter,Integer position){
        }

@Override
public int getSwipeBackgroundColor(Directions direction){
        return ResourceUtils.getColorByAttribute(getContextActivity(),R.attr.colorError);
        }

@Override
public Drawable getSwipeIcon(Directions direction){
        return ResourceUtils.getDrawable(recyclerView.getContext(),R.drawable.icon_delete);
        }

@Override
public String getPendingActionText(Directions direction){
        return"Delete";
        }

@Override
public EasyRecyclerViewTouchHandler.SwipeConfiguration getConfiguration(Context context){
        return new EasyRecyclerViewTouchHandler.SwipeConfiguration(getContext());
        }
        };
        }
```

## Enable alternative empty layout

### Trough xml

```xml

<com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
    android:id="@+id/easy_recycler_view" android:layout_width="match_parent"
    android:layout_height="match_parent" app:ERV_EmptyLayoutId="R.layout.yourlayout" />
```

### Trough java

```java
@Override
protected void viewStateRestored(@Nullable Bundle savedInstanceState){
        super.viewStateRestored(savedInstanceState);
        View emptyLayout= //...inflate/create view 
        recyclerView.setEmptyLayout(emptyLayout,new EasyRecyclerView.EasyRecyclerEmptyLayoutListener(){
@Override
public void onInflated(View view){
        super.onInflated(view);
        }

@Override
public void onShow(View view){
        super.onShow(view);
        }

@Override
public void onHide(View view){
        super.onHide(view);
        }
        });
        }
```

**More detailed demonstrations you can find in the demo application.**