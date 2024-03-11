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
    <form
      role="search"
      class="f-search"
    >
      <input
        ref="searchInput"
        v-model="searchInput"
        :placeholder="$t('search.placeholder')"
        class="f-search__input f-search__input--card"
        type="text"
        @keydown.enter.prevent="performSearch()"
      >
      <button
        class="f-search__submit"
        @click.prevent="performSearch()"
      >
        <span class="sr-only">
          {{ $t('search.aria-label') }}
        </span>
      </button>
    </form>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  props: {
    query: {
      type: String,
      required: false,
      default: undefined
    }
  },
  data () {
    return {
      searchInput: this.query || ''
    }
  },
  computed: {
    ...mapState({
      profile: (state) => state.profile
    })
  },
  i18n: {
    messages: {
      pt: {
        header: 'Navegador de domínios',
        description: 'Navegue pelos domínios do Fenix',
        search: {
          'aria-label': 'Pesquisar...',
          placeholder: 'Pesquisar domínios',
          empty: 'Nenhum domínio encontrado'
        }
      },
      en: {
        header: 'Domain browser',
        description: 'Browse Fenix domains',
        search: {
          'aria-label': 'Search...',
          placeholder: 'Search domains',
          empty: 'No domains found'
        }
      }
    }
  },
  methods: {
    performSearch () {
      if (this.searchInput !== this.$route.query.q) {
        const location = { name: 'DomainObjectPage', params: { domainObjectId: this.searchInput } }
        this.$router.push(location)
      }
    },
    getDomainObjectName (domainObject) {
      return domainObject.type.split('.').pop()
    }
  }
}
</script>

<style lang="scss" scoped>
.field-separator {
  margin-bottom: 0;
}
</style>
