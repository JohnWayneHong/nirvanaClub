package com.ggb.nirvanahappyclub.module.message

import android.app.Application
import com.ggb.common_library.base.viewmodel.BaseViewModel

class MessageViewModel(application: Application) : BaseViewModel(application) {

    private var model: MessageModel? = null

    init {
        model = MessageModel()
    }

}