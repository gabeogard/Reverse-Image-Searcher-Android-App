package no.kristiania.imagesearcherexam

import android.app.Dialog
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlin.random.Random

class ResultItemAdapter(
    private val items: List<ResultItem>,
    private val updateListener: (view: ImageView, title: String) -> Unit
) :
    RecyclerView.Adapter<ResultItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain: LinearLayout = view.findViewById(R.id.llMain)
        val ivSearched: ImageView = view.findViewById(R.id.ivSearched)
        val ivResultOne: ImageView = view.findViewById(R.id.ivResultOne)
        val ivResultTwo: ImageView = view.findViewById(R.id.ivResultTwo)
        val ivResultThree: ImageView = view.findViewById(R.id.ivResultThree)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivSearched.setImageBitmap(items[position].searchedItem)
        holder.ivResultOne.setImageBitmap(items[position].resultOne)
        holder.ivResultTwo.setImageBitmap(items[position].resultTwo)
        holder.ivResultThree.setImageBitmap(items[position].resultThree)



        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.salmon
                )
            )
        }

        holder.ivSearched.setOnClickListener {
            updateListener.invoke(it as ImageView, "Searched Image")
        }
        holder.ivResultOne.setOnClickListener {
            updateListener.invoke(it as ImageView, "Result #1")
        }
        holder.ivResultTwo.setOnClickListener {
            updateListener.invoke(it as ImageView, "Result #2")
        }
        holder.ivResultThree.setOnClickListener {
            updateListener.invoke(it as ImageView, "Result #3")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}