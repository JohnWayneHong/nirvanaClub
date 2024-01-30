package com.ggb.nirvanahappyclub.module.subscribe

import com.drake.brv.item.ItemPosition

/**
 * 用于绑定Adapter的数据Model
 */
class SimpleModel(var country: String = "乖乖帮", var name: String = "乖乖兔", override var itemPosition: Int = 0) : ItemPosition