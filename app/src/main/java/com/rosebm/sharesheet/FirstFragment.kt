package com.rosebm.sharesheet

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rosebm.sharesheet.databinding.FragmentFirstBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonShare.setOnClickListener {
            buildTheShareSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildTheShareSheet() {
        try {
            val intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_to_share))
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE,  getString(R.string.share))
                this.clipData = getClipManager().primaryClip
            }

            val chooser: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent.createChooser(intent,  null, getPendingIntent().intentSender)
            } else {
                Intent.createChooser(intent, null)
            }
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Timber.e("Error: ${e.localizedMessage}")
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT

        return PendingIntent.getBroadcast(context, 0,
            Intent(context, ShareSheetBroadcastReceiver::class.java), flag)
    }

    private fun getClipManager(): ClipboardManager {
        val clipManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipManager.addPrimaryClipChangedListener {
            Timber.tag("FirstFragment").d("Link shared via Copy link")
        }

        return clipManager
    }
}