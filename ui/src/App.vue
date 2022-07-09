<template>
  <div id="app">
    <section class="p-4">
      <div>
        <b-field label="Number of weeks">
          <b-slider tooltip-always v-model="numWeeks" :min="1" :max="10" ticks></b-slider>
        </b-field>
      </div>
    </section>

    <div class="chart">
      <canvas id="covid-chart-state-new-confirmed"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-state-new-deaths"></canvas>
    </div>


    <div class="chart">
      <canvas id="covid-chart-county-new-confirmed"></canvas>
    </div>
    <div class="chart">
      <canvas id="covid-chart-county-new-deaths"></canvas>
    </div>


<!--    <div class="chart">-->
<!--      <canvas id="covid-chart-state-confirmed"></canvas>-->
<!--    </div>-->
<!--    <div class="chart">-->
<!--      <canvas id="covid-chart-state-deaths"></canvas>-->
<!--    </div>-->

<!--    <div class="chart">-->
<!--      <canvas id="covid-chart-county-confirmed"></canvas>-->
<!--    </div>-->
<!--    <div class="chart">-->
<!--      <canvas id="covid-chart-county-deaths"></canvas>-->
<!--    </div>-->

  </div>
</template>

<script>
import chartHelper from "./chartHelper";
import dataHelper from "./dataHelper";
import axios from 'axios';

//const BASE_URL = 'https://s3.us-east-2.amazonaws.com/akang.info/covid';
const BASE_URL = 'public';

export default {
  name: 'app',
  data() {
    return {
      usData: {},
      state: 'Illinois',
      county: 'Cook',
      numWeeks: 3,
      apiCallFinished: false,
      chart1: null,
      chart2: null,
      chart3: null,
      chart4: null,
    }
  },
  mounted() {
    const isSmall = window.matchMedia('(max-device-width: 480px)').matches;
    this.numWeeks = isSmall ? 3: 6;

    axios.get(`${BASE_URL}/illinois_data.json`).then(response => {
      this.usData = response.data['usStats'];
      this.usData = dataHelper.cleanseData(this.usData);
      this.apiCallFinished = true;
      this.updateCharts(this.numWeeks);
    });
  },
  watch: {
    numWeeks: function(newVal, oldVal) {
      if (this.apiCallFinished) {
        this.updateCharts(newVal);
      }
    },
  },
  methods: {
    updateCharts: function (numWeeks) {
      let stats = dataHelper.getUSStats(this.usData, this.state, this.county, numWeeks);

      this.chart1 && this.chart1.destroy();
      this.chart1 = this.createChart(
        'covid-chart-county-new-confirmed',
        stats,
        [dataHelper.createNewConfirmedDataSet(stats, 'line')],
        `${this.county} county confirmed`,
      );

      this.chart2 && this.chart2.destroy();
      this.chart2 = this.createChart(
        'covid-chart-county-new-deaths',
        stats,
        [dataHelper.createNewDeathsDataSet(stats, 'line')],
        `${this.county} county deaths`,
      );

      stats = dataHelper.getUSStats(this.usData, this.state, null, this.numWeeks);

      this.chart3 && this.chart3.destroy();
      this.chart3 = this.createChart(
        'covid-chart-state-new-confirmed',
        stats,
        [dataHelper.createNewConfirmedDataSet(stats, 'line')],
        `${this.state} confirmed`,
      );

      this.chart4 && this.chart4.destroy();
      this.chart4 = this.createChart(
        'covid-chart-state-new-deaths',
        stats,
        [dataHelper.createNewDeathsDataSet(stats, 'line')],
        `${this.state} deaths`,
      );


      // this.createChart(
      //   'covid-chart-county-confirmed',
      //   stats,
      //   [dataHelper.createConfirmedDataSet(stats, 'line')],
      //   `${this.county} accumulated county confirmed`,
      // );
      // this.createChart(
      //   'covid-chart-county-deaths',
      //   stats,
      //   [dataHelper.createDeathsDataSet(stats, 'line')],
      //   `${this.county} accumulated county deaths`,
      // );
      // this.createChart(
      //   'covid-chart-state-confirmed',
      //   stats,
      //   [dataHelper.createConfirmedDataSet(stats, 'line')],
      //   `${this.state} accumulated confirmed`,
      // );
      // this.createChart(
      //   'covid-chart-state-deaths',
      //   stats,
      //   [dataHelper.createDeathsDataSet(stats, 'line')],
      //   `${this.state} accumulated deaths`,
      // );
    },
    createChart: (elementName, stats, dataSets, title) => {
      return chartHelper.createChart(
        document.getElementById(elementName),
        dataSets,
        stats.counts.map((count) => count.dt),
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
    width: 100%;
  }

  @media only screen and (max-device-width: 480px) {
    .chart {
      width: 100%;
    }
  }

</style>
