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
        val globalStats = StatsTransformer().transformGlobalStats(usAndGlobalData.confirmedGlobal, usAndGlobalData.deathsGlobal, usAndGlobalData.recoveredGlobal)
        val mapper = createObjectMapper()
        val jsonString = mapper.writeValueAsString(globalStats)
        File("global_data.json").writeText(jsonString)
    }

    private fun runUSStats(usAndGlobalData: USAndGlobalData) {
        val globalStats = StatsTransformer().transformUSStats(usAndGlobalData.confirmedUS, usAndGlobalData.deathsUS)
        val mapper = createObjectMapper()
        val jsonString = mapper.writeValueAsString(globalStats)
        File("us_data.json").writeText(jsonString)
    }

    private fun createObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        return mapper
    }

    private fun getFileData(): USAndGlobalData {
        val retriever = FileRetriever()
        return USAndGlobalData(
                confirmedUS = retriever.retrieve(CONFIRMED_US_PATH),
                deathsUS = retriever.retrieve(DEATHS_US_PATH),
                confirmedGlobal = retriever.retrieve(CONFIRMED_GLOBAL_PATH),
                deathsGlobal = retriever.retrieve(DEATHS_GLOBAL_PATH),
                recoveredGlobal = retriever.retrieve(RECOVERED_GLOBAL_PATH)
        )
    }

    private fun getHttpData(): USAndGlobalData {
        val retriever = HttpRetriever()
        return USAndGlobalData(
                confirmedUS = retriever.retrieve(CONFIRMED_US_URL),
                deathsUS = retriever.retrieve(DEATHS_US_URL),
                confirmedGlobal = retriever.retrieve(CONFIRMED_GLOBAL_URL),
                deathsGlobal = retriever.retrieve(DEATHS_GLOBAL_URL),
                recoveredGlobal = retriever.retrieve(RECOVERED_GLOBAL_URL)
        )
    }

    companion object {
        val CONFIRMED_US_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv")
        val DEATHS_US_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_US.csv")
        val CONFIRMED_GLOBAL_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv")
        val DEATHS_GLOBAL_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv")
        val RECOVERED_GLOBAL_URL = URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv")

        const val CONFIRMED_US_PATH = "/confirmed_us.csv"
        const val DEATHS_US_PATH = "/deaths_us.csv"
        const val CONFIRMED_GLOBAL_PATH = "/confirmed_global.csv"
        const val DEATHS_GLOBAL_PATH = "/deaths_global.csv"
        const val RECOVERED_GLOBAL_PATH = "/recovered_global.csv"
    }
}

data class USAndGlobalData(
    val confirmedUS: String,
    val deathsUS: String,
    val confirmedGlobal: String,
    val deathsGlobal: String,
    val recoveredGlobal: String
)

fun main(args: Array<String>) {
    DataETL().run()
}



