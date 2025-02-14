## Feature
* FoldText：支持展开/收起、省略的文本展示控件。支持富文本处理逻辑扩展，内置高亮及数字引用处理逻辑。
* MultiClickTextView：处理一个文本展示列表数据，列表项需要响应点击
* 更多文本控件
## 集成方式

## 效果图
![效果图](%E6%95%88%E6%9E%9C%E5%9B%BE.gif)
## 示例代码
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <zyz.hero.textlib.widget.fold.FoldText
        android:id="@+id/foldText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="20dp"
        android:layout_marginTop="50dp"
        app:buttonMarginRight="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:showMode="showModeFoldWithButtonText" />

    <zyz.hero.textlib.widget.fold.FoldText
        android:id="@+id/foldText2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="20dp"
        android:layout_marginTop="20dp"
        app:buttonMarginRight="0dp"
        app:layout_constraintTop_toBottomOf="@id/foldText"
        app:showMode="showModeUnfoldWithButtonText" />


</androidx.constraintlayout.widget.ConstraintLayout>

```
```java
 val foldText = findViewById<FoldText>(R.id.foldText)
        val foldText2 = findViewById<FoldText>(R.id.foldText2)
        foldText.setHandler(HighLightHandler {
            showToast(it)
        }, QuoteHandler {
            showToast(it)
        })
        foldText.setOnLayoutClickListener {
            showToast("OnLayoutClick")
        }
        foldText.setText(
            "<hl=导赏：>永和九年，岁在癸丑，暮春之初，会于会稽山阴之兰亭，修禊事也。<quote=[1]>群贤毕至，少长咸集。此地有崇山峻岭，茂林修竹，又有清流激湍，映带左右，引以为流觞曲水，列坐其次。虽无丝竹管弦之盛，一觞一咏，亦足以畅叙幽情。" +
                    "是日也，天朗气清，惠风和畅。仰观宇宙之大，俯察品类之盛，所以游目骋怀，足以极视听之娱，信可乐也。" +
                    "夫人之相与，俯仰一世，或因寄所托，放浪形骸之外。虽趣舍万殊，静躁不同，当其欣于所遇，暂得于己，快然自足，不知老之将至；及其所之既倦，情随事迁，感慨系之矣。向之所欣，俯仰之间，已为陈迹，犹不能不以之兴怀；况脍炙之人，遽然而去，修短随化，终期于尽。" +
                    "古人云：“死生亦大矣。”岂不痛哉！每览昔人兴感之由，若合一契，未尝不临文嗟悼<quote=[2]>，不能喻之于怀。固知一死生为虚诞，齐彭殇为妄作。" +
                    "后之视今，亦犹今之视昔。悲夫！故列叙时人，录其所述，虽世殊事异，所以兴怀，其致一也。后之览者，亦将有感于斯文。"
        )

        foldText2.setHandler(HighLightHandler {
            showToast(it)
        }, QuoteHandler {
            showToast(it)
        })
        foldText2.setOnButtonClickListener {
            showToast("onButtonClick")
        }
        foldText2.setOnLayoutClickListener {
            showToast("OnLayoutClick")
        }
        foldText2.setText(
            "<hl=导赏：>永和九年，岁在癸丑，暮春之初，会于会稽山阴之兰亭，修禊事也。<quote=[1]>群贤毕至，少长咸集。此地有崇山峻岭，茂林修竹，又有清流激湍，映带左右，引以为流觞曲水，列坐其次。虽无丝竹管弦之盛，一觞一咏，亦足以畅叙幽情。" +
                    "是日也，天朗气清，惠风和畅。仰观宇宙之大，俯察品类之盛，所以游目骋怀，足以极视听之娱，信可乐也。" +
                    "夫人之相与，俯仰一世，或因寄所托，放浪形骸之外。虽趣舍万殊，静躁不同，当其欣于所遇，暂得于己，快然自足，不知老之将至；及其所之既倦，情随事迁，感慨系之矣。向之所欣，俯仰之间，已为陈迹，犹不能不以之兴怀；况脍炙之人，遽然而去，修短随化，终期于尽。" +
                    "古人云：“死生亦大矣。”岂不痛哉！每览昔人兴感之由，若合一契，未尝不临文嗟悼<quote=[2]>，不能喻之于怀。固知一死生为虚诞，齐彭殇为妄作。" +
                    "后之视今，亦犹今之视昔。悲夫！故列叙时人，录其所述，虽世殊事异，所以兴怀，其致一也。后之览者，亦将有感于斯文。"
        )
```
## 属性
属性说明 button为控件右下角控件，可展示icon及文本
| 属性                             | 类型/枚举值                  | 描述                                         |
|:--------------------------------:|:----------------------------:|:--------------------------------------------:|
| showMode                         | showModeUnfoldWithButtonIcon | 超过限制行数显示省略icon                     |
|                                  | showModeUnfoldWithButtonText | 超过限制行数显示省略文本                     |
|                                  | showModeFoldWithButtonIcon   | 超过限制行数显示展开/收起icon                |
|                                  | showModeFoldWithButtonText   | 超过限制行数显示展开/收起文本                |
| limitLineCount                   | integer\|reference           | 限制行数                                     |
| supportEmoji                     | boolean                      | 是否支持emoji，默认为true，设置false可提高性能 |
| contentTextColor                 | color\|reference             | 文本颜色                                     |
| contentTextSize                  | dimension                    | 文本大小                                     |
| contentTextLineSpacingMultiplier | float                        | 文本间距（倍距如1.2）                          |
| contentTextLineSpacing           | float                        | 文本间距                                     |
| buttonTextSize                   | dimension                    | 按钮文本大小                                 |
| buttonTextColor                  | color\|reference             | 按钮文本颜色                                 |
| buttonEllipsizeIcon              | reference                    | 按钮省略时icon                               |
| buttonEllipsizeText              | string\|reference            | 按钮省略时文本                               |
| buttonFoldText                   | string\|reference            | 按钮折叠状态下的文本                         |
| buttonExpandText                 | string\|reference            | 按钮展开状态下的文本                         |
| buttonFoldIcon                   | reference                    | 按钮折叠状态下的icon                         |
| buttonExpandIcon                 | reference                    | 按钮展开状态下的icon                         |
| buttonMarginRight                | dimension\|reference         | 按钮右边距                                   |
| buttonMarginLeft                 | dimension\|reference         | 按钮左边距                                   |

