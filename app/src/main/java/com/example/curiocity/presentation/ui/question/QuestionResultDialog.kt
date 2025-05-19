package com.example.curiocity.presentation.ui.question

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.curiocity.databinding.DialogQuestionResultBinding

class QuestionResultDialog(
    private val title: String,
    private val message: String,
    private val primaryButtonText: String,
    private val secondaryButtonText: String?,
    private val onPrimaryClick: () -> Unit,
    private val onSecondaryClick: (() -> Unit)? = null
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogQuestionResultBinding.inflate(layoutInflater)

        binding.dialogTitle.text = title
        binding.dialogMessage.text = message
        binding.buttonPrimary.text = primaryButtonText
        binding.buttonPrimary.setOnClickListener {
            onPrimaryClick()
            dismiss()
        }

        if (secondaryButtonText != null && onSecondaryClick != null) {
            with(binding.buttonSecondary) {
                text = secondaryButtonText
                visibility = View.VISIBLE
                setOnClickListener {
                    onSecondaryClick.invoke()
                    dismiss()
                }
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()
        return dialog
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onPrimaryClick()
    }
}