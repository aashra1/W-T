package com.example.wt.ui.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.ProductsAdapter
import com.example.wt.databinding.ActivityAdminBinding
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.viewModel.ProductViewModel

class AdminActivity : AppCompatActivity() {

    lateinit var binding : ActivityAdminBinding

    lateinit var productViewModel: ProductViewModel

    lateinit var adapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = ProductRepositoryImpl()
        productViewModel = ProductViewModel(repo)

        adapter = ProductsAdapter(this@AdminActivity,
            ArrayList())

        productViewModel.getAllProduct()

        productViewModel.allProducts.observe(this){it->
            it?.let {
                adapter.updateData(it)
            }
        }

        productViewModel.loading.observe(this){loading->
            if(loading){ // true
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE

            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        binding.floatingActionButton.setOnClickListener {
            var intent = Intent(this@AdminActivity, AddProductActivity::class.java)
            startActivity(intent)
        }

        // Add spacing between items
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(5, 5, 5, 5) // Spacing for grid items
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}