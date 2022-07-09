package info.akang.covid

import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StatsTransformer {
    companion object {
    }

    fun transformGlobalStats(confirmedData: String, deathsData: String): GlobalStats {
        val mapOfCounts = createMapOfCounts(confirmedData, GlobalStatHelper())
        addCountTypeToMap(CountType.CONFIRMED, confirmedData, mapOfCounts, GlobalStatHelper())
        addCountTypeToMap(CountType.DEATHS, deathsData, mapOfCounts, GlobalStatHelper())

        val stats = mapOfCounts.map { entry ->
            entry.key.copy(counts = entry.value)
        }

        return GlobalStats(stats)
    }

    fun transformUSStats(confirmedData: String, deathsData: String): USStats {
        val mapOfCounts = createMapOfCounts(deathsData, USDeathStatHelper())     // Only the deaths data has population
        addCountTypeToMap(CountType.CONFIRMED, confirmedData, mapOfCounts, USConfirmedStatHelper())
        addCountTypeToMap(CountType.DEATHS, deathsData, mapOfCounts, USDeathStatHelper())

        val stats = mapOfCounts.map { entry ->
            entry.key.copy(counts = entry.value)
        }

        return USStats(stats)
    }

    private fun <T> addCountTypeToMap(countType: CountType, data: String, mapOfCounts: MutableMap<T, List<DateCounts>>,
                                      statHelper: StatHelper<T>) {
        val lines = data.split("\n")
        lines.drop(1).filter { it.isNotEmpty() }.forEach { line ->
            val values = line.split(TransformerConstants.SPLIT_ON_COMMAS_NOT_WITHIN_QUOTES, 0)
            val stat = statHelper.create(values)

            val counts = mapOfCounts[stat]

            if (counts == null) {
                println("Not adding due to mismatch - expecting all files to have some keys" +
                        "- countType[$countType] $stat because mismatch")
            } else {
                var lastCount = 0

                counts.withIndex().forEach { count ->
                    val num = values[count.index + statHelper.getDateStartIndex()].toInt()

                    when (countType) {
                        CountType.CONFIRMED -> {
                            count.value.c = num
                            count.value.nc = num - lastCount
                        }
                        CountType.DEATHS -> {
                            count.value.d = num
                            count.value.nd = num - lastCount
                        }
                    }

                    lastCount = num
                }
            }
        }
    }

    private fun <T> createMapOfCounts(data: String, statHelper: StatHelper<T>)
            : MutableMap<T, List<DateCounts>> {
        val mapOfCounts: MutableMap<T, List<DateCounts>> = mutableMapOf()

        val lines = data.split("\n")
        val header = lines[0]

        val dates = header
                .split(TransformerConstants.SPLIT_ON_COMMAS_NOT_WITHIN_QUOTES, 0)
                .drop(statHelper.getDateStartIndex())
                .map { dateString -> LocalDate.parse(dateString, TransformerConstants.DATE_FORMATTER) }

        lines.drop(1).filter { it.isNotEmpty() }.forEach { line ->
            val values = line.split(TransformerConstants.SPLIT_ON_COMMAS_NOT_WITHIN_QUOTES, 0)
            val counts = dates.withIndex().map { DateCounts(dt = it.value) }
            val stat = statHelper.create(values)
            mapOfCounts[stat] = counts
        }

        return mapOfCounts
    }
}

interface StatHelper<T> {
    fun create(values: List<String>): T
    fun getDateStartIndex(): Int
}

class GlobalStatHelper: StatHelper<GlobalStat> {
    override fun create(values: List<String>): GlobalStat {
        return GlobalStat(
                countryOrRegion = values[1],
                provinceOrState = values[0],
                latitude = if (values[2].trim() != "") values[2].trim().toFloat() else null,
                longitude = if (values[3].trim() != "") values[3].trim().toFloat() else null
        )
    }

    override fun getDateStartIndex(): Int = 4
}

class USConfirmedStatHelper: StatHelper<USStat> {
    override fun create(values: List<String>): USStat {
        return USStat(
                uid = values[0].toLong(),
                iso2 = values[1],
                iso3 = values[2],
                code3 = values[3],
                fips = values[4],
                county = values[5],
                countryOrRegion = values[7],
                provinceOrState = values[6],
                latitude = if (values[8].trim() != "") values[8].trim().toFloat() else null,
                longitude = if (values[9].trim() != "") values[9].trim().toFloat() else null
        )
    }

    override fun getDateStartIndex(): Int = 11
}

class USDeathStatHelper: StatHelper<USStat> {
    override fun create(values: List<String>): USStat {
        return USStat(
                uid = values[0].toLong(),
                iso2 = values[1],
                iso3 = values[2],
                code3 = values[3],
                fips = values[4],
                county = values[5],
                countryOrRegion = values[7],
                provinceOrState = values[6],
                latitude = if (values[8].trim() != "") values[8].trim().toFloat() else null,
                longitude = if (values[9].trim() != "") values[9].trim().toFloat() else null,
                population = values[11].toInt()
        )
    }

    override fun getDateStartIndex(): Int = 12
}


object TransformerConstants {
    val SPLIT_ON_COMMAS_NOT_WITHIN_QUOTES = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yy")
}

data class GlobalStats(
        val globalStats: List<GlobalStat>
)

data class GlobalStat(
        val countryOrRegion: String,
        val provinceOrState: String,
        val latitude: Float?,
        val longitude: Float?,
        val counts: List<DateCounts> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GlobalStat

        if (countryOrRegion != other.countryOrRegion) return false
        if (provinceOrState != other.provinceOrState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = countryOrRegion.hashCode()
        result = 31 * result + provinceOrState.hashCode()
        return result
    }
}

data class USStats(
        val usStats: List<USStat>
)

data class USStat(
        val uid: Long,
        val iso2: String,
        val iso3: String,
        val code3: String,
        val fips: String,
        val population: Int = 0,
        val county: String,
        val countryOrRegion: String,
        val provinceOrState: String,
        val latitude: Float?,
        val longitude: Float?,
        val counts: List<DateCounts> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as USStat

        if (uid != other.uid) return false
        if (county != other.county) return false
        if (countryOrRegion != other.countryOrRegion) return false
        if (provinceOrState != other.provinceOrState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + county.hashCode()
        result = 31 * result + countryOrRegion.hashCode()
        result = 31 * result + provinceOrState.hashCode()
        return result
    }
}

enum class CountType(val value: String) { CONFIRMED("confirmed"), DEATHS("deaths") }

data class DateCounts(
    val dt: LocalDate,      // date
    var c: Int = 0,         // confirmed
    var nc: Int = 0,        // new confirmed
    var d: Int = 0,         // deaths
    var nd: Int = 0         // new deaths
)

