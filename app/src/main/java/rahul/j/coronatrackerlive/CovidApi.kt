package rahul.j.coronatrackerlive

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import kotlinx.parcelize.Parcelize
import retrofit2.http.Query

interface CovidApi {
    @GET("v1")
    fun getCovidData(): Call<List<CovidCases>>
}

interface StateDataAPI{
    @GET("stats")
    fun getStateData(@Query("country") country: String): Call<StateCases>
}

@Parcelize
data class CovidCases(
    @SerializedName("Country_text")
    var country: String?,
    @SerializedName("Last Update")
    var date: String,
    @SerializedName("New Cases_text")
    var newCases: String,
    @SerializedName("New Deaths_text")
    var newDeath: String,
    @SerializedName("Total Cases_text")
    var totalCase: String,
    @SerializedName("Total Deaths_text")
    var totalDeath: String,
    @SerializedName("Total Recovered_text")
    var recovery: String,
    @SerializedName("Active Cases_text")
    var activeCases: String,
) : Parcelable

@Parcelize
data class StateCases(
    @SerializedName("statusCode")
    var statusCode:Int,
    @SerializedName("data")
    var data:StateDate,
): Parcelable

@Parcelize
data class StateDate(
    @SerializedName("covid19Stats")
    var covidState:List<ItemData>
):Parcelable

@Parcelize
data class ItemData(
    @SerializedName("province")
    var state:String,
    @SerializedName("country")
    var country:String,
    @SerializedName("confirmed")
    var confirmed:String,
    @SerializedName("deaths")
    var death:String,
    @SerializedName("recovered")
    var recovered:String,
):Parcelable