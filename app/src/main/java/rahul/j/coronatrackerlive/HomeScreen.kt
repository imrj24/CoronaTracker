package rahul.j.coronatrackerlive

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar


class HomeScreen : Fragment(), HomeScreenListAdapter.OnItemClickListener {

    private val homeScreenVM: HomeScreenVM by navGraphViewModels(R.id.home_nav)

    private lateinit var covidRV:RecyclerView
    private lateinit var loadingPB:ProgressBar
    private lateinit var layoutErrorStateView: View
    private lateinit var inlineErrorSnackBar: Snackbar
    private var searchET: EditText? = null
    private lateinit var data:List<CovidCases>
    private val listener:HomeScreenListAdapter.OnItemClickListener=this
    private lateinit var swipeToRefreshView: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        initViews(view)
        setToolBar()
        homeScreenVM.currentState.observe(viewLifecycleOwner, Observer(::reactToState))
        return view
    }

    private fun setToolBar(){
        val activity = requireActivity() as ToolbarSettable
        val toolbarView = layoutInflater.inflate(R.layout.search_toolbar, activity.toolbar, false)
        activity.toolbar.apply {
            visibility = View.VISIBLE
            removeAllViews()
            addView(toolbarView)

            searchET = toolbarView.findViewById(R.id.search_et)
        }
        searchET!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do Nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotBlank()) {
                    if (::data.isInitialized) {
                        val a = "$s".lowercase()
                        val filteredData = data.filter {
                            it.country!!.contains(a,true)
                        }
                        covidRV.adapter =
                            HomeScreenListAdapter(filteredData, listener, requireContext())
                    }
                } else {
                    covidRV.adapter =
                        HomeScreenListAdapter(data, listener, requireContext())
                }
            }
        })
    }

    private fun reactToState(state: HomeScreenState){
        when(state){
            is HomeScreenState.Loading -> {
                handleLoadingState()
            }
            is HomeScreenState.Data -> {
                handleDataState(state.data)
            }
            is HomeScreenState.Error -> {
                handleErrorState(state.error)
            }
        }
    }

    private fun handleLoadingState(){
        loadingPB.visibility = View.VISIBLE
        covidRV.visibility = View.GONE
        layoutErrorStateView.visibility = View.GONE
        dismissSnackbar()
    }

    private fun handleDataState(data: List<CovidCases>?) {
        dismissSnackbar()
        loadingPB.visibility = View.GONE
        layoutErrorStateView.visibility = View.GONE
        covidRV.visibility = View.VISIBLE
        val itemDecor = DividerItemDecoration(requireContext(), ClipDrawable.HORIZONTAL)
        if (data != null) {
            this.data=data
         covidRV.adapter = HomeScreenListAdapter(data,this,requireContext())
            covidRV.addItemDecoration(itemDecor)
        }
    }

    private fun handleErrorState(error: String) {
        inlineErrorSnackBar = Snackbar.make(requireView(), error, Snackbar.LENGTH_INDEFINITE)
        inlineErrorSnackBar.let {
            it.setAction(R.string.retry) {
                homeScreenVM.getCovidData(requireContext())
            }
            it.show()
        }
    }

    private fun dismissSnackbar(){
        if(::inlineErrorSnackBar.isInitialized && inlineErrorSnackBar.isShown){
            inlineErrorSnackBar.dismiss()
        }
    }

    private fun initViews(view: View){
        covidRV=view.findViewById(R.id.covid_list)
        loadingPB = view.findViewById(R.id.loading)
        layoutErrorStateView = view.findViewById(R.id.layout_error)
        swipeToRefreshView = view.findViewById(R.id.swipe_to_refresh)
        swipeToRefreshView.setColorSchemeResources(R.color.color_primary)
        swipeToRefreshView.setOnRefreshListener {
            homeScreenVM.pullTorefresh(requireContext())
            swipeToRefreshView.isRefreshing = false
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeScreenVM.getCovidData(requireContext())
    }

    override fun onStop() {
        super.onStop()
        dismissSnackbar()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView: View? = requireActivity().currentFocus

        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    override fun onItemClick(covidCase: CovidCases) {
        homeScreenVM.onItemClicked(findNavController(),covidCase)
    }
}