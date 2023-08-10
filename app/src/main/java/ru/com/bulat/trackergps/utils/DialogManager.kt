package ru.com.bulat.trackergps.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import ru.com.bulat.trackergps.R
import ru.com.bulat.trackergps.databinding.SaveDialogBinding
import ru.com.bulat.trackergps.db.TrackItem

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

    fun showSaveDialog (context: Context, trackItem : TrackItem?, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val binding = SaveDialogBinding.inflate(LayoutInflater.from(context), null,false)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            tvTime.text = trackItem?.time
            tvVelocity.text = trackItem?.velocity.toString()
            tvDistance.text = trackItem?.distance.toString()


            btnSave.setOnClickListener {
                listener.onClick()
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    interface Listener {
        fun onClick()
    }
}