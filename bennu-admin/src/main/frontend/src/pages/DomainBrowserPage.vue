<template>
  <div class="page">
    <header class="section-header page__header">
      <div class="section-header__text">
        <h1 class="section-header__title">
          {{ $t('header') }}
        </h1>
        <p>{{ $t('description') }}</p>
      </div>
    </header>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  computed: {
    ...mapState({
      profile: (state) => state.profile
    })
  },
  i18n: {
    messages: {
      pt: {},
      en: {}
    }
  }
}
</script>
