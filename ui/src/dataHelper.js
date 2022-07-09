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
        count.dt = count.dt.substring(5);
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
  getUSStats: (usData, state = null, county = null, numberOfWeeks = 3) => {
    const numDays = Math.max(usData[0].counts.length - (7 * numberOfWeeks), 1);
    const filter =
      (state && county) ? dataHelper.createCountyFilter(state, county) :
        (state) ? dataHelper.createStateFilter(state) :
          () => true;

    const firstStat = usData.find(filter);
    const initialCounts = firstStat['counts'].slice(numDays).map((stat) => {
      const count = { dt: stat.dt, c: 0, nc: 0, d: 0, nd: 0 };
      return count;
    });

    const stats = usData
      .filter(filter)
      .reduce((acc, stat) => {
        acc.population += stat.population;

        const counts = stat.counts.slice(numDays);

        for (let i = 0; i < counts.length; i++) {
          acc.counts[i].c += counts[i].c;
          acc.counts[i].nc += counts[i].nc;
          acc.counts[i].d += counts[i].d;
          acc.counts[i].nd += counts[i].nd;
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
