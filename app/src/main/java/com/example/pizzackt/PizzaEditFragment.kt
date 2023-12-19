package com.example.pizzackt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.pizzackt.data.ApiInterface
import com.example.pizzackt.data.DataPizza
import com.example.pizzackt.data.NewRequestDataPizza
import com.example.pizzackt.data.ResponseListPizza
import com.example.pizzackt.data.ResponsePizza
import com.example.pizzackt.data.RetrofitClient
import com.example.pizzackt.placeholder.PlaceholderContent
import com.google.gson.Gson

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val ARG_PIZZA_ACTION_TYPE = "ARG_PIZZA_ACTION_TYPE"
private const val ARG_PIZZA_POSITION = "ARG_PIZZA_POSITION"
private const val ARG_PIZZA_ITEM_DATA = "ARG_PIZZA_ITEM_DATA"

/**
 * A simple [Fragment] subclass.
 * Use the [PIZZAEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PizzaEditFragment : Fragment() {
    private var actionType: String? = null // EDIT | NEW
    private var position: Int = -1

    lateinit var dItemData: DataPizza
    lateinit var rvData: PlaceholderContent

    lateinit var editPizzaTitle: EditText
    lateinit var editPizzaStatus: EditText
    lateinit var editPizzaDetails: EditText
    lateinit var editPizzaPrice: EditText
    lateinit var editPizzaSaveBtn : Button
    lateinit var editPizzaCancelBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvData = PlaceholderContent

        arguments?.let {
            actionType = it.getString(ARG_PIZZA_ACTION_TYPE)
            position = it.getInt(ARG_PIZZA_POSITION)
//            dItemData = it.getSerializable(ARG_PIZZA_ITEM_DATA)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pizza_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Pizza", "POSITION "+ position)

        if (position > -1)
            dItemData = rvData.getItemData(position)!!

        editPizzaTitle = view.findViewById(R.id.editPizzaTitle)
        editPizzaStatus = view.findViewById(R.id.editPizzaStatus)
        editPizzaDetails = view.findViewById(R.id.editPizzaDetails)
        editPizzaPrice = view.findViewById(R.id.editPizzaPrice)
        editPizzaSaveBtn = view.findViewById(R.id.editPizzaSaveBtn)
        editPizzaCancelBtn = view.findViewById(R.id.editPizzaCancelBtn)

        if (actionType == "EDIT") {
            editPizzaTitle.setText(dItemData!!.title)
            editPizzaStatus.setText(dItemData!!.status)
            editPizzaDetails.setText(dItemData!!.details)
            editPizzaPrice.setText(dItemData!!.price)
            editPizzaSaveBtn.setText("Update")
        }
        else {
            editPizzaSaveBtn.setText("Save")
        }
        editPizzaSaveBtn.setOnClickListener(View.OnClickListener {
            Log.d("Pizza", "Submit Button Clicked. position: "+ position)

//            dItemData.copy()
            if (actionType == "EDIT") {
                dItemData?.title  = editPizzaTitle.getText().toString()
                dItemData?.status  = editPizzaStatus.getText().toString()
                dItemData?.details  = editPizzaDetails.getText().toString()
                dItemData?.price  = editPizzaPrice.getText().toString()
                PlaceholderContent.updateItem(position, dItemData)
//                updatePizza(dItemData)

                var gson = Gson()
                val ijstr = gson.toJson(dItemData)
                Log.d("Pizza: ", "Sending JSON data: " + ijstr)
                val response = updatePizzaFromJNI(PlaceholderContent.itemToId(dItemData), ijstr)
                Log.d("Pizza: ", "Response JSON data: " + response)

                var rdj = gson?.fromJson(response, ResponsePizza::class.java)
                val rd: DataPizza? = rdj?.data
                if (rd != null) {
                    PlaceholderContent.addItem(rd)
                }
                goBackToList()
            } else {
                val dItemData : NewRequestDataPizza = NewRequestDataPizza("","","", "")
                dItemData?.title  = editPizzaTitle.getText().toString()
                dItemData?.status  = editPizzaStatus.getText().toString()
                dItemData?.details  = editPizzaDetails.getText().toString()
                dItemData?.price  = editPizzaPrice.getText().toString()
//                savePizza(dItemData)

                var gson = Gson()
                val ijstr = gson.toJson(dItemData)
                Log.d("Pizza: ", "Sending JSON data: " + ijstr)
                val response = savePizzaFromJNI(ijstr)
                Log.d("Pizza: ", "Response JSON data: " + response)

                var rdj = gson?.fromJson(response, ResponsePizza::class.java)
                val rd: DataPizza? = rdj?.data
                if (rd != null) {
                    PlaceholderContent.addItem(rd)
                }
                goBackToList()

            }
        })

        editPizzaCancelBtn.setOnClickListener(View.OnClickListener {
            goBackToList()
        })

    }
    private fun goBackToList(){

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, PizzaListFragment.newInstance(1,1, position))
            .commit()
    }


    private external fun savePizzaFromJNI(d: String): String
    private external fun updatePizzaFromJNI(id: String, d: String): String


    private fun savePizza(d: NewRequestDataPizza) {
        val retrofit = RetrofitClient.getInstance()
        val apiInterface = retrofit.create(ApiInterface::class.java)
        GlobalScope.launch {
            try {

                val response = apiInterface.savePizza(d)
                if (response.isSuccessful()) {
                    //your code for handling success response
                    Log.d("Pizza: ", "SAve Success: " +response.body().toString())

                    val rd: DataPizza? = response.body()?.data
                    if (rd != null) {
                        PlaceholderContent.addItem(rd)
                    }
                    goBackToList()
                } else {
                    Log.e("Error", "Save Error" + response.errorBody().toString())
//                    Toast.makeText(
//                        this@PizzaEditFragment.context,
//                        response.errorBody().toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
                }
            }catch (Ex:Exception){
                Ex.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }

    private fun updatePizza(d: DataPizza) {

        val retrofit = RetrofitClient.getInstance()
        val apiInterface = retrofit.create(ApiInterface::class.java)
        GlobalScope.launch {
            try {
                val id = PlaceholderContent.itemToId(d)
                val response = apiInterface.updatePizza(id, d)
                if (response.isSuccessful()) {
                    //your code for handling success response
                    Log.d("Pizza: ", response.body().toString())

                    val rd: DataPizza? = response.body()?.data
//                    if (rd != null) {
////                        PlaceholderContent.addItem(rd)
//                    }
                    goBackToList()
                } else {
                    Log.e("Error", response.errorBody().toString())
//                    Toast.makeText(
//                        this@PizzaEditFragment.context,
//                        response.errorBody().toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
                }
            }catch (Ex:Exception){
                Ex.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PizzaEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(actionType: String, position: Int) =
            PizzaEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PIZZA_ACTION_TYPE, actionType)
                    putInt(ARG_PIZZA_POSITION, position)
                }
            }
    }
}