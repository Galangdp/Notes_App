package com.galang.notesapp.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.galang.notesapp.MainActivity
import com.galang.notesapp.R
import com.galang.notesapp.data.entity.Notes
import com.galang.notesapp.databinding.FragmentHomeBinding
import com.galang.notesapp.ui.NotesViewModel
import com.galang.notesapp.utils.ExtensionFunctions.setActionBar
import com.galang.notesapp.utils.HelperFunctions
import com.galang.notesapp.utils.HelperFunctions.checkIsDataEmpty
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), SearchView.OnQueryTextListener{

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private val homeViewModel by viewModels<NotesViewModel>()

    private val homeAdapter by lazy { HomeAdapter() }

    private var _currentData: List<Notes>? = null
    private val currentData get() = _currentData as List<Notes>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    //Setting Tombol Back Setiap Fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mHelperFunctions = HelperFunctions  //mHelper dari fragment_home.xml

        setHasOptionsMenu(true)     //Ngasih tau fragment kalo kita punya menu sendiri sendiri

        //Bikin = nav controller, app bar configuration, di tempatkan di toolbar
        val navController = findNavController()     //Nav Conroller
        val appBarConfiguration = AppBarConfiguration(navController.graph)  //App Bar Configuration


        binding.apply {
            toolbarHome.setActionBar(requireActivity())

            fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
            }

            setupRecylerView()

        }
//        binding.toolbarHome.apply {
//            (requireActivity() as MainActivity).setSupportActionBar(this)
//            //Gak bisa langsung (setSupportActionBar) karena ga ada app compatt nya di home fragment ini
//            //Require activity buat nge get Activity
//            setupWithNavController(navController,appBarConfiguration)
//        }               //Ini Tempatin Toolbar nya
//
//        binding.fabAdd.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
//        }
//
//        binding.btnToDetail.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
//        }

    }

    private fun setupRecylerView() {
        binding.rvNotes.apply {
            homeViewModel.getAllData().observe(viewLifecycleOwner){
                //.observe buat mantau live data, kalo live data buat mantau aturan data
                checkIsDataEmpty(it)
                showEmptyDataLayout(it)
                homeAdapter.setData(it)
                _currentData = it
            }
            adapter = homeAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)//ngambil
            // sampingnya jika itu kosong

            swipeToDelete(this)
        }
    }

    private fun showEmptyDataLayout(data: List<Notes>) {
        when(data.isEmpty()){
            true -> {
                binding.rvNotes.visibility = View.INVISIBLE
                binding.imgNoData.visibility = View.VISIBLE
            }
            else -> {
                binding.rvNotes.visibility = View.VISIBLE
                binding.imgNoData.visibility = View.INVISIBLE
            }
        }
    }

//    private fun checkIsDataEmpty(data: List<Notes>) {
//        binding.apply {
//            if (data.isEmpty()){
//                imgNoData.visibility = View.VISIBLE
//                rvNotes.visibility = View.INVISIBLE
//            } else {
//                imgNoData.visibility = View.INVISIBLE
//                rvNotes.visibility = View.VISIBLE
//            }
//        }
//    }//Logic nampilin background empty nya
////
//// Setelah saya check ini hanya menampilkan pada ui nya saja bukan pada data nya
////
//// DI PINDAHIN KE HELPER FUNCTION



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val search = menu.findItem(R.id.menu_search)
        val searchAction = search.actionView as? SearchView
        searchAction?.setOnQueryTextListener(this)  //ini buat langsung nampilin datanya pas ngetik
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_priority_high -> homeViewModel.sortByHighPriority().observe(this){ dataHigh ->
                homeAdapter.setData(dataHigh)
            }
            R.id.menu_priority_low -> homeViewModel.sortByLowPriority().observe(this){ dataLow ->
                homeAdapter.setData(dataLow)
            }
            R.id.menu_delete_all -> confirmDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }       // Activity -> View Model -> Repository -> DATABASE (LOCAL, ONLINE)

    private fun confirmDeleteAll(){
        if (currentData.isEmpty()){
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.txt_no_notes))
                .setMessage("No Have Data")
                .setPositiveButton("Close"){dialog, _->
                    dialog.dismiss()
                }
                .show()
        }else{
            AlertDialog.Builder(requireContext())
                .setTitle("Delete All ur Notes ?")
                .setMessage("Are u sure want clear all of this data ?")
                .setPositiveButton("Yes"){_, _->
                    homeViewModel.deleteAllData()
                    Toast.makeText(requireContext(), "Succesfully deleted data", Toast.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton("No"){ dialog, _->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val querySearch = "%$query%"
        query?.let {
            homeViewModel.searchByQuery(querySearch).observe(this){
                homeAdapter.setData(it)
            }
        }
        return true
    }//Di jalankan ketika menekan tombol search

    override fun onQueryTextChange(newText: String?): Boolean {
        val querySearch = "%$newText%"
        newText?.let {
            homeViewModel.searchByQuery(querySearch).observe(this){
                homeAdapter.setData(it)
            }
        }
        return true
    }   //Di jalankan setiap ada perubahan di search viewnya (saat ngetik)

    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT   // Ngatur mau ngapus ke mana
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = homeAdapter.listNotes[viewHolder.adapterPosition]
                homeViewModel.deleteNote(deletedItem)
                restoredData(viewHolder.itemView, deletedItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDelete)
        itemTouchHelper.attachToRecyclerView(recyclerView)  //Nampilin di recyler view ny soalnya tadi kan belum
    }   //Buat ngatur ngapus nya

    private fun restoredData(view: View, deletedItem: Notes) {
        val snackBar = Snackbar.make(
            view, "Deleted: '${deletedItem.title}'", Snackbar.LENGTH_LONG
        )
        snackBar.setTextColor(ContextCompat.getColor(view.context, R.color.black))
        snackBar.setAction("Undo"){
            homeViewModel.insertData(deletedItem)
        }
        snackBar.setActionTextColor(ContextCompat.getColor(view.context, R.color.black))
        snackBar.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}