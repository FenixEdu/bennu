<template>
  <div
    id="app"
    ref="app"
    tabindex="-1"
    class="page-with-card u-a11y-focusable"
  >
    <button
      class="skip-link"
      @click.prevent="() => $refs.main.focus()"
    >
      {{ $t('actions.skip-to-content') }}
    </button>
    <progress-bar class="page-with-card__progress-bar" />
    <feedback-top-bar />
    <main
      ref="main"
      tabindex="-1"
      class="page-with-card__main main-content u-a11y-focusable"
    >
      <div class="card-container">
        <slot />
        <div class="language-toggle-container">
          <button
            v-if="$i18n.locale === 'pt'"
            class="language-toggle small"
            @click="setLocale('en', $auth)"
          >
            English
          </button>
          <button
            v-if="$i18n.locale === 'en'"
            class="language-toggle small"
            @click="setLocale('pt', $auth)"
          >
            Português
          </button>
        </div>
      </div>
    </main>
    <div class="page-with-card__footer">
      <footer-compact compact-line />
    </div>
  </div>
</template>

<script>
import FooterCompact from '@/components/utils/FooterCompact.vue'
import FeedbackTopBar from '@/components/utils/FeedbackTopBar.vue'
import ProgressBar from '@/components/ProgressBar.vue'
import { shouldResetPageFocus } from '@/layouts/util'

export default {
  components: {
    FooterCompact,
    ProgressBar,
    FeedbackTopBar
  },
  watch: {
    $route (to, from) {
      if (shouldResetPageFocus(to, from)) {
        this.$nextTick(() => {
          this.$refs.app.focus()
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page-with-card {
  display: flex;
  flex-flow: column nowrap;
  min-height: 100vh;

  .page-with-card__progress-bar {
    top: 0;
  }

  .page-with-card__main {
    display: flex;
    flex-flow: column nowrap;
    align-items: center;
    margin: auto;
    padding: 1rem;
    min-height: unset;

    @media screen and (min-width: 32rem) {
      padding: 3.825rem 0;
      min-height: 50rem;
    }
  }
  .page-with-card__footer {
    margin-top: auto;
    display: flex;
    justify-content: center;
  }
  .card-container {
    width: 100%;
    @media screen and (min-width: 32rem) {
      width: 28.125rem;
    }
    .language-toggle-container {
      margin-top: 8px;
      display: flex;
      justify-content: flex-end;
      height: 1rem;
    }
  }
}

</style>
