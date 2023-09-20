package com.ggb.nirvanahappyclub.module.index

import android.app.Application
import com.ggb.common_library.base.viewmodel.BaseViewModel

class IndexViewModel(application: Application) : BaseViewModel(application) {

    private var model: IndexModel? = null

    init {
        model = IndexModel()
    }

}