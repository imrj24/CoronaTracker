package rahul.j.coronatrackerlive

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailViewModel : ViewModel() {

    private val vmIOScope = viewModelScope + Dispatchers.IO
    private val _currentState: MutableLiveData<DetailedScreenState> = MutableLiveData(DetailedScreenState.Loading)
    val currentState: LiveData<DetailedScreenState> = _currentState

    fun getStateWiseData(context: Context, country: String){
        if(isConnected(context)){
            _currentState.postValue(DetailedScreenState.Loading)
            vmIOScope.launch {
                val covidApi=getCovidAPI()
                val call = covidApi.getStateData(country)
                call.enqueue(object : Callback<StateCases> {

                    override fun onResponse(
                        call: Call<StateCases>,
                        response: Response<StateCases>
                    ) {
                        if(response.isSuccessful && response.body()!=null){
                            val data = response.body()!!
                            _currentState.postValue(DetailedScreenState.Data(data))
                        }else{
                            _currentState.postValue(DetailedScreenState.Error(context.getString(R.string.failure_unknown_description)))
                        }
                    }

                    override fun onFailure(call: Call<StateCases>, t: Throwable) {
                        _currentState.postValue(DetailedScreenState.Error(context.getString(R.string.failure_unknown_description)))
                    }
                }

                )
            }
        } else {
            _currentState.postValue(DetailedScreenState.Error(context.getString(R.string.no_internet_exception_message)))
        }
    }

    private fun getCovidAPI(): StateDataAPI {
        val httpClient = getHttpClient()

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return retrofitBuilder.create(
            StateDataAPI::class.java
        )
    }

    private fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("x-rapidapi-key", "0967b98c08msh4ce3bbc8ffc33bfp175521jsnbdb3b996e87a")
                    builder.header("x-rapidapi-host", "covid-19-coronavirus-statistics.p.rapidapi.com")
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
}
sealed class DetailedScreenState{
    object Loading: DetailedScreenState()
    data class Data(val data: StateCases):DetailedScreenState()
    data class Error(val error:String):DetailedScreenState()
}