# 💡 Lightbulb-EasyRecyclerView
[![](https://jitpack.io/v/rooneyandshadows/lightbulb-easyrecyclerview.svg)](https://jitpack.io/#rooneyandshadows/lightbulb-easyrecyclerview)

All-in-one easy to use RecyclerView for your android project

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
  | [v2.0.2](https://github.com/RooneyAndShadows/lightbulb-easyrecyclerview/tree/2.0.2)
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
implementation 'com.github.rooneyandshadows:lightbulb-easyrecyclerview:2.0.2'
// Add recycler adapters support
implementation 'com.github.rooneyandshadows:lightbulb-recycleradapters:2.0.2'
```

### Note

> EasyRecyclerView works with adapters of type EasyRecyclerAdapter.

### 3. Describe the data model for the adapter

```Kotlin
class DemoModel : EasyAdapterObservableDataModel {
    @get:Bindable
    var title: String
        set(value) {
            if (field == value) return
            field = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    var subtitle: String
        set(value) {
            if (field == value) return
            field = value
            notifyPropertyChanged(BR.subtitle)
        }
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
    override val adapter: EasyRecyclerAdapter<DemoModel>
        get() = super.adapter as SimpleAdapter

    override val adapterCreator: AdapterCreator<DemoModel>
        get() = object : AdapterCreator<DemoModel> {
            override fun createAdapter(): SimpleAdapter {
                return SimpleAdapter()
            }
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
    <attr name="erv_supports_pull_to_refresh" format="boolean" /><!--Whether pull to refresh is supported.[default:false]-->
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

### Note

> To use this feature you must enable it trough XMl by adding erv_supports_pull_to_refresh="true"

### 1. Create a ViewModel which will keep the callback for the data.

```kotlin
class PullToRefreshDemoViewModel : ViewModel() {
    var pullToRefreshAction: RefreshDataAction<DemoModel>? = null

    @Override
    override fun onCleared() {
        super.onCleared()
        pullToRefreshAction?.dispose()
    }
}
```

### 2. Setup your fragment/activity

```kotlin
override fun doOnCreate(savedInstanceState: Bundle?, viewModel: PullToRefreshDemoViewModel) {
    if (savedInstanceState != null) return
    viewModel.pullToRefreshAction = RefreshDataAction(object : AsyncAction.Action<DemoModel> {
        override fun execute(easyRecyclerView: EasyRecyclerView<DemoModel>): List<DemoModel> {
            Thread.sleep(3000) //Simulate API call delay
            return generateData(10)
        }
    }, object : AsyncAction.OnComplete<DemoModel> {
        override fun execute(
            result: List<DemoModel>,
            easyRecyclerView: EasyRecyclerView<DemoModel>
        ) {
            easyRecyclerView.adapter.apply {
                collection.set(result)
            }
        }
    }, object : AsyncAction.OnError<DemoModel> {
        override fun execute(error: Exception, easyRecyclerView: EasyRecyclerView<DemoModel>) {
            error.printStackTrace()
        }
    })
}

override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    recyclerView.apply {
        setRefreshAction(viewModel.pullToRefreshAction) //Set action from the ViewModel
        addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12)))
        if (savedInstanceState != null) return@apply
        val initialCollection = generateData(4)
        adapter.collection.set(initialCollection)
    }
}
```

## Enable lazy loading

### 1. Create a ViewModel which will keep the callback for the data.

```kotlin
class LazyLoadingDemoViewModel : ViewModel() {
    var lazyLoadingAction: LoadMoreDataAction<DemoModel>? = null

    override fun onCleared() {
        super.onCleared()
        lazyLoadingAction?.dispose()
    }
}
```

### 2. Setup your fragment/activity

```kotlin
override fun doOnCreate(savedInstanceState: Bundle?, viewModel: LazyLoadingDemoViewModel) {
    if (savedInstanceState != null) return
    viewModel.lazyLoadingAction = LoadMoreDataAction(object : Action<DemoModel> {
        override fun execute(easyRecyclerView: EasyRecyclerView<DemoModel>): List<DemoModel> {
            Thread.sleep(3000)
            val offset = easyRecyclerView.adapter.collection.size()
            return generateData(10, offset)
        }
    }, object : OnComplete<DemoModel> {
        override fun execute(
            result: List<DemoModel>,
            easyRecyclerView: EasyRecyclerView<DemoModel>
        ) {
            easyRecyclerView.adapter.apply { collection.addAll(result) }
        }
    }, object : OnError<DemoModel> {
        override fun execute(error: Exception, easyRecyclerView: EasyRecyclerView<DemoModel>) {
            error.printStackTrace()
        }
    })
}

override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
    recyclerView.apply {
        setLazyLoadingAction(viewModel.lazyLoadingAction) //Set action from the ViewModel
        addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12)))
        if (savedInstanceState != null) return@apply
        adapter.collection.set(generateData(10))
    }
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