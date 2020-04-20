import colors from "./colors";
import chartHelper from "./chartHelper";

const dataHelper = {
  /**
   * Remove year from dates
   *
   * @param data
   * @returns {{usStats: *}}
   */
  cleanseData: (data) => {
    return data.map((stat) => {
      let counts = stat.counts.map((count) => {
        count.date = count.date.substring(5);
        return count;
      });
      return stat;
    });
  },
  createStateFilter: (state) => {
    return (stat) => stat['provinceOrState'] === state;
  },
  createCountyFilter: (state, county) => {
    return (stat) => stat['provinceOrState'] === state && stat['county'] === county;
  },
  getUSStats: (usData, state = null, county = null, numberOfDays = 21) => {
    const numDays = Math.max(usData[0].counts.length - numberOfDays, 1)

    const filter =
      (state && county) ? dataHelper.createCountyFilter(state, county) :
        (state) ? dataHelper.createStateFilter(state) :
          () => true;

    const firstStat = usData.find(filter);
    const initialCounts = firstStat['counts'].slice(numDays).map((stat) => {
      const count = { date: stat.date, confirmed: 0, newConfirmed: 0, deaths: 0, newDeaths: 0 };
      return count;
    });

    const stats = usData
      .filter(filter)
      .reduce((acc, stat) => {
        acc.population += stat.population;

        const counts = stat.counts.slice(numDays);

        for (let i = 0; i < counts.length; i++) {
          acc.counts[i].confirmed += counts[i].confirmed;
          acc.counts[i].newConfirmed += counts[i].newConfirmed;
          acc.counts[i].deaths += counts[i].deaths;
          acc.counts[i].newDeaths += counts[i].newDeaths;
        }

        return acc;
      }, { population: 0, counts: initialCounts });

    return stats;
  },
  createConfirmedDataSet: (stats, type) => chartHelper.createConfirmedDataSet(stats, type),
  createNewConfirmedDataSet: (stats, type) => chartHelper.createNewConfirmedDataSet(stats, type),
  createNewDeathsDataSet: (stats, type) => chartHelper.createNewDeathsDataSet(stats, type),
  createDeathsDataSet:    (stats, type) => chartHelper.createDeathsDataSet(stats, type),
  createTitle: (state = null, county = null) => {
    let title =
      (state && county) ? `${county} county, ${state}` :
        (state) ? `${state} ` :
          'US ';
    title += ' covid stats';

    return title;
  }
};

export default dataHelper;
