package rahul.j.coronatrackerlive

import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {

    private val detailVM: DetailViewModel by navGraphViewModels(R.id.home_nav)
    private lateinit var country:TextView
    private lateinit var lastUpdated:TextView
    private lateinit var totalCases:TextView
    private lateinit var activeCases:TextView
    private lateinit var totalRecovery:TextView
    private lateinit var totalDeath:TextView
    private lateinit var inlineErrorSnackBar: Snackbar
    private lateinit var covidList:RecyclerView
    private var isWorld:Boolean=false
    private lateinit var headingTV:TextView
    private lateinit var loading:ProgressBar
    private lateinit var covidCases: CovidCases

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.detail_fragment, container, false)
        initViews(view)
        covidCases = requireArguments().getParcelable<CovidCases>("Data") as CovidCases
        if(covidCases.country == "World"){
            isWorld = true
            headingTV.text = getString(R.string.country_detail)
        } else {
            isWorld = false
            headingTV.text = getString(R.string.state_detail)
        }
        detailVM.getStateWiseData(requireContext(),covidCases.country!!)
        detailVM.currentState.observe(viewLifecycleOwner, Observer(::reactToState))
        setText(covidCases)
        setDefaultToolbar(getString(R.string.detail_title,covidCases.country!!))
        setupOnBackPressedListener()
        return view
    }

    private fun reactToState(state: DetailedScreenState){
        when(state){
            is DetailedScreenState.Loading -> {
                handleLoadingState()
            }
            is DetailedScreenState.Data -> {
                handleDataState(state)
            }
            is DetailedScreenState.Error -> {
                handleErrorState(state.error)
            }
        }
    }

    private fun handleErrorState(error: String) {
        loading.visibility = View.GONE
        covidList.visibility = View.GONE
        inlineErrorSnackBar = Snackbar.make(requireView(), error, Snackbar.LENGTH_INDEFINITE)
        inlineErrorSnackBar.let {
            it.setAction(R.string.retry) {
                detailVM.getStateWiseData(requireContext(),covidCases.country!!)
            }
            it.show()
        }
    }

    private fun dismissSnackbar(){
        if(::inlineErrorSnackBar.isInitialized && inlineErrorSnackBar.isShown){
            inlineErrorSnackBar.dismiss()
        }
    }

    private fun handleLoadingState(){
        dismissSnackbar()
        covidList.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun handleDataState(state: DetailedScreenState.Data) {
        loading.visibility = View.GONE
        covidList.visibility = View.VISIBLE
        dismissSnackbar()
        val itemDecor = DividerItemDecoration(requireContext(), ClipDrawable.HORIZONTAL)
        val data=state.data.data.covidState
        covidList.adapter = DetailedScreenListAdapter(data,requireContext(),isWorld)
        covidList.addItemDecoration(itemDecor)
    }

    private fun setupOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
    }

    private fun initViews(view: View) {
        country = view.findViewById(R.id.country_name)
        lastUpdated = view.findViewById(R.id.last_updated)
        totalCases = view.findViewById(R.id.total_cases)
        activeCases = view.findViewById(R.id.active_case)
        totalRecovery = view.findViewById(R.id.total_recovery)
        totalDeath = view.findViewById(R.id.total_death)
        covidList = view.findViewById(R.id.covid_list)
        headingTV = view.findViewById(R.id.heading)
        loading = view.findViewById(R.id.loading)
    }

    private fun setText(covidCases: CovidCases){
        country.text = covidCases.country
        lastUpdated.text = getString(R.string.last_updated,covidCases.date)
        totalCases.text = getString(R.string.total_cases,getNumber(covidCases.totalCase))
        activeCases.text = getString(R.string.active_cases,getNumber(covidCases.activeCases))
        totalRecovery.text = getString(R.string.total_recovery,getNumber(covidCases.recovery))
        totalDeath.text = getString(R.string.total_death,getNumber(covidCases.totalDeath))
    }

    private fun getNumber(number: String):String{
        return if(number.isBlank()){
            "0"
        } else{
            number
        }
    }

    override fun onStop() {
        super.onStop()
        dismissSnackbar()
    }

}