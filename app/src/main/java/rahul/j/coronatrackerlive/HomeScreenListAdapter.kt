package rahul.j.coronatrackerlive

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeScreenListAdapter(private val covidDataList: List<CovidCases>,private val onItemClickListener: OnItemClickListener,private val context: Context) :
    RecyclerView.Adapter<HomeScreenListAdapter.HomeScreenViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(covidCase: CovidCases)
    }

    class HomeScreenViewHolder(view: View):RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.title)
        val totalCase: TextView = view.findViewById(R.id.active_case)
        val newCase:TextView = view.findViewById(R.id.new_cases)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeScreenViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_covid_list, parent, false)
        return HomeScreenViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeScreenViewHolder, position: Int) {
        val currentItem = covidDataList[position]
        holder.title.text = currentItem.country
        holder.totalCase.text = context.getString(R.string.total_cases,currentItem.totalCase)
        holder.newCase.text = context.getString(R.string.active_cases,currentItem.activeCases)
        holder.itemView.setOnClickListener{
            onItemClickListener.onItemClick(currentItem)
        }

    }
    override fun getItemCount(): Int {
        return covidDataList.size
    }
}