<template>
  <div
    class="card"
    :class="{ 'card--modal': type === 'modal' }"
  >
    <div class="card-row">
      <div class="card-row__text">
        <header>
          <div class="logo">
            <svg
              viewBox="0 0 200 46"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g
                fill="none"
                fill-rule="evenodd"
              >
                <path
                  d="M29.265 14.683h-1.59v11.845H24.23V14.683h-1.587v-2.376h6.62v2.376zm-8.478-6.148v21.77c0 2.823-2.31 4.284-5.155 4.284-2.846 0-5.174-1.234-5.174-3.63h.003c0-.94.77-1.703 1.72-1.703.948 0 1.717.763 1.717 1.703h.002c0 1.74.14 2.92 1.732 2.92 1.712 0 1.712-1.185 1.712-3.574l.002-21.77c0-2.82 2.307-4.282 5.153-4.282 2.845 0 5.173 1.23 5.173 3.628h-.002c0 .94-.77 1.703-1.72 1.703-.947 0-1.716-.762-1.716-1.703h-.002c0-1.74-.14-2.92-1.733-2.92-1.714 0-1.713 1.184-1.713 3.573zm-10.323 3.772h3.43L13.9 26.52h-3.44l.004-14.213zM0 .23v19.21C0 36.735 19.062 46 19.062 46s19.133-9.265 19.133-26.56V.23H0z"
                  fill="#009DE0"
                  class="graphic"
                />
                <g
                  fill="#46555F"
                  class="text"
                >
                  <path d="M55.506 14.714h-4.06V12.35h10.87v2.364h-4.06v11.824h-2.75V14.714M67.208 9.986c1.026-.62 2.03-1.236 2.904-1.896l1.026 1.576c-.982.703-1.79 1.17-2.97 1.768l-.96-1.448zm-3.034 2.364h8.906v2.364h-6.156v3.43H72.6v2.365h-5.676v3.664h6.156v2.364h-8.906V12.35zM75.265 16.25c0-2.6 1.55-4.113 5.174-4.113 1.353 0 2.75.17 3.95.47l-.305 2.32c-1.18-.213-2.554-.36-3.56-.36-1.898 0-2.51.638-2.51 2.15v5.455c0 1.512.612 2.15 2.51 2.15 1.006 0 2.38-.148 3.56-.362l.306 2.322c-1.2.3-2.597.47-3.95.47-3.625 0-5.175-1.513-5.175-4.113v-6.39M89.368 15.78v10.758h-2.685V12.35h3.668l5.524 10.78V12.35h2.686v14.188h-3.668L89.368 15.78M101.617 12.35h2.75v14.188h-2.75V12.35zM107.098 16.25c0-2.6 1.55-4.113 5.173-4.113 1.355 0 2.753.17 3.954.47l-.306 2.32c-1.18-.213-2.554-.36-3.56-.36-1.898 0-2.51.638-2.51 2.15v5.455c0 1.512.612 2.15 2.51 2.15 1.006 0 2.38-.148 3.56-.362l.306 2.322c-1.2.3-2.6.47-3.953.47-3.622 0-5.172-1.513-5.172-4.113v-6.39M120.612 22.235c0 1.342.7 2.15 2.75 2.15 2.053 0 2.75-.808 2.75-2.15v-5.582c0-1.34-.697-2.15-2.75-2.15-2.05 0-2.75.81-2.75 2.15v5.582zm8.252.15c0 2.364-1.397 4.367-5.502 4.367-4.104 0-5.5-2.003-5.5-4.368v-5.88c0-2.365 1.396-4.367 5.5-4.367 4.105 0 5.502 2.002 5.502 4.368v5.88zM137.576 25.41h6.55v1.128h-7.838V12.35h1.288v13.06M147.052 12.35h1.288v14.188h-1.288V12.35zM156.025 26.752c-1.266 0-2.86-.214-4.104-.683l.24-1.024c1.31.384 2.642.576 3.91.576 2.487 0 3.164-.724 3.164-2.535 0-2.194-.02-2.43-3.252-3.153-3.516-.788-3.908-1.406-3.908-4.26 0-2.323.872-3.537 4.518-3.537 1.267 0 2.773.213 3.756.49l-.153 1.066c-1.07-.235-2.424-.426-3.56-.426-2.88 0-3.273.68-3.273 2.45 0 2.3.065 2.406 3.122 3.152 3.82.937 4.04 1.47 4.04 4.154 0 2.32-.81 3.728-4.5 3.728M171.744 21.83c0-1.193-.72-2.003-2.4-2.003h-4.062v5.603h4.432c1.376 0 2.03-.66 2.03-1.79v-1.81zm-.458-6.86c0-.98-.568-1.512-1.812-1.512h-4.192v5.263h3.755c1.594 0 2.25-.81 2.25-1.94v-1.81zm-7.292 11.568V12.35h5.458c1.965 0 3.122.917 3.122 2.79v1.705c0 1.13-.48 1.876-1.66 2.3 1.136.15 2.12 1.067 2.12 2.643v1.852c0 1.96-1.464 2.898-3.342 2.898h-5.698zM184.626 22.49v-6.092c0-2.024-1.004-3.13-3.8-3.13-2.794 0-3.798 1.106-3.798 3.13v6.092c0 2.025 1.004 3.132 3.8 3.132 2.794 0 3.798-1.107 3.798-3.132zm-3.8 4.262c-3.732 0-5.086-1.79-5.086-4.218v-6.18c0-2.428 1.354-4.217 5.087-4.217 3.734 0 5.087 1.79 5.087 4.218v6.18c0 2.427-1.353 4.217-5.087 4.217zM190.98 21.724h5.98l-2.99-8.65-2.99 8.65zm6.374 1.086h-6.767l-1.354 3.728h-1.267l5.066-14.188h1.9l5.064 14.188h-1.266l-1.376-3.728z" />
                </g>
              </g>
            </svg>
          </div>
          <template v-if="hasHeader">
            <back-arrow-button
              v-if="withBackButton"
              :to="backTo"
              :label="backButtonLabel"
              @back="$emit('back')"
            />
            <div v-if="!!$slots['pre-icon']">
              <slot name="pre-icon" />
            </div>
            <div
              v-if="!!$slots['icon']"
              class="icon-container"
            >
              <slot name="icon" />
            </div>
            <div :class="{ 'header--centered': layout === 'centered' }">
              <h1
                v-if="!!$slots['title']"
                class="card-row__title h3"
              >
                <slot name="title" />
              </h1>
              <slot name="description" />
            </div>
          </template>
        </header>
        <section
          v-if="!!$slots['card-content']"
          class="card-content"
        >
          <slot name="card-content" />
        </section>
      </div>
    </div>
    <section
      v-if="!!$slots['card-section']"
      class="card-row card-row--max-height"
    >
      <slot name="card-section" />
    </section>
    <footer
      v-if="!!$slots['footer']"
      class="card-row"
    >
      <div class="card-row__text">
        <slot name="footer" />
      </div>
    </footer>
  </div>
