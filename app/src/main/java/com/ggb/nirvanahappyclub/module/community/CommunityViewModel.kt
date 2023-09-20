package com.ggb.nirvanahappyclub.module.community

import android.app.Application
import com.ggb.common_library.base.viewmodel.BaseViewModel

class CommunityViewModel(application: Application) : BaseViewModel(application) {

    private var model: CommunityModel? = null

    init {
        model = CommunityModel()
    }

}