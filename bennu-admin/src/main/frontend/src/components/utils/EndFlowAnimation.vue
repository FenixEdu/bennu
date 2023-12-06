<template>
  <section
    class="flow__container"
    :class="`flow--${asyncState}`"
  >
    <!-- SVG for the animation states -->
    <div class="icon">
      <svg
        width="90"
        height="90"
        xmlns="http://www.w3.org/2000/svg"
      >
        <g
          fill="none"
          fill-rule="evenodd"
          stroke-width="2.6"
        >
          <g class="icon--loading">
            <circle
              class="icon__path--loading"
              cx="43"
              cy="43"
              r="43"
              stroke="#009DE0"
              transform="translate(2 2)"
            />
          </g>
          <g
            class="icon--success"
            stroke="#62D321"
            transform="translate(2 2)"
          >
            <circle
              class="icon__circle--success"
              cx="43"
              cy="43"
              r="43"
            />
            <path
              class="icon__check--success"
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M26 44.396222l14.157976 15.1611113L60.9555556 26"
            />
          </g>
          <g
            class="icon--error"
            stroke="#E9374F"
            transform="translate(2 2)"
          >
            <circle
              class="icon__circle--error"
              cx="42.9"
              cy="42.9"
              r="42.9"
            />
            <g
              stroke-linecap="round"
              stroke-linejoin="round"
              transform="translate(28.153125, 28.153125)"
            >
              <line
                class="icon__line--error"
                x1="0.526865625"
                y1="28.9668844"
                x2="28.9668844"
                y2="0.526865625"
              />
              <line
                class="icon__line--error"
                x1="0.526865625"
                y1="0.526865625"
                x2="28.9668844"
                y2="28.9668844"
              />
            </g>
          </g>
        </g>
      </svg>
    </div>
    <!-- Text content for each state -->
    <div
      class="flow__states"
      refs="state-container"
      :style="{ 'height': `${activeStateHeight}px` }"
    >
      <transition
        name="horizontal-slide"
        @enter="setHeight()"
      >
        <!-- Submitting -->
        <div
          v-if="asyncState === States.LOADING"
          :ref="States.LOADING"
          :key="States.LOADING"
          class="flow__states--loading"
        >
          <slot name="loading-message" />
        </div>
        <!-- Success -->
        <div
          v-else-if="asyncState === States.SUCCESS"
          :ref="States.SUCCESS"
          :key="States.SUCCESS"
          class="flow__states--success"
        >
          <slot name="success-message" />
        </div>
        <!-- Error -->
        <div
          v-else-if="asyncState === States.ERROR"
          :ref="States.ERROR"
          :key="States.ERROR"
          class="flow__states--error"
        >
          <slot name="error-message" />
        </div>
      </transition>
    </div>
  </section>
</template>
<script>
// TODO: look into a11y live areas

export const States = Object.freeze({
  LOADING: 'loading',
  SUCCESS: 'success',
  ERROR: 'error'
})

export default {
  props: {
    state: {
      validator: function (value) {
        return Object.values(States).includes(value)
      },
      required: true
    },
    minLoadingDelay: {
      type: Number,
      default: 800,
      validator: value => value >= 0
    }
  },
  data () {
    return {
      asyncState: this.state,
      activeStateHeight: undefined
    }
  },
  computed: {
    States () {
      return States
    }
  },
  watch: {
    state (value) {
      setTimeout(() => {
        this.asyncState = value
      }, this.minLoadingDelay)
    },
    '$i18n.locale' () {
      this.$nextTick(() => {
        this.setHeight()
      })
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.setHeight()
    })
  },
  methods: {
    setHeight (state = this.state) {
      const activeStateEl = this.$refs[state]
      const { height = 0 } = activeStateEl?.getBoundingClientRect() ?? {}
      if (height > 0) {
        this.activeStateHeight = height
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.flow__container {
  display: flex;
  flex-flow: column nowrap;
  align-items: center;
  text-align: center;
}

.flow__states {
  margin-top: 3rem;
  position: relative;
  width: 100%;
  overflow: hidden;
  height: auto;
  transition: height 1s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.horizontal-slide-leave-active,
.horizontal-slide-enter-active {
  transition: all 1s cubic-bezier(0.645, 0.045, 0.355, 1);
}
.horizontal-slide-enter-active {
  position: absolute;
  top: 0;
}
.horizontal-slide-enter {
  transform: translateX(100%);
  opacity: 0;
}
.horizontal-slide-enter-to {
  opacity: 1;
}
.horizontal-slide-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}

.icon > svg {
  stroke-width: 2.6;
  width: 90px;
  height: auto;
}

/* Loading */

.flow--loading {
  transition: all 3s cubic-bezier(0.445, 0.05, 0.55, 0.95);
  transition-delay: 1s;
  opacity: 1;

  .icon--loading {
    animation: icon-loading 3s linear infinite;
    transform-origin: 45px 45px;
  }
}

.flow--error .icon--loading,
.flow--success .icon--loading {
  transition: all 1s cubic-bezier(0.445, 0.05, 0.55, 0.95);
  opacity: 0;
}

.icon__path--loading {
  stroke-dasharray: 1, 200;
  stroke-dashoffset: 0;
  animation: icon-loading-path 2s ease-in-out infinite, color 6s ease-in-out infinite;
  stroke-linecap: round;
}

@keyframes icon-loading {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes icon-loading-path {
  0% {
    stroke-dasharray: 1, 520;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 200, 300;
    stroke-dashoffset: -50;
  }
  100% {
    stroke-dasharray: 250, 520;
    stroke-dashoffset: -270;
  }
}

/* Success */

.flow--success,
.flow--error {
  transform-origin: 43px 43px;
}

.icon__circle--success {
  stroke-dasharray: 270px;
  stroke-dashoffset: 270px;
  transform: rotate(-360deg);
  transition: all 1.5s cubic-bezier(0.645, 0.045, 0.355, 1);
  transform-origin: 43px 43px;
}

.flow--success .icon__circle--success {
  stroke-dashoffset: 0;
  transform: rotate(0deg);
}

.icon__check--success {
  stroke-dasharray: 62px;
  stroke-dashoffset: 62px;
  transition: all 0.6s cubic-bezier(0.445, 0.05, 0.55, 0.95);
}

.flow--success .icon__check--success {
  stroke-dashoffset: 0;
  transition: all 1s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  transition-delay: 0.5s;
}

/* Error */

.icon--error {
  transform-origin: 43px 43px;
}

.icon__circle--error {
  stroke-dasharray: 270px;
  stroke-dashoffset: 270px;
  transform: rotate(-360deg);
  transition: all 1.5s cubic-bezier(0.645, 0.045, 0.355, 1);
  transform-origin: 43px 43px;
}

.flow--error .icon__circle--error {
  stroke-dashoffset: 0;
  transform: rotate(0deg);
}

.icon__line--error {
  transition: all 0.6s cubic-bezier(0.445, 0.05, 0.55, 0.95);
}

.icon__line--error:nth-of-type(1) {
  stroke-dasharray: 42px;
  stroke-dashoffset: 42px;
}

.flow--error .icon__line--error {
  transition: all 1s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.flow--error .icon__line--error:nth-of-type(1) {
  stroke-dasharray: 42px;
  stroke-dashoffset: 84px;
  transition-delay: 0.5s;
}

.icon__line--error:nth-of-type(2) {
  stroke-dasharray: 42px;
  stroke-dashoffset: 126px;
}

.flow--error .icon__line--error:nth-of-type(2) {
  stroke-dasharray: 42px;
  stroke-dashoffset: 84px;
  transition-delay: 0.75s;
}
</style>
