package com.example.wt.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import com.example.wt.R
import com.example.wt.databinding.ActivityContactUsBinding
import com.example.wt.ui.fragment.AccountFragment

class ContactUsActivity : AppCompatActivity() {

    lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish() // Closes ContactUsActivity and goes back to NavigationActivity
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up click listeners
        findViewById<ImageView>(R.id.imgFacebook).setOnClickListener {
            openFacebook()
        }

        findViewById<ImageView>(R.id.imgInstagram).setOnClickListener {
            openInstagram()
        }
    }

    private fun openFacebook() {
        val facebookUrl = "https://www.facebook.com/sambandha.rai.16"
        val facebookAppUri = Uri.parse("fb://facewebmodal/f?href=$facebookUrl")
        try {
            startActivity(Intent(Intent.ACTION_VIEW, facebookAppUri))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)))
        }
    }

    private fun openInstagram() {
        val instagramUrl = "https://www.instagram.com/_ginny_30_/"
        val instagramAppUri = Uri.parse("https://www.instagram.com/_ginny_30_/")
        try {
            startActivity(Intent(Intent.ACTION_VIEW, instagramAppUri).setPackage("com.instagram.android"))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl)))
        }
    }
}
