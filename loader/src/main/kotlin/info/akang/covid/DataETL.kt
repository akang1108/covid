package info.akang.covid

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.net.URL

class DataETL {

    fun run() {
        val usAndGlobalData = getHttpData()
        runGlobalStats(usAndGlobalData)
        runUSStats(usAndGlobalData)
    }

    private fun runGlobalStats(usAndGlobalData: USAndGlobalData) {
        val stats = StatsTransformer().transformGlobalStats(usAndGlobalData.confirmedGlobal, usAndGlobalData.deathsGlobal)
        val mapper = createObjectMapper()
        val jsonString = mapper.writeValueAsString(stats)
        File("$OUTPUT_DIR/global_data.json").writeText(jsonString)
    }

    private fun runUSStats(usAndGlobalData: USAndGlobalData) {
        val stats = StatsTransformer().transformUSStats(usAndGlobalData.confirmedUS, usAndGlobalData.deathsUS)
        val mapper = createObjectMapper()
        val jsonString = mapper.writeValueAsString(stats)
        File("$OUTPUT_DIR/us_data.json").writeText(jsonString)

        val illinoisStats = stats.copy(usStats = stats.usStats.filter { stat ->
            stat.provinceOrState == "Illinois"
        })
        val illinoisJsonString = mapper.writeValueAsString(illinoisStats)
        File("$OUTPUT_DIR/illinois_data.json").writeText(illinoisJsonString)
    }

    private fun createObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        return mapper
    }

    private fun getFileData(): USAndGlobalData {
        val retriever = FileRetriever()
        return USAndGlobalData(
                confirmedUS = retriever.retrieve(CONFIRMED_US_PATH),
                deathsUS = retriever.retrieve(DEATHS_US_PATH),
                confirmedGlobal = retriever.retrieve(CONFIRMED_GLOBAL_PATH),
                deathsGlobal = retriever.retrieve(DEATHS_GLOBAL_PATH)
        )
    }

    private fun getHttpData(): USAndGlobalData {
        val retriever = HttpRetriever()
        return USAndGlobalData(
                confirmedUS = retriever.retrieve(CONFIRMED_US_URL),
                deathsUS = retriever.retrieve(DEATHS_US_URL),
                confirmedGlobal = retriever.retrieve(CONFIRMED_GLOBAL_URL),
                deathsGlobal = retriever.retrieve(DEATHS_GLOBAL_URL)
        )
    }

    companion object {
        val CONFIRMED_US_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv")
        val DEATHS_US_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_US.csv")
        val CONFIRMED_GLOBAL_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv")
        val DEATHS_GLOBAL_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv")

        const val CONFIRMED_US_PATH = "/confirmed_us.csv"
        const val DEATHS_US_PATH = "/deaths_us.csv"
        const val CONFIRMED_GLOBAL_PATH = "/confirmed_global.csv"
        const val DEATHS_GLOBAL_PATH = "/deaths_global.csv"

        const val OUTPUT_DIR = "../ui/public"
    }
}

data class USAndGlobalData(
    val confirmedUS: String,
    val deathsUS: String,
    val confirmedGlobal: String,
    val deathsGlobal: String
)

fun main(args: Array<String>) {
    DataETL().run()
}



