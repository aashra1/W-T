package com.example.wt.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wt.R
import com.example.wt.databinding.ActivityAddProductBinding
import com.example.wt.model.ProductModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.utils.ImageUtils
import com.example.wt.utils.LoadingUtils
import com.example.wt.viewModel.ProductViewModel
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso

class AddProductActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var binding : ActivityAddProductBinding

    lateinit var productViewModel: ProductViewModel

    lateinit var loadingUtils: LoadingUtils

    lateinit var imageUtils: ImageUtils

    var imageUri: Uri? = null

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingUtils = LoadingUtils(this)

        imageUtils = ImageUtils(this)

        var repo = ProductRepositoryImpl()
        productViewModel = ProductViewModel(repo)


        imageUtils.registerActivity { url ->
            url.let { it ->
                imageUri = it
                Picasso.get().load(it).into(binding.productImage)
            }
        }
        binding.productImage.setOnClickListener {
            imageUtils.launchGallery(this)
        }
        binding.addProductButton.setOnClickListener {
            uploadImage()

        }

        // Set up the Navigation Drawer
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        // Set up the menu button click listener
        binding.menuImageBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                val intent = Intent(this@AddProductActivity, AdminActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_addprod -> {
                val intent = Intent(this@AddProductActivity, AddProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_updateprod -> {
                val intent = Intent(this@AddProductActivity, UpdateProductActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                val intent = Intent(this@AddProductActivity, NavigationActivity::class.java)
                startActivity(intent)
                Toast.makeText(this@AddProductActivity,"Logout from Admin",Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun uploadImage() {
        loadingUtils.show()
        imageUri?.let { uri ->
            productViewModel.uploadImage(this, uri) { imageUrl ->
                Log.d("checpoirs", imageUrl.toString())
                if (imageUrl != null) {
                    addProduct(imageUrl)
                } else {
                    Log.e("Upload Error", "Failed to upload image to Cloudinary")
                }
            }
        }
    }

    private fun addProduct(imageUrl: String) {
        var brandName = binding.brandName.text.toString()
        var productName = binding.productName.text.toString()
        var productPrice = binding.price.text.toString().toInt()

        var model = ProductModel(
            "",
            imageUrl,
            brandName,
            productName, productPrice,
        )

        productViewModel.addProduct(model) { success, message ->
            if (success) {
                Toast.makeText(
                    this@AddProductActivity,
                    message, Toast.LENGTH_LONG
                ).show()
                finish()
                loadingUtils.dismiss()
            } else {
                Toast.makeText(
                    this@AddProductActivity,
                    message, Toast.LENGTH_LONG
                ).show()
                loadingUtils.dismiss()
            }
        }
    }
}