package com.example.pizzackt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzackt.data.ApiInterface
import com.example.pizzackt.data.DataPizza
import com.example.pizzackt.data.ResponseListPizza
import com.example.pizzackt.data.RetrofitClient
import com.example.pizzackt.placeholder.PlaceholderContent
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [PizzaListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PizzaListFragment : Fragment() {

    private var columnCount = 1
    private var editBack = 0
    private var editPosition = 0

    lateinit var rvAdapter: PizzaRecyclerViewAdapter
    lateinit var rv: RecyclerView
    lateinit var rvData: PlaceholderContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvData = PlaceholderContent

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            editBack = it.getInt(ARG_EDIT_BACK)
            editPosition = it.getInt(ARG_EDIT_POSITION)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pizza_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            rv = view
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = PizzaRecyclerViewAdapter(PlaceholderContent.ITEMS)
            }
            rvAdapter = rv.adapter as PizzaRecyclerViewAdapter

            // Applying OnClickListener to our Adapter
            rvAdapter.setOnClickListener(object : PizzaRecyclerViewAdapter.OnClickListener {
                override fun onClick(position: Int, model: DataPizza) {
                    val d: DataPizza? = rvData.getItemData(position)
                    Log.d("Pizza", "Item on clicked")
                }

                override fun onLongClick(
                    position: Int,
                    model: DataPizza
                ) {
                    Log.d("Pizza", "Item on Long Clicked / Press")
                }

                override fun onClickToEdit(
                    position: Int,
                    model: DataPizza
                ) {
                    val d: DataPizza? = rvData.getItemData(position)
                    val actionType = "EDIT" // EDIT | NEW

                    Log.d("Pizza", "Pizza list EDIT btn clicked on clicked")
                    Log.d("Pizza", d.toString())
                    val fr = parentFragmentManager.beginTransaction()
                    fr.replace(R.id.nav_host_fragment_content_main, PizzaEditFragment.newInstance(
                        actionType,
                        position
                    ))
                    fr.commit()
                }

                override fun onClickToDelete(
                    position: Int,
                    model: DataPizza
                ) {
                    val d: DataPizza? = rvData.getItemData(position)
                    rvData.removeItem(position)
                    rvAdapter.submitList(PlaceholderContent.ITEMS)
//                        rvAdapter.notifyDataSetChanged()
//                        TODO: server-side api implementation
                    if (d != null) {
//                        deletePizza(d)
                        val id = PlaceholderContent.itemToId(d)
                        deletePizzaFromJNI(id)
                    }
                }
            })
            rvAdapter.notifyDataSetChanged()

        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        getPizzaList()
        getPizzaListNative()
//        rvAdapter.notifyDataSetChanged()
        Log.i("Pizza", "Adapter Created")
        Log.i("Pizza", "Adapter Created, view Created")
        Log.i("Pizza", "Adapter Data Size: " + rvAdapter.getItemCount().toString())
    }

    private fun notifyDataChanged(){

        rvAdapter.submitList(PlaceholderContent.ITEMS)
//        Log.d("Pizza: ", "Adapter List Count: " + rvAdapter.getItemCount().toString())
        rvAdapter.notifyDataSetChanged()
//        Log.d("Pizza: ", "Adapter List Count: = " + rvAdapter.getItemCount().toString())
    }

    // convert c/c++ std::string str to java string UTF-8
    private fun convert_jbyteArray_to_jstring(responseByteArray: ByteArray): String {
        return String(responseByteArray, charset("UTF-8"))
    }

    private fun getPizzaListNative() {
//        val responseByteArray = getPizzaListFromJNI()
//        // convert byte array to string
//        val response = String(responseByteArray, charset("UTF-8"))
        val response = convert_jbyteArray_to_jstring(getPizzaListFromJNI())
        var gson = Gson()
        var rd = gson?.fromJson(response, ResponseListPizza::class.java)
        val dList: List<DataPizza>? = rd?.data
        if (dList != null) {
            PlaceholderContent.clearAll()
//                        Log.d("Pizza: ", "Adapter List Count: " + rvAdapter.getItemCount().toString())
            for (i in 0.. dList.size - 1) {
//                            Log.d("Pizza: ", "AdapterItem: " + dList[i].toString())
                PlaceholderContent.addItem(dList[i])
            }
//                        if (! rv.isComputingLayout())
//                        rvAdapter.submitList(PlaceholderContent.ITEMS)
            notifyDataChanged()
            Log.d("Pizza: ", "Adapter List Count:= " + rvAdapter.getItemCount().toString())
//                        rvAdapter.notifyDataSetChanged()
        }
    }
    external fun getPizzaListFromJNI(): ByteArray

    private fun getPizzaList() {
        val retrofit = RetrofitClient.getInstance()
        val apiInterface = retrofit.create(ApiInterface::class.java)
        GlobalScope.launch {
            try {
                val response = apiInterface.getAllPizza()
                if (response.isSuccessful()) {
                    //your code for handling success response
//                    Log.d("Pizza: ", response.body().toString())

                    val dList: List<DataPizza>? = response.body()?.data
                    if (dList != null) {
                        PlaceholderContent.clearAll()
//                        Log.d("Pizza: ", "Adapter List Count: " + rvAdapter.getItemCount().toString())
                        for (i in 0.. dList.size - 1) {
//                            Log.d("Pizza: ", "AdapterItem: " + dList[i].toString())
                            PlaceholderContent.addItem(dList[i])
                        }
//                        if (! rv.isComputingLayout())
//                        rvAdapter.submitList(PlaceholderContent.ITEMS)
                        notifyDataChanged()
                        Log.d("Pizza: ", "Adapter List Count:= " + rvAdapter.getItemCount().toString())
//                        rvAdapter.notifyDataSetChanged()
                    }


                } else {
                    Log.e("Error", response.errorBody().toString())
//                    Toast.makeText(
//                        this@PizzaListFragment.context,
//                        response.errorBody().toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
                }
            }catch (Ex:Exception){
                Ex.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }

    external fun deletePizzaFromJNI(id: String): Int

    fun deletePizza(d: DataPizza) {
        val retrofit = RetrofitClient.getInstance()
        val apiInterface = retrofit.create(ApiInterface::class.java)
        GlobalScope.launch {
            try {
                val id = PlaceholderContent.itemToId(d)
                val response = apiInterface.deletePizza(id)
                if (response.isSuccessful()) {
                    //your code for handling success response
                    Log.d("Pizza: ", response.body().toString())

//                    val rd: DataPizza? = response.body()?.data
//                    if (rd != null) {
////                        PlaceholderContent.removeItem(position)
//                    }

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

        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_EDIT_BACK = "edit-back"
        const val ARG_EDIT_POSITION = "edit-position"

        @JvmStatic
        fun newInstance(columnCount: Int, editBack: Int, editPosition: Int) =
            PizzaListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putInt(ARG_EDIT_BACK, editBack)
                    putInt(ARG_EDIT_POSITION, editPosition)
                }
            }
    }
}