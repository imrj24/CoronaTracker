package rahul.j.coronatrackerlive

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

class HomeScreenVM :ViewModel(){
    private val vmIOScope = viewModelScope + Dispatchers.IO
    private val _currentState: MutableLiveData<HomeScreenState> = MutableLiveData(HomeScreenState.Loading)
    val currentState: LiveData<HomeScreenState> = _currentState
    private var job: Job? = null
    private var dataVal:List<CovidCases>? = null

    fun getCovidData(context: Context){
        if(isConnected(context)) {
            _currentState.postValue(HomeScreenState.Loading)
            if(job == null) {
                job = vmIOScope.launch {
                    val covidApi = getCovidAPI()
                    val call = covidApi.getCovidData()
                    call.enqueue(object : Callback<List<CovidCases>> {

                        override fun onResponse(
                            call: Call<List<CovidCases>>,
                            response: Response<List<CovidCases>>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val data = response.body()!!.filter {
                                    it.country != null
                                }
                                dataVal=data
                                _currentState.postValue(HomeScreenState.Data(data))
                            } else {
                                _currentState.postValue(HomeScreenState.Error(context.getString(R.string.failure_unknown_description)))
                            }
                        }

                        override fun onFailure(call: Call<List<CovidCases>>, t: Throwable) {
                            _currentState.postValue(HomeScreenState.Error(context.getString(R.string.failure_unknown_description)))
                        }
                    }

                    )
                }
            } else{
                _currentState.postValue(HomeScreenState.Data(dataVal))
            }
        } else {
            job = null
            dataVal=null
            _currentState.postValue(HomeScreenState.Error(context.getString(R.string.no_internet_exception_message)))
        }
    }

    fun pullTorefresh(context: Context){
        job = null
        getCovidData(context)
    }

    private fun getCovidAPI(): CovidApi {
        val httpClient = getHttpClient()

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://covid-19-tracking.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return retrofitBuilder.create(
            CovidApi::class.java
        )
    }
    private fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("x-rapidapi-key", "0967b98c08msh4ce3bbc8ffc33bfp175521jsnbdb3b996e87a")
                    builder.header("x-rapidapi-host", "covid-19-tracking.p.rapidapi.com")
                    return@Interceptor chain.proceed(builder.build())
                }
            )
        }.build()
    }

    private fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        return activeNetwork != null
    }

    fun onItemClicked(navController: NavController, covidCase: CovidCases){
        val args = Bundle()
        args.putParcelable(
            "Data",
            covidCase
        )
        navController.navigate(R.id.action_homeScreen_to_detailFragment, args)
    }
}
sealed class HomeScreenState{
    object Loading:HomeScreenState()
    data class Data(val data: List<CovidCases>?):HomeScreenState()
    data class Error(val error:String):HomeScreenState()
}
