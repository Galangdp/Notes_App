package com.galang.notesapp.ui.update

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.galang.notesapp.MainActivity
import com.galang.notesapp.R
import com.galang.notesapp.data.entity.Notes
import com.galang.notesapp.databinding.FragmentUpdateBinding
import com.galang.notesapp.ui.NotesViewModel
import com.galang.notesapp.utils.ExtensionFunctions.setActionBar
import com.galang.notesapp.utils.HelperFunctions.parseToPriority
import com.galang.notesapp.utils.HelperFunctions.spinnerListener
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    private var _binding : FragmentUpdateBinding? = null
    private val binding get() = _binding as FragmentUpdateBinding

    private val saveArgs : UpdateFragmentArgs by navArgs()

    private val updateViewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inisialisasi variable dataBinding yang ada di XML
        binding.safeArgs = saveArgs

        setHasOptionsMenu(true)

        binding.apply {
            toolbarUpdate.setActionBar(requireActivity())
            spinnerPrioritiesUpdate.onItemSelectedListener = spinnerListener(context, binding.priorityIndicator)
        }

    }

//    private fun updateNote() {
//        binding.apply {
//            val title = edtTitleUpdate.text.toString()
//            val desc = edtDescriptionUpdate.text.toString()
//            val priority = spinnerPrioritiesUpdate.selectedItem.toString()
//            val date = Calendar.getInstance().time
//
//            val formattedDate = SimpleDateFormat("dd MMMMM yyyy", Locale.getDefault()).format(date)
//
//            findNavController().navigate(R.id.action_updateFragment_to_detailFragment)
//            Toast.makeText(context, "Note has been update", Toast.LENGTH_SHORT).show()
//
//            if (title.isEmpty()){
//                binding.edtTitleUpdate.error = "Please Fill Field"
//            }else if (desc.isEmpty()){
//                Toast.makeText(context, "Your Note Is still empty", Toast.LENGTH_LONG).show()
//            } else{
//                updateViewModel.updateNote(Notes(saveArgs.currentItem.id,
//                title,
//                parseToPriority(priority, context),
//                desc,
//                formattedDate
//                    )
//                )
//            }
//        }
//    }

    private fun updateNote() {
        with(binding) {
            val title = edtTitleUpdate.text.toString()
            val desc = edtDescriptionUpdate.text.toString()
            val priority = spinnerPrioritiesUpdate.selectedItem.toString()
            val date = Calendar.getInstance().time

            val formattedDate = SimpleDateFormat("dd MMMMM yyyy", Locale.getDefault()).format(date)

            val note = Notes(
                saveArgs.currentItem.id,
                title,
                parseToPriority(priority,context),
                desc,
                formattedDate
            )

            if (title.isEmpty()){
                binding.edtTitleUpdate.error = "Please Fill Field"
            } else{
                updateViewModel.updateNote(note)
                val action = UpdateFragmentDirections.actionUpdateFragmentToDetailFragment(note)
                findNavController().navigate(action)
                Toast.makeText(context, "Note has been update", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)

        val action = menu.findItem(R.id.menu_save)
        action.actionView.findViewById<AppCompatImageButton>(R.id.btn_save).setOnClickListener {
            updateNote()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