</template>

<script>
import BackArrowButton from '@/components/utils/BackArrowButton.vue'

export default {
  name: 'CardLayoutWrapper',
  components: {
    BackArrowButton
  },
  props: {
    withBackButton: {
      type: Boolean,
      default: false
    },
    backTo: {
      type: Object,
      default: () => {}
    },
    backButtonLabel: {
      type: String,
      default: undefined
    },
    layout: {
      type: String,
      default: 'default',
      validator: (string) => {
        return ['default', 'centered'].includes(string)
      }
    },
    type: {
      type: String,
      default: 'default',
      validator: (string) => {
        return ['default', 'modal'].includes(string)
      }
    }
  },
  computed: {
    hasHeader () {
      return this.withBackButton || !!this.$slots.title || !!this.$slots.icon
    }
  }
}
</script>

<style lang="scss" scoped>
.card-row {
  padding: 40px;
}

.card--modal {
  margin: auto;

  @media screen and (min-width: 32rem) {
    width: 28.125rem;
  }
}

.logo {
  width: 40%;
  margin-bottom: 56px;
}

.icon-container {
  display: flex;
  justify-content: center;
  margin-bottom: 1rem;
}

.card-row__title {
  display: block;
}

.card-row__title + * {
  margin-top: 0.5rem;
}

.header--centered * {
  text-align: center;
}

.card-content {
  margin-top: 2rem;
}

.card-row--max-height {
  @media screen and (min-width: 32rem) {
    max-height: 16.5rem;
    overflow-y: auto;
    align-items: flex-start;

    & > * {
      align-self: flex-start;
    }
  }
}

.footer {
  margin-top: 2rem;

  .footer__small-text {
    font-size: 0.875rem;
  }

  .u-active-link {
    cursor: pointer;
  }
}
</style>
