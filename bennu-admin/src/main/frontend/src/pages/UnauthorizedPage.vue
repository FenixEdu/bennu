<template>
  <main class="layout-centered">
    <h1>{{ $t('header') }}</h1>
    <p class="u-text-multiline">
      {{ description }}
    </p>
  </main>
</template>

<script>
import TranslateApiString from '@/mixins/TranslateApiString'

export default {
  name: 'UnauthorizedPage',
  mixins: [
    TranslateApiString
  ],
  props: {
    errorKey: {
      type: String,
      default: undefined
    },
    errorMessage: {
      type: String,
      default: undefined
    }
  },
  computed: {
    description () {
      if (this.errorMessage) {
        return this.translate(this.errorMessage)
      } else if (this.errorKey) {
        return this.$t('description-with-error-key', { errorKey: this.errorKey })
      } else {
        return this.$t('description')
      }
    }
  },
  i18n: {
    messages: {
      pt: {
        header: 'Utilizador não autorizado',
        description: 'De momento, não tem acesso a este serviço',
        'description-with-error-key': '@:(description)\nID: {errorKey}'
      },
      en: {
        header: 'Unauthorized user',
        description: 'You do not have access to this service',
        'description-with-error-key': '@:(description)\nID: {errorKey}'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-centered {
  position: absolute;
  left: 50%;
  top: 6rem;
  transform: translate(-50%, 0);
  text-align: center;
  width: 100%;
  max-width: 46rem;
  padding: 1rem;
  @media screen and (min-width: 47.5rem) {
    top: 50%;
    transform: translate(-50%, -50%);
  }
}
</style>
