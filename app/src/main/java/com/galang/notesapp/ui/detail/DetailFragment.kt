package com.galang.notesapp.ui.detail

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.galang.notesapp.MainActivity
import com.galang.notesapp.R
import com.galang.notesapp.data.entity.Notes
import com.galang.notesapp.databinding.FragmentDetailBinding
import com.galang.notesapp.ui.NotesViewModel
import com.galang.notesapp.utils.ExtensionFunctions.setActionBar

class DetailFragment : Fragment() {

    private var _binding : FragmentDetailBinding? = null
    private val binding get() = _binding as FragmentDetailBinding

    private val navArgs by navArgs<DetailFragmentArgs>()

    private val detailViewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.safeArgs = navArgs
        binding.toolbarDetail.setActionBar(requireActivity())

//        binding.toolbarDetail.apply {
//            (requireActivity() as MainActivity).setSupportActionBar(this)
//            setupWithNavController(navController, appBarConfiguration)
//            navController.addOnDestinationChangedListener{ _, destination, _ ->
//                when(destination.id){
//                    R.id.detailFragment -> this.setNavigationIcon(R.drawable.ic_left_arrow)
//                    //Ngubah pake ic sendiri
//                }
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_edit -> {
                val action = DetailFragmentDirections.actionDetailFragmentToUpdateFragment(
                    Notes(
                        navArgs.currentItem.id,
                        navArgs.currentItem.title,
                        navArgs.currentItem.priority,
                        navArgs.currentItem.description,
                        navArgs.currentItem.date
                    )
                )
                findNavController().navigate(action)
            }
            R.id.action_delete -> confirmDeleteNote()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDeleteNote() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Delete '${navArgs.currentItem.title}' ?")      //Buat bikin tittle nya

                .setMessage("Sure want to remove this '${navArgs.currentItem.title}'?")
                //Buat bikin Pertanyaannya

                .setPositiveButton("OFC"){_ , _ ->
                    detailViewModel.deleteNote(navArgs.currentItem)
                    findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
                    Toast.makeText(it, "Succesfully delete '${navArgs.currentItem.title}'.", Toast.LENGTH_SHORT).show()
                }       //Buat bikin tombol yang kanan karena positive

                .setNegativeButton("No Banget"){_ , _ ->
                    Toast.makeText(it,"Why?", Toast.LENGTH_SHORT).show()
                }       //Buat bikin tombol yang kiri karena Negative

                .setNeutralButton("Cancel"){_ , _ ->}           //Buat bikin tombol yang tengah karena Netral

                .create()
                .show()

        }         //Kalo AlertDialogLayout ntar dia bikin layout sendiri lagi
    }       // Bikin kayak pop up pilihan gtuu

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


