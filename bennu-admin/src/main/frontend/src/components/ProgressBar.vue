<template>
  <div
    role="progressbar"
    :style="`width: ${barWidth}%;`"
    class="progress-bar"
    :class="{ 'progress-bar--hidden': !showBar }"
    aria-valuemin="0"
    aria-valuemax="100"
    :aria-valuenow="showBar ? barWidth : 100"
    :aria-valuetext="$t('aria-text')"
  />
</template>

<script>
import { mapState, mapActions } from 'vuex'

const sleep = (ms) => new Promise(resolve => {
  setTimeout(resolve, ms)
})

export default {
  name: 'ProgressBar',
  i18n: {
    messages: {
      pt: {
        'aria-text': 'A carregar a página…'
      },
      en: {
        'aria-text': 'Loading the page…'
      }
    }
  },
  data () {
    return {
      showBar: false,
      barWidth: 0
    }
  },
  computed: {
    ...mapState(['progressBar'])
  },
  watch: {
    async progressBar (progress) {
      if (progress === 0) {
        this.showBar = false
        await sleep(100)
        this.barWidth = 0
      } else if (progress === 100) {
        this.barWidth = 100
        await sleep(300)
        this.setProgressBar(0)
      } else {
        this.showBar = true
        await this.$nextTick()
        this.barWidth = progress
      }
    }
  },
  methods: {
    ...mapActions(['setProgressBar'])
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";
.progress-bar {
  height: 3px;
  position: fixed;
  top: $header-height;
  left: 0;
  transition: width 0.3s cubic-bezier(0.65, 0, 0.35, 1);
  opacity: 1;
  overflow: hidden;
  background: $blue;
  z-index: 2;
}

.progress-bar--hidden {
  transition: none;
}

.progress-bar::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to left, transparent 0%, rgb(255 255 255 / 70%) 50%, transparent 100%);
  animation: progress-bar-gleam 2s cubic-bezier(0.65, 0, 0.35, 1) infinite;
}

@keyframes progress-bar-gleam {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style>
