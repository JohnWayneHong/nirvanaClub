package com.ggb.nirvanahappyclub.module.subscribe

import android.app.Application
import com.ggb.common_library.base.viewmodel.BaseViewModel

class SubscribeViewModel(application: Application) : BaseViewModel(application) {

    private var model: SubscribeModel? = null

    init {
        model = SubscribeModel()
    }

}