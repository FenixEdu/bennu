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

    <div style="margin-top: 2.5rem;">
      <div
        v-if="domainClasses.length > 0"
        class="card"
      >
        <table class="table">
          <thead>
            <tr>
              <th>{{ $t('table.class-name') }}</th>
              <th>{{ $t('table.class-type') }}</th>
              <th />
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="domainClass in domainClasses"
              :key="domainClass.fullClassName"
              class="table__row"
            >
              <td class="table__cell">
                <span class="u-text-strong">{{ domainClass.className }}</span>
              </td>
              <td class="table__cell">
                <p>{{ domainClass.fullClassName }}</p>
              </td>
              <td class="table__cell table__cell--right">
                <div class="f-field--radio">
                  <input
                    :id="`selected-class-${domainClass.fullClassName}`"
                    type="radio"
                    class="f-field__input"
                    :checked="selectedClass === domainClass.fullClassName"
                    @change="selectedClass = domainClass.fullClassName"
                  >
                  <label
                    :for="`selected-class-${domainClass.fullClassName}`"
                    class="f-field__label"
                  >
                    <span class="sr-only">
                      {{ $t('radio-label', { class: domainClass.fullClassName }) }}
                    </span>
                  </label>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else>
        <empty-state>
          <template #title>
            {{ hasSearchQuery ? $t('empty-state.no-classes-query', { query }) : $t('empty-state.no-classes') }}
          </template>
        </empty-state>
      </div>
    </div>

    <div class="footer">
      <pagination
        :total-items="totalItems"
        :items-per-page="perPage"
        :current-page="page"
      />

      <button
        :disabled="!selectedClass"
        class="btn btn--primary"
      >
        {{ $t('create') }}
      </button>
    </div>
  </div>
</template>

<script>
import { guardWithErrorHandling } from '@/router/guards'
import Pagination from '@/components/utils/Pagination.vue'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'CreateDomainObjectPage',
  components: {
    Pagination,
    EmptyState
  },
  beforeRouteUpdate: guardWithErrorHandling(async function (to, from, next) {
    this.$progress.set(10)
    await to.meta.beforeRouteLoad(to, from)
    this.searchInput = to.query.q || ''
    this.$progress.complete()
    next()
  }),
  props: {
    query: {
      type: String,
      required: false,
      default: undefined
    },
    domainClasses: {
      type: Array,
      required: true
    },
    page: {
      type: Number,
      required: true
    },
    perPage: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      searchInput: this.query || '',
      selectedClass: undefined
    }
  },
  computed: {
    hasSearchQuery () {
      return this.query !== undefined && this.query !== ''
    }
  },
  methods: {
    performSearch () {
      if (this.searchInput !== this.$route.query.q) {
        const location = { query: { q: this.searchInput === '' ? undefined : this.searchInput } }
        if (this.$route.query.q === undefined) {
          this.$router.replace(location)
        } else {
          this.$router.push(location)
        }
      }
    }
  },
  i18n: {
    messages: {
      pt: {
        header: 'Criar objeto de domínio',
        description: 'Crie um novo objeto de domínio',
        search: {
          'aria-label': 'Pesquisar...',
          placeholder: 'Pesquisar tipos de objeto de domínio'
        },
        table: {
          'class-name': 'Nome da classe',
          'class-type': 'Tipo da classe'
        },
        'empty-state': {
          'no-classes': 'Nenhuma classe encontrada',
          'no-classes-query': 'Nenhuma classe encontrada para o termo "{query}".'
        },
        create: 'Criar'
      },
      en: {
        header: 'Create domain object',
        description: 'Create a new domain object',
        search: {
          'aria-label': 'Search...',
          placeholder: 'Search domain object types'
        },
        table: {
          'class-name': 'Class name',
          'class-type': 'Class type'
        },
        'empty-state': {
          'no-classes': 'No classes found',
          'no-classes-query': 'No classes found for the search query "{query}".'
        },
        create: 'Create'
      }
    }
  }
}
</script>

<style lang="scss">
.table__cell--right {
  text-align: right;
  padding-right: 0;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 3.5rem;

  .page-nav {
    margin-top: 0;
  }
}
</style>
