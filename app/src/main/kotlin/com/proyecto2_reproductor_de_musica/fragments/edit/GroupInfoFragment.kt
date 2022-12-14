package com.proyecto2_reproductor_de_musica.fragments.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.proyecto2_reproductor_de_musica.R
import com.proyecto2_reproductor_de_musica.data.db.entities.GroupsEntity
import com.proyecto2_reproductor_de_musica.data.db.entities.PerformerEntity
import com.proyecto2_reproductor_de_musica.data.db.entities.PersonsEntity
import com.proyecto2_reproductor_de_musica.data.viewModels.MediaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupInfoFragment : Fragment() {

    private val args by navArgs<GroupInfoFragmentArgs>()

    private lateinit var mediaViewModel : MediaViewModel
    private var isInDatabase : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_group_info, container, false)

        mediaViewModel = ViewModelProvider(this).get(MediaViewModel::class.java)
        setData(view)
        val saveBtn = view.findViewById<FloatingActionButton>(R.id.saveGroupInfo_btn)

        saveBtn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val groupName = view.findViewById<TextInputLayout>(R.id.groupName_layout)
                val startDate = view.findViewById<TextInputLayout>(R.id.startDate_layout)
                val endDate = view.findViewById<TextInputLayout>(R.id.endDate_layout)
                val group = GroupsEntity(
                    0,
                    checkUnknownString(groupName.editText?.text.toString()),
                    checkUnknownString(startDate.editText?.text.toString()),
                    checkUnknownString(endDate.editText?.text.toString())
                )

                val performer = PerformerEntity(
                    args.currentPerformer.id_performer,
                    args.currentPerformer.id_type,
                    checkUnknownString(groupName.editText?.text.toString()),
                )

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                    if(isInDatabase){
                        mediaViewModel.generalDao.updateGroupData(group)
                    }else{
                        mediaViewModel.generalDao.insertGroup(group)
                    }
                    mediaViewModel.generalDao.updatePerformer(performer)
                }

                Toast.makeText(view.context, "Saved!", Toast.LENGTH_SHORT).show()
            }
        })
        setDataToSpinner(view)
        return view
    }
    private fun setData(view : View){
        val groupName = view.findViewById<TextInputLayout>(R.id.groupName_layout)
        val startDate = view.findViewById<TextInputLayout>(R.id.startDate_layout)
        val endDate = view.findViewById<TextInputLayout>(R.id.endDate_layout)
        var group : GroupsEntity? = null
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            var result = mediaViewModel.generalDao.getGroupByName(args.currentPerformer.name)
            if(!result.isNullOrEmpty()){
                //the person is already in database
                group = result.first()
                isInDatabase = true
            }

            withContext(Dispatchers.Main){

                if(isInDatabase){
                    groupName.editText?.setText(group!!.name)
                    startDate.editText?.setText(group!!.start_date)
                    endDate.editText?.setText(group!!.end_date)
                }else{
                    groupName.editText?.setText(args.currentPerformer.name)
                }
            }

        }



    }

    fun setDataToSpinner(view : View){
        var persons : List<PersonsEntity>? = null
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            persons = mediaViewModel.generalDao.getAllPersons()
        }

        val spinner: Spinner = view.findViewById(R.id.personsGroup_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout

        var temp = arrayOf("person1", "person2", "person3")


        var adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, temp)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


//        var personNames = mutableListOf<String>()
//        persons!!.forEach { personNames.add(it.stage_name) }
//        Log.d("x", "person" + persons.toString())
//
//        var array : Array<String> = personNames.toTypedArray()
//        Log.d("x", "arrya of persons " + personNames)
//        if(!persons.isNullOrEmpty()){
//            var adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, array)
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
//        }

        spinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("x", "selected person " )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }
    fun checkUnknownString(string : String) : String{
        if(string.isNullOrEmpty())
            return "unknown"
        return string
    }

}