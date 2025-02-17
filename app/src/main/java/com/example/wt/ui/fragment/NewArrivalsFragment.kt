package com.example.wt.ui.fragment

import WishlistViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wt.R
import com.example.wt.adapter.CustProductsAdapter
import com.example.wt.adapter.ProductsAdapter
import com.example.wt.databinding.FragmentNewArrivalsBinding
import com.example.wt.model.WishlistModel
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.repository.WishlistRepositoryImpl
import com.example.wt.viewModel.ProductViewModel

class NewArrivalsFragment : Fragment() {

    lateinit var binding: FragmentNewArrivalsBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var wishlistViewModel: WishlistViewModel
    lateinit var adapter: CustProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewArrivalsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productRepo = ProductRepositoryImpl()
        val wishlistRepo = WishlistRepositoryImpl()

        productViewModel = ProductViewModel(productRepo)
        wishlistViewModel = WishlistViewModel(wishlistRepo)

        adapter = CustProductsAdapter(requireContext(), ArrayList(), wishlistViewModel)

        productViewModel.getAllProduct()

        // Use viewLifecycleOwner for observing LiveData in fragments
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                adapter.updateData(it)
            }
        }

        productViewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.newProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.newRecyclerView.adapter = adapter
        binding.newRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
