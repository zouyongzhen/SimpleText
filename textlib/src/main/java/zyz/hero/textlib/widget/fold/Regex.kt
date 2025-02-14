package zyz.hero.textlib.widget.fold

//匹配<quote=[1]>，并将其转为中括号加数字添加span // 如："春种一粒粟<quote=[1]>秋收万颗子<quote=[2]>"  =>  "春种一粒粟[1]秋收万颗子[2]"
const val REGEX_QUOTE = "<quote=(\\[\\d+\\])>"
//高亮
const val REGEX_HIGH_LIGHT = "<hl=(.*?)>"