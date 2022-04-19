package no.kristiania.imagesearcherexam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_items.view.*


class RecylerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<RecyclerViewObjects> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_results, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder ->{
                holder.bind(items.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList (list: List<RecyclerViewObjects>){
        items = list
    }

    class ViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.image1
        val title : TextView = itemView.title

        fun bind(recyclerViewObjects: RecyclerViewObjects){

            title.setText(recyclerViewObjects.title)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(recyclerViewObjects.image)
                .into(image)
        }
    }
}