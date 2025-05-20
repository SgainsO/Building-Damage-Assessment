package com.example.drone.ui.dashboard
import androidx.fragment.app.commit
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drone.R
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class RecycleAdapter(private val frag: Fragment, private val dataSet: Array<Int>) :
    RecyclerView.Adapter<RecycleAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageHolder: ImageView = view.findViewById(R.id.imContent)
        init {
            imageHolder.setOnClickListener {
                Log.d("ADAPTER CLICKER", "CONFIRMED")
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_item_view, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

     //   viewHolder.imageHolder.setImageResource(dataSet[position])
        viewHolder.imageHolder.setImageResource(R.drawable.oblique_test)

        viewHolder.imageHolder.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("imageId", position)
                putInt("pictureNumber", R.drawable.oblique_test)
            }
            frag.requireView().findNavController().navigate(
                R.id.action_navigation_dashboard_to_navigation_selector,
                bundle)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}