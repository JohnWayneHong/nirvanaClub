package com.ggb.nirvanahappyclub.module.subscribe

import com.drake.brv.item.ItemPosition
import com.ggb.nirvanahappyclub.R

/**
 * 用于绑定Adapter的数据Model
 */
class FullSpanModel(var country: String = "乖乖帮", var name: String = "乖乖熊", var picture: Int = R.mipmap.carousel1, override var itemPosition: Int = 0) : ItemPosition