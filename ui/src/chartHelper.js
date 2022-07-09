import Chart from "chart.js";
import ChartDataLabels from 'chartjs-plugin-datalabels';
import colors from "./colors";

const chartHelper = {
  createChart: (chartElement, datasets, labels, title = null) => {
    const chart = new Chart(chartElement, {
      plugins: [ChartDataLabels],
      type: 'bar',
      data: {
        datasets: datasets,
        labels: labels
      },
      options: chartHelper.createOptions(title)
    });

    return chart;
  },
  createConfirmedDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('confirmed', stats) : chartHelper.createLineDataSet('confirmed', stats),
  createDeathsDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('deaths', stats) : chartHelper.createLineDataSet('deaths', stats),
  createNewConfirmedDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('newConfirmed', stats) : chartHelper.createLineDataSet('newConfirmed', stats),
  createNewDeathsDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('newDeaths', stats) : chartHelper.createLineDataSet('newDeaths', stats),
  createBarDataSet: (statsType, stats) => {
    return {
      label: statsType,
      data: stats.counts.map((count) => count[chartHelper.mapStatsType(statsType)]),
      backgroundColor: colors[statsType],
      borderColor: colors[`${statsType}Opaque`],
      borderWidth: 2,
      type: 'bar',
      datalabels: {
        color: colors[`${statsType}Opaque`]
      },
    };
  },
  createLabel: (statsType) => {
    if (statsType === 'newDeaths') {
      return 'deaths';
    } else if (statsType === 'deaths') {
      return 'accumulated deaths';
    } else if (statsType === 'newConfirmed') {
      return 'confirmed';
    } else if (statsType === 'confirmed') {
      return 'accumulated confirmed';
    }
  },
  mapStatsType: (statsType) => {
    if (statsType === 'newDeaths') {
      return 'nd';
    } else if (statsType === 'deaths') {
      return 'd';
    } else if (statsType === 'newConfirmed') {
      return 'nc';
    } else if (statsType === 'confirmed') {
      return 'c';
    }
  },
  createLineDataSet: (statsType, stats) => {
    return {
      label: chartHelper.createLabel(statsType),
      data: stats.counts.map((count) => count[chartHelper.mapStatsType(statsType)]),
      backgroundColor: colors[statsType],
      borderColor: colors[`${statsType}Opaque`],
      borderWidth: 2,
      pointHitRadius: 20,
      lineTension: .3,
      fill: false,
      type: 'line',
      datalabels: {
        borderRadius: 3,
        borderWidth: 1,
        padding: 2,
        offset: 5,
        borderColor: colors[statsType],
        color: colors[`${statsType}Opaque`],
        // display: (context) => {
        //   const index = context.dataIndex;
        //   const lastIndex = context.dataset.data.length - 1;
        //   const numToShow = 3;
        //   const interval = Math.round(lastIndex / numToShow);
        //
        //   console.info('interval' + interval);
        //   console.info('diff' + (lastIndex - index));
        //   console.info((lastIndex - index) % interval);
        //
        //   return ((lastIndex - index) % interval === 0);
        // },
        display: 'auto',
        align: 'top',
        rotation: 0,
      },
    };
  },
  createOptions: (title = null) => {
    const titleOption = (title) ?
      { display: true, text: title, fontSize: 20 } :
      { display: false };

    return {
      title: titleOption,
      responsive: true,
      aspectRatio: 3,
      lineTension: 1,
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: true,
          }
        }]
      },
      plugins: {
        datalabels: {
          display: false,
        }
      },
      legend: {
        display: false,
      },
    };
  }
};

export default chartHelper;
