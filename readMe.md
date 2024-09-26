数据来源：https://www.uic.edu.cn/en/faculty.htm#/en

本地数据：app/src/main/res/raw/teachers_data.json

功能实现：要求中全部功能都实现，包含bouns

页面xml：app/src/main/res/layout/activity_main.xml
        app/src/main/res/layout/item_card.xml
        app/src/main/res/layout/item_color.xml

长按删除模块：app/src/main/java/com/example/myapplication/Helper/SimpleItemTouchHelperCallback.kt

下滑刷新模块：app/src/main/java/com/example/myapplication/Listener/OnLoadMoreListener.kt

业务数据加载模块：MainActivity.kt 中的load_teacher_data

实体：app/src/main/java/com/example/myapplication/Data/CardItem.kt

卡片展示依赖器：app/src/main/java/com/example/myapplication/RecyclerView/CardAdapter.kt

