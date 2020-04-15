import Chart from "chart.js";
import colors from "./colors";

const chartHelper = {
  createBarChart: (chartElement, datasets, labels, title = null) => {
    const chart = new Chart(chartElement, {
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
  createRecoveredDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('recovered', stats) : chartHelper.createLineDataSet('recovered', stats),
  createNewConfirmedDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('newConfirmed', stats) : chartHelper.createLineDataSet('newConfirmed', stats),
  createNewDeathsDataSet: (stats, type) => (type === 'bar') ? chartHelper.createBarDataSet('newDeaths', stats) : chartHelper.createLineDataSet('newDeaths', stats),
  createBarDataSet: (statsType, stats) => {
    return {
      label: statsType,
      data: stats.counts.map((count) => count[statsType]),
      backgroundColor: colors[statsType],
      borderColor: colors[`${statsType}Opaque`],
      borderWidth: 2,
      type: 'bar'
    };
  },
  createLineDataSet: (statsType, stats) => {
    return {
      label: statsType,
      data: stats.counts.map((count) => count[statsType]),
      backgroundColor: colors[statsType],
      borderColor: colors[`${statsType}Opaque`],
      borderWidth: 2,
      pointHitRadius: 20,
      lineTension: .3,
      fill: false,
      type: 'line'
    };
  },
  createOptions: (title = null) => {
    const titleOption = (title) ? { display: true, text: title } : { display: false };

    return {
      title: titleOption,
      responsive: true,
      lineTension: 1,
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: true,
          }
        }]
      }
    };
  }
};

export default chartHelper;
