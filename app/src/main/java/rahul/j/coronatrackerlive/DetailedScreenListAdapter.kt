package rahul.j.coronatrackerlive

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DetailedScreenListAdapter(
    private val stateCases: List<ItemData>,
    private val context: Context,
    private val isWorld: Boolean
) :
    RecyclerView.Adapter<DetailedScreenListAdapter.DetailedScreenViewHolder>() {

    class DetailedScreenViewHolder(view: View):RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.title)
        val acticeCases: TextView = view.findViewById(R.id.active_case)
        val recovered: TextView = view.findViewById(R.id.recovered)
        val death: TextView = view.findViewById(R.id.death)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailedScreenViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_state_list, parent, false)
        return DetailedScreenViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DetailedScreenViewHolder,
        position: Int
    ) {
        val currentItem = stateCases[position]
        holder.title.text = currentItem.state
        if(holder.title.text == ""){
            holder.title.text = currentItem.country
        }
        holder.acticeCases.text = context.getString(R.string.total_cases,currentItem.confirmed)
        holder.death.text = context.getString(R.string.total_death,currentItem.death)
        holder.recovered.text = context.getString(R.string.total_recovery,currentItem.recovered)
    }

    override fun getItemCount(): Int {
        return stateCases.size
    }
}