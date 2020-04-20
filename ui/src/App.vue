<template>
  <div id="app">
    <div class="chart">
      <canvas id="covid-chart-county-new-confirmed"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-county-confirmed"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-county-new-deaths"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-county-deaths"></canvas>
    </div>

    <div class="chart">
      <canvas id="covid-chart-state-new-confirmed"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-state-confirmed"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-state-new-deaths"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-state-deaths"></canvas>
    </div>

  </div>
</template>

<script>
import Chart from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import chartHelper from "./chartHelper";
import dataHelper from "./dataHelper";
import colors from "./colors";
import axios from 'axios';
import pattern from 'patternomaly';

//const BASE_URL = 'https://s3.us-east-2.amazonaws.com/akang.info/covid';
const BASE_URL = 'public';

export default {
  name: 'app',
  data() {
    return {
      usData: {},
      state: 'Illinois',
      county: 'Cook',
    }
  },
  mounted() {
    axios.get(`${BASE_URL}/illinois_data.json`).then(response => {
      this.usData = response.data['usStats'];
      this.usData = dataHelper.cleanseData(this.usData);

      let stats = dataHelper.getUSStats(this.usData, this.state, this.county);
      this.createChart(
        'covid-chart-county-new-confirmed',
        stats,
        [dataHelper.createNewConfirmedDataSet(stats, 'line')],
        `${this.county} county confirmed new`,
      );
      this.createChart(
        'covid-chart-county-confirmed',
        stats,
        [dataHelper.createConfirmedDataSet(stats, 'line')],
        `${this.county} county confirmed all`,
      );
      this.createChart(
        'covid-chart-county-new-deaths',
        stats,
        [dataHelper.createNewDeathsDataSet(stats, 'line')],
        `${this.county} county deaths new`,
      );
      this.createChart(
        'covid-chart-county-deaths',
        stats,
        [dataHelper.createDeathsDataSet(stats, 'line')],
        `${this.county} county deaths all`,
      );

      stats = dataHelper.getUSStats(this.usData, this.state);
      this.createChart(
        'covid-chart-state-new-confirmed',
        stats,
        [dataHelper.createNewConfirmedDataSet(stats, 'line')],
        `${this.state} confirmed new`,
      );
      this.createChart(
        'covid-chart-state-confirmed',
        stats,
        [dataHelper.createConfirmedDataSet(stats, 'line')],
        `${this.state} confirmed all`,
      );
      this.createChart(
        'covid-chart-state-new-deaths',
        stats,
        [dataHelper.createNewDeathsDataSet(stats, 'line')],
        `${this.state} deaths new`,
      );
      this.createChart(
        'covid-chart-state-deaths',
        stats,
        [dataHelper.createDeathsDataSet(stats, 'line')],
        `${this.state} deaths all`,
      );
    });
  },
  methods: {
    createChart: (elementName, stats, dataSets, title) => {
      chartHelper.createChart(
        document.getElementById(elementName),
        dataSets,
        stats.counts.map((count) => count.date),
        title,
      );
    },
  }
}

</script>

<style lang="scss">
  .chart {
    position: relative;
    display: inline-block;
    width: 49%;
  }
</style>
