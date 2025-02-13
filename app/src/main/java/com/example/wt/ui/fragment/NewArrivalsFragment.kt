package com.example.wt.ui.fragment

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
import com.example.wt.repository.ProductRepositoryImpl
import com.example.wt.viewModel.ProductViewModel

class NewArrivalsFragment : Fragment() {

    lateinit var binding : FragmentNewArrivalsBinding

    lateinit var productViewModel: ProductViewModel

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

        var repo = ProductRepositoryImpl()
        productViewModel = ProductViewModel(repo)

        adapter = CustProductsAdapter(requireContext(), ArrayList())

        productViewModel.getAllProduct()

        productViewModel.allProducts.observe(this){it->
            it?.let {
                adapter.updateData(it)
            }
        }

        productViewModel.loading.observe(this){loading->
            if(loading){ // true
                binding.newProgressBar.visibility = View.VISIBLE
            }else{
                binding.newProgressBar.visibility = View.GONE

            }
        }

        binding.newRecyclerView.adapter = adapter
        binding.newRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }

}