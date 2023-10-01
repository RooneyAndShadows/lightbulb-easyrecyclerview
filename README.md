# 💡 Lightbulb-EasyRecyclerView
[![](https://jitpack.io/v/rooneyandshadows/lightbulb-easyrecyclerview.svg)](https://jitpack.io/#rooneyandshadows/lightbulb-easyrecyclerview)

Looking for a hassle-free way to implement RecyclerViews in your Android app? Look no further than
EasyRecyclerView! Create custom RecyclerViews quickly and easily with EasyRecyclerView library. It
offers various features to streamline your development process and enhance your app's user
interface. With support for swipe to dismiss, clickable items, drag and drop, and many other features, you can
quickly create complex and interactive user interfaces that your users will love. Plus, the library
is lightweight, easy to use, and fully customizable, so you can tweak it to your heart's content.

-------

### 📋 Features

- **Lifecycle aware adapter state** - Items loaded in the list outlive configuration changes (
  orientation change etc.)
- Selection support
- Filter support
- Drag to reorder support
- Pull to refresh support
- Swipe to delete support
- Lazy loading support
- Empty layout support
- Diff util support
- Header and footer list items support
- Sticky headers decoration
- Bounce effect on overscroll
- Different layout managers support: LinearLayoutVertical, LinearLayoutHorizontal, FlowLayoutVertical, FlowLayoutHorizontal

-------

## 🎨 Screenshots

![Image](DEV/screenshots/combined.png)

## Latest releases 🛠

- Kotlin
  | [v2.4.0](https://github.com/RooneyAndShadows/lightbulb-easyrecyclerview/tree/2.4.0)
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
implementation 'com.github.rooneyandshadows:lightbulb-easyrecyclerview:2.4.0'
// Add recycler adapters support
implementation 'com.github.rooneyandshadows:lightbulb-recycleradapters:2.2.0'
```

### Note

> EasyRecyclerView works with adapters of type EasyRecyclerAdapter.

### 3. Describe the data model for the adapter

```Kotlin
class DemoModel : EasyAdapterObservableDataModel {

    @get:Bindable
    var title: String by ObservableProperty("", BR.title)

    @get:Bindable
    var subtitle: String by ObservableProperty("", BR.subtitle)
  
    override val itemName: String
        get() = title

    constructor(title: String, subtitle: String) {
        this.title = title
        this.subtitle = subtitle
    }

    // Parcelling part
    constructor(parcel: Parcel) {
        title = parcel.readString()!!
        subtitle = parcel.readString()!!
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
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

### 4. Create your data adapter

```kotlin
class SimpleAdapter : EasyRecyclerAdapter<DemoModel>() {
    override val collection: BasicCollection<DemoModel>
        get() = super.collection as BasicCollection<DemoModel>

    override fun createCollection(): BasicCollection<DemoModel> {
        return BasicCollection(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //create your ViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //bind your data
    }

    class ViewHolder(val binding: DemoListItemLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
```

### 5. Create your RecyclerView

```kotlin
class SimpleRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<DemoModel>(context, attrs) {
    override val adapter: SimpleAdapter
        get() = super.adapter as SimpleAdapter

    init {
        setAdapter(SimpleAdapter())
    }
}
```

### 6. Add the `EasyRecyclerView` into the XML

```xml

<com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView
    android:id="@+id/recycler_view" 
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 7. Select the view in your activity/fragment and provide it with adapter and data

```kotlin
@Override
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    recyclerView = fragmentView.findViewById(R.id.recycler_view).apply {
        if (savedInstanceState != null) return@apply
        val initialData = generateData(20)
        adapter.collection.set(initialData)
    }
}
```

And that's it. `EasyRecyclerView` is ready to use.

# Additional Setup

## Available attributes

```xml

<EasyRecyclerView>
    <attr name="erv_empty_layout_id" format="reference" /><!--Layout to show when there is no data-->
    <attr name="erv_supports_overscroll_bounce" format="boolean" /><!--Whether bounce on overscroll is supported.[default:false]-->
    <attr name="erv_layout_manager" format="enum"><!--Type of the layout manager for the recyclerview. [default:LAYOUT_LINEAR_VERTICAL] -->
        <enum name="LAYOUT_LINEAR_VERTICAL" value="1" />
        <enum name="LAYOUT_LINEAR_HORIZONTAL" value="2" />
        <enum name="LAYOUT_FLOW_VERTICAL" value="3" />
        <enum name="LAYOUT_FLOW_HORIZONTAL" value="4" />
    </attr>
</EasyRecyclerView>
```

## Enable pull to refresh

### Setup your fragment/activity

```kotlin
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    val decoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
    recyclerView.addItemDecoration(decoration)
    recyclerView.setPullToRefreshListener {
        //fetch data
        val data = generateData(10)
        recyclerView.adapter.collection.set(data)
        recyclerView.onRefreshDataFinished()
    }
    if (savedInstanceState != null) return
    recyclerView.adapter.collection.set(viewModel.listData)
}
```

## Enable lazy loading

### Setup your fragment/activity

```kotlin
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    val decoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
    recyclerView.addItemDecoration(decoration)
    recyclerView.setLazyLoadingListener {
        val data = generateData(10)
        val currentSize = recyclerView.adapter.collection.size()
        val hasMoreData =currentSize + data.size < 40
        recyclerView.adapter.collection.addAll(data)
        recyclerView.onLazyLoadingFinished(hasMoreData)
    }
  if (savedInstanceState != null) return
  recyclerView.adapter.collection.set(viewModel.listData)
}
```

## Enable swipe/drag of items

```kotlin
 @Override
override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    //...
    recyclerView.setAdapter(SimpleAdapter(), configureSwipeHandler())
}

private fun configureSwipeHandler(): TouchCallbacks<DemoModel> {
    return object : TouchCallbacks<DemoModel>(requireContext()) {
        override fun getAllowedSwipeDirections(item: DemoModel): Directions {
            return Directions.NONE
        }

        override fun getAllowedDragDirections(item: DemoModel): Directions {
            return Directions.UP_DOWN
        }

        override fun getActionBackgroundText(item: DemoModel): String {
            return item.itemName
        }

        override fun onSwipeActionApplied(
            item: DemoModel,
            position: Int,
            adapter: EasyRecyclerAdapter<DemoModel>,
            direction: Directions
        ) {
        }

        override fun onActionCancelled(
            item: DemoModel,
            adapter: EasyRecyclerAdapter<DemoModel>,
            position: Int
        ) {
        }

        override fun getSwipeBackgroundColor(direction: Directions): Int {
            return ResourceUtils.getColorByAttribute(requireContext(), R.attr.colorError)
        }

        override fun getSwipeIcon(direction: Directions): Drawable {
            return ResourceUtils.getDrawable(requireContext(), R.drawable.icon_delete)!!
        }

        override fun getPendingActionText(direction: Directions): String {
            return "Delete"
        }

        override fun getConfiguration(context: Context): SwipeConfiguration {
            return SwipeConfiguration(requireContext())
        }
    }
}
```

## Enable alternative empty layout

### Trough xml

```xml

<com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
    android:id="@+id/easy_recycler_view" 
    android:layout_width="match_parent"
    android:layout_height="match_parent" 
    app:erv_empty_layout_id="R.layout.yourlayout" />
```

### Trough java

```kotlin
@Override
override fun doOnViewStateRestored(savedInstanceState: Bundle?) {
    val emptyLayout = generateEmptyLayout()
    recyclerView.setEmptyLayout(emptyLayout,
        object : EasyRecyclerView.EasyRecyclerEmptyLayoutListener() {
            @Override
            override fun onInflated(view: View?) {
                super.onInflated(view)
            }

            @Override
            override fun onShow(view: View?) {
                super.onShow(view)
            }

            @Override
            override fun onHide(view: View?) {
                super.onHide(view)
            }
        })
}
```

**More detailed demonstrations you can find in the demo application.**