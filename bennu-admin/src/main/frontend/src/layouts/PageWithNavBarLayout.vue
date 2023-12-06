<template>
  <div
    id="app"
    ref="app"
    tabindex="-1"
    :class="{ 'nav-is-visible': showSideMenu }"
    class="page-width-nav-bar-layout u-a11y-focusable"
  >
    <button
      class="skip-link"
      @click.prevent="() => $refs.main.focus()"
    >
      {{ $t('actions.skip-to-content') }}
    </button>
    <top-nav-bar
      @toggle-mobile-menu="toggleMobileMenu"
    />
    <progress-bar />
    <feedback-top-bar />
    <main
      ref="main"
      tabindex="-1"
      class="page-width-nav-bar-layout__main-content main-content u-a11y-focusable"
    >
      <slot />
    </main>
    <div
      class="page-width-nav-bar-layout__overlay"
      aria-hidden="true"
    />
  </div>
</template>

<script>
import TopNavBar from '@/components/TopNavBar.vue'
import FeedbackTopBar from '@/components/utils/FeedbackTopBar.vue'
import ProgressBar from '@/components/ProgressBar.vue'
import { shouldResetPageFocus } from '@/layouts/util'

export default {
  components: {
    TopNavBar,
    FeedbackTopBar,
    ProgressBar
  },
  data () {
    return {
      showSideMenu: false
    }
  },
  watch: {
    $route (to, from) {
      if (shouldResetPageFocus(to, from)) {
        this.$nextTick(() => {
          this.$refs.app.focus()
        })
      }
    }
  },
  methods: {
    toggleMobileMenu (show) {
      this.showSideMenu = show
    }
  }
}
</script>
<style lang="scss">
@import "@/assets/scss/variables";

.main-header,
.main-content {
  transition: transform 600ms cubic-bezier(0.77, 0, 0.175, 1);

  .page-width-nav-bar-layout.nav-is-visible & {
    transform: translateX(-#{$menu-mobile--width});
  }
}
.page-width-nav-bar-layout {
  display: flex;
  flex-flow: column nowrap;
  min-height: 50rem;
  justify-content: stretch;
  @media screen and (min-height: 50rem), (min-width: 37.5rem) {
    min-height: 100vh;
  }
}
.page-width-nav-bar-layout__main-content {
  flex-grow: 1;
  background-color: white;
  display: flex;
  flex-flow: column nowrap;
}
.page-width-nav-bar-layout.nav-is-visible .page-width-nav-bar-layout__overlay {
  opacity: 1;
  visibility: visible;
}
.page-width-nav-bar-layout__overlay {
  position: fixed;
  z-index: 1;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  cursor: pointer;
  background-color: rgb(46 50 66 / 30%);
  visibility: hidden;
  opacity: 0;
  backface-visibility: hidden;
  transition:
    opacity 600ms cubic-bezier(0.77, 0, 0.175, 1),
    visibility 600ms cubic-bezier(0.77, 0, 0.175, 1);
}
.main-content {
  padding-top: $header-height;
}
</style>
