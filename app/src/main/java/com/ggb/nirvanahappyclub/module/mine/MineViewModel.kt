package com.ggb.nirvanahappyclub.module.mine

import android.app.Application
import com.ggb.common_library.base.viewmodel.BaseViewModel

class MineViewModel(application: Application) : BaseViewModel(application) {

    private var model: MineModel? = null

    init {
        model = MineModel()
    }

}