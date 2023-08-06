package ru.com.bulat.trackergps.utils

import android.app.AlertDialog
import android.content.Context
import ru.com.bulat.trackergps.R

object DialogManager {
    fun showDialogLocationEnabled(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.loaction_disabled)
        dialog.setMessage(context.getString(R.string.loaction_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.btn_yes)){ _, _ ->
            listener.onClick()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.btn_no)){ _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick()
    }
}