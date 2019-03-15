package com.example.week4.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.week4.PlaceSearchModal.Prediction
import com.example.week4.PlaceSearchModal.Result
import com.example.week4.R
import com.example.week4.Service.APIClient
import com.example.week4.Service.MapAPI
import java.lang.Exception

@SuppressLint("ResourceType")
class PlacesAutocompleteAdapter(var con: Context, var arr: ArrayList<Prediction>?) : ArrayAdapter<Result>(con, 0) {
    private val LIMIT: Int = 5

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(con).inflate(R.layout.place_row_layout, null)
        if(arr != null && arr!!.size > 0){
            val prediction: Prediction = arr!![position]
            val textView: TextView = view.findViewById(R.id.place)
            textView.text = prediction.description
        }
        Log.d("getView", arr.toString())
        return view
    }

    override fun getCount(): Int {
        Log.d("getCount", arr!!.size.toString())
        return Math.min(LIMIT, arr!!.size)
    }

    override fun getFilter(): Filter {
        return PlaceAutocompleteFilter(this, con)
    }

    private class PlaceAutocompleteFilter(var placesAutocompleteAdapter: PlacesAutocompleteAdapter, var con: Context): Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            try {
                placesAutocompleteAdapter.arr!!.clear()
                val filterResult = FilterResults()
                if(constraint == null || constraint.isEmpty()){
                    filterResult.values = ArrayList<Prediction>()
                    filterResult.count = 0
                }
                else{
                    val mapAPI: MapAPI = APIClient().getClient().create(MapAPI::class.java)
                    val result: Result = mapAPI.getPlacesAutocomplete(constraint.toString(), con.getString(R.string.google_maps_key)).execute().body()!!
                    filterResult.values = result.predictions
                    filterResult.count = result.predictions.size
                }
                Log.d("values_filter", filterResult.values.toString())
                Log.d("count_filter", filterResult.count.toString())
                return filterResult
            }catch (e: Exception){
                return null
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            placesAutocompleteAdapter.arr!!.clear()
            placesAutocompleteAdapter.arr!!.addAll(results!!.values as Collection<Prediction>)
            Log.d("arr", placesAutocompleteAdapter.arr!!.toString())
            placesAutocompleteAdapter.notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            val prediction: Prediction = resultValue as Prediction
            return prediction.description
        }

    }
}