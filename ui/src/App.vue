<template>
  <div id="app">
    <div class="chart">
      <canvas id="covid-chart-county"></canvas>
    </div>
    <div class="chart">
      <canvas class="chart" id="covid-chart-state"></canvas>
    </div>
    <div class="chart">
      <canvas class="chart" id="covid-chart-country"></canvas>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js';
import chartHelper from "./chartHelper";
import dataHelper from "./dataHelper";
import colors from "./colors";
import axios from 'axios';
import pattern from 'patternomaly';


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
    axios.get('public/us_data.json').then(response => {
      this.usData = response.data['usStats'];
      this.usData = dataHelper.cleanseData(this.usData);

      const countyStats = dataHelper.getUSStats(this.usData, this.state, this.county);
      const countyChart = chartHelper.createBarChart(
        document.getElementById('covid-chart-county'),
        [
          // dataHelper.createConfirmedDataSet(countyStats, 'bar'),
          dataHelper.createConfirmedDataSet(countyStats, 'line'),
          // dataHelper.createDeathsDataSet(countyStats, 'bar'),
        ],
        countyStats.counts.map((count) => count.date),
        dataHelper.createTitle(this.state, this.county)
      );

      const stateStats = dataHelper.getUSStats(this.usData, this.state);
      const stateChart = chartHelper.createBarChart(
        document.getElementById('covid-chart-state'),
        [
          dataHelper.createConfirmedDataSet(stateStats, 'bar'),
          dataHelper.createDeathsDataSet(stateStats, 'bar'),
        ],
        stateStats.counts.map((count) => count.date),
        dataHelper.createTitle(this.state)
      );
    });
  },
  methods: {

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
