package com.galang.notesapp.ui.add

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.galang.notesapp.MainActivity
import com.galang.notesapp.R
import com.galang.notesapp.data.entity.Notes
import com.galang.notesapp.data.entity.Priority
import com.galang.notesapp.databinding.FragmentAddBinding
import com.galang.notesapp.ui.NotesViewModel
import com.galang.notesapp.utils.ExtensionFunctions.setActionBar
import com.galang.notesapp.utils.HelperFunctions
import com.galang.notesapp.utils.HelperFunctions.parseToPriority
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding as FragmentAddBinding

    // untuk mendapatkan akses Dao
    private val addViewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

//        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)

//        binding.toolbarAdd.apply {
//            (requireActivity() as MainActivity).setSupportActionBar(this)
//            setupWithNavController(navController,appBarConfiguration)
//            navController.addOnDestinationChangedListener{ _, destination, _ ->
//                when(destination.id){
//                    R.id.addFragment -> this.setNavigationIcon(R.drawable.ic_left_arrow)
//                    //Ngubah pake ic sendiri
//                }
//            }
//        }

        binding.toolbarAdd.setActionBar(requireActivity())

        binding.spinnerPriorities.onItemSelectedListener = HelperFunctions.spinnerListener(context, binding
            .priorityIndicator)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)
        val action = menu.findItem(R.id.menu_save)       //Cara kerja save nya disini
        action.actionView.findViewById<AppCompatImageButton>(R.id.btn_save).setOnClickListener {
            insertNotes()
        }                                                //Sampe Sini
    }

    private fun insertNotes() {
        with(binding){
            val title = edtTitle.text.toString()
            val priority = spinnerPriorities.selectedItem.toString()
            val desc = edtDescription.text.toString()

            val calendar = Calendar.getInstance().time
            val date = SimpleDateFormat("dd MMMMM yyyy", Locale.getDefault()).format(calendar)

//            addViewModel.insertData(Notes(
//                0,
//                title,
//                parceToPriority(priority),
//                descriptionCompat,
//                date
//            ))

            val note = Notes(
                0,
                title,
                parseToPriority(priority,context),
                desc,
                date
            )

            if (edtTitle.text.isEmpty() || edtDescription.text.isEmpty()){
                edtTitle.setError("Please fill field")
                edtDescription.error = "Please fill field"
            }else{
                findNavController().navigate(R.id.action_addFragment_to_homeFragment)
                addViewModel.insertData(note)
                Toast.makeText(context, "Bisa jalan", Toast.LENGTH_SHORT).show()
                Log.i("AddFragment", "insertNotes: $note")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

//findNavController().navigate(R.id.action_addFragment_to_homeFragment)

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.menu_save -> findNavController().navigate(R.id.action_addFragment_to_homeFragment)
//            //ngeklik mau ngapain? mau ke home!, maka arahin ke home
//        }
//        return super.onOptionsItemSelected(item)
//    }       //Ga akan kerja karena tadi kita make (app:actionLayout)
//            //Cara kerja nya di bagian (onCreateOptionsMenu)
