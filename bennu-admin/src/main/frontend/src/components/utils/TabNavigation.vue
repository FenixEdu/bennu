<template>
  <div>
    <div class="tab-navigation">
      <div
        v-if="totalTabs > 0"
        class="tab-navigation__list"
        role="tablist"
        :aria-label="label"
        aria-orientation="horizontal"
      >
        <template v-for="n in totalTabs">
          <div
            v-if="!!$scopedSlots[`tab-${n - 1}`]"
            :key="n"
            ref="tabs"
            class="tab-navigation__list-item"
            :class="{ 'tab-navigation__list-item--selected': isCurrentTab(n - 1) }"
          >
            <slot
              :name="`tab-${n - 1}`"
              :index="n - 1"
              :attrs="{
                role: 'tab',
                class: 'tab-navigation__link',
                'aria-selected': String(isCurrentTab(n - 1)),
                tabindex: isCurrentTab(n - 1) ? '0' : '-1'
              }"
              :events="{
                keydown: ($event) => handleKeydown($event),
                click: () => currentTab = n - 1
              }"
            />
          </div>
        </template>
      </div>
      <template v-else>
        <slot name="main" />
      </template>
      <div
        v-if="!!$slots['options']"
        class="tab-navigation__options"
      >
        <slot name="options" />
      </div>
    </div>
    <template v-if="!!$slots['sliding-options']">
      <transition name="slide">
        <div
          v-show="showSlidingOptions"
          id="sliding-options"
          ref="sliding-options"
          class="card tab-navigation__sliding-options"
        >
          <slot name="sliding-options" />
          <button
            class="skip-link btn--primary btn--outline btn--sm tab-navigation__btn-close-sliding-options"
            @click="showSlidingOptions = false"
          >
            {{ $t('sliding-options.actions.close') }}
          </button>
        </div>
      </transition>
    </template>
  </div>
</template>

<script>
import { createFocusTrap } from 'focus-trap'

export default {
  name: 'TabNavigation',
  props: {
    value: {
      type: Number,
      default: 0
    },
    label: {
      type: String,
      default: undefined
    },
    totalTabs: {
      type: Number,
      required: true
    },
    closeSlidingOptionsOnTabChange: {
      type: Boolean,
      default: true
    },
    slidingOptionsExpanded: {
      type: Boolean,
      default: false
    }
  },
  i18n: {
    messages: {
      pt: {
        'sliding-options': {
          actions: {
            close: 'Fechar menu'
          }
        }
      },
      en: {
        'sliding-options': {
          actions: {
            close: 'Close menu'
          }
        }
      }
    }
  },
  data () {
    return {
      /** @type {import('focus-trap').FocusTrap} */
      slidingOptionsTrap: undefined
    }
  },
  computed: {
    currentTab: {
      get () {
        return this.value
      },
      set (val) {
        if (this.$slots['sliding-options'] && this.closeSlidingOptionsOnTabChange) {
          this.slidingOptionsTrap.deactivate()
          this.showSlidingOptions = false // in case the trap was not active before
        }
        this.$emit('input', val)
      }
    },
    showSlidingOptions: {
      get () {
        return this.slidingOptionsExpanded
      },
      set (val) {
        this.$emit('update:slidingOptionsExpanded', val)
      }
    }
  },
  watch: {
    showSlidingOptions (newValue) {
      if (newValue === true) {
        this.$nextTick(() => {
          this.slidingOptionsTrap.activate()
        })
      } else {
        this.slidingOptionsTrap.deactivate()
      }
    }
  },
  mounted () {
    if (this.$slots['sliding-options']) {
      this.slidingOptionsTrap = createFocusTrap('#sliding-options', {
        clickOutsideDeactivates: false, // we will handle outside click ourselves
        allowOutsideClick: true,
        escapeDeactivates: true,
        returnFocusOnDeactivate: true,
        onDeactivate: () => {
          this.showSlidingOptions = false
        }
      })
      document.addEventListener('mousedown', this.onPointerClick, true)
      document.addEventListener('touchstart', this.onPointerClick, true)
      document.addEventListener('click', this.onPointerClick, true)
    }
  },
  beforeDestroy () {
    if (this.$slots['sliding-options']) {
      this.slidingOptionsTrap.deactivate()
      document.removeEventListener('mousedown', this.onPointerClick, true)
      document.removeEventListener('touchstart', this.onPointerClick, true)
      document.removeEventListener('click', this.onPointerClick, true)
    }
  },
  methods: {
    onPointerClick (event) {
      const slidingOptions = this.$refs['sliding-options']
      if (this.showSlidingOptions && !slidingOptions?.contains(event.target)) {
        this.slidingOptionsTrap.pause()
      }
    },
    isCurrentTab (index) {
      return this.currentTab === index
    },
    /**
     * @param {KeyboardEvent} event
     */
    handleKeydown (event) {
      if (event.altKey || event.ctrlKey || event.metaKey) {
        return
      }
      if (event.key === 'ArrowLeft') {
        event.preventDefault()
        this.focusOnPreviousTab(event.target)
      } else if (event.key === 'ArrowRight') {
        event.preventDefault()
        this.focusOnNextTab(event.target)
      }
    },
    focusOnNextTab (tabEl) {
      const tabs = this.$refs.tabs.map(el => el.firstChild)
      const tabIndex = tabs.findIndex(el => el === tabEl)
      const nextIndex = tabIndex === tabs.length - 1 ? 0 : tabIndex + 1
      const nextTabEl = tabs[nextIndex]
      nextTabEl.focus()
    },
    focusOnPreviousTab (tabEl) {
      const tabs = this.$refs.tabs.map(el => el.firstChild)
      const tabIndex = tabs.findIndex(el => el === tabEl)
      const prevIndex = tabIndex === 0 ? tabs.length - 1 : tabIndex - 1
      const prevTabEl = tabs[prevIndex]
      prevTabEl.focus()
    }
  }
}
</script>
<style lang="scss" scoped>
// TODO: Move slide transition to _helpers.scss ?
.slide-enter-active,
.slide-leave-active {
  transition: all 0.5s cubic-bezier(0.645, 0.045, 0.355, 1);
  overflow: hidden;
}

.slide-enter,
.slide-leave-to {
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
  margin-bottom: 0;
}

.slide-leave,
.slide-enter-to {
  max-height: 100vh;
  margin-bottom: 1rem;
}

.tab-navigation__sliding-options {
  position: relative;
  margin-top: -0.5rem;
  margin-bottom: 3rem;
}
.tab-navigation__btn-close-sliding-options {
  top: 1rem;
  right: 1rem;
}
</style>
